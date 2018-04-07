package com.mike.sim;

import com.mike.routing.AnnealData;
import com.mike.routing.Stop;
import com.mike.util.Location;
import com.mike.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mike.sim.TripInfo.State.Arrived;
import static com.mike.sim.TripInfo.State.NotStarted;

/**
 * Created by mike on 8/20/2016.
 */
public class TripInfo {

    private static final String TAG = TripInfo.class.getSimpleName();

    private double cost;
    private double distance;
    private long endTime;
    private long startTime;
    private AnnealData annealer;


    // which of the route's stops is where we are going now
    private int nextStop = 0;

    public List<Order> getOrders() {
        List<Order> orders = new ArrayList<>();
        annealer.getStops().forEach(stop -> orders.addAll(stop.getOrders()));
        return orders;
    }

    // as we travel from stop to stop in a route this
    // records the current state
    public enum State {NotStarted, Enroute, Arrived };
    private State state = State.NotStarted;

    public State getState() {
        return state;
    }

    // the stuff we have in the truck at any point in time
    private List<Order> inTruck = new ArrayList<>();

    private Transporter transporter;

    public TripInfo(Transporter transporter, List<Order> orders) {
        cost = 0;
        distance = 0;
        endTime = 0;
        this.transporter = transporter;

        startTime = Clock.getTime();

        annealer = new AnnealData();
        annealer.setupRun(orders);
        annealer.anneal();
    }

    public double getTripLength() {
        return distance;
    }
    public long getTripTime() {
        return endTime - startTime;
    }
    public long getTripStartTime() {
        return startTime;
    }
    public double getCost() {
        return annealer.getRoute().getMetrics().getCost();
    }
    public Transporter getTransporter() { return transporter; }

    public void start() {
        state = State.Enroute;

        Log.d(TAG, String.format("Enroute to %s %d orders",
                annealer.getStops().get(nextStop).getAction().toString(),
                annealer.getStops().get(nextStop).getOrders().size()));
    }

    public void move() {
        // we are enroute to the next stop,  x & y are not uniformly
        // scaled...but we do know the width
        // and height of the map in both meters and degrees, so we
        // approximate it...

        double metersPerTick = transporter.getSpeedMPS() * Constants.SecondsPerSimulationTick;
        Location destination = annealer.getStops().get(nextStop).getLocation();
        Location loc = transporter.getLocation();

        double theta = Math.atan2(
                Constants.deg2MeterY(destination.y - loc.y),
                Constants.deg2MeterX(destination.x - loc.x));
        double dxMetersPerTick = Math.cos(theta) * metersPerTick;
        double dyMetersPerTick = Math.sin(theta) * metersPerTick;

        loc.moveMeters(dxMetersPerTick, dyMetersPerTick);
        double distanceMoved = loc.distance(transporter.getLocation());

        transporter.setLocation(loc);

        double metersToDest = loc.distance(destination);

//        Log.d(TAG, String.format("%3d Remaing distance %f", this.getSerialNumber(), metersToDest));
        if (metersToDest <= (metersPerTick)) {
            // we have arrived
            state = Arrived;
            transporter.setLocation(destination);
            distanceMoved = metersToDest;
        }

        distance += distanceMoved;
    }

    public void arrived() {
        Stop stop = annealer.getStops().get(nextStop);
        switch(stop.getAction()) {
            case Pick: {
                stop.getOrders().forEach(order -> order.pickup(transporter));

                Log.d(TAG, String.format("Picked %d orders",
                        annealer.getStops().get(nextStop).getOrders().size()));
            }
            break;
            case Drop: {
                stop.getOrders().forEach(order -> order.deliver(transporter));

                Log.d(TAG, String.format("Delivered %d orders",
                        annealer.getStops().get(nextStop).getOrders().size()));
            }
        }
        nextStop++;

        if(nextStop >= annealer.getStops().size()) {
            transporter.tripCompleted();
        }

        state = NotStarted;
    }

    public void summarize() {
        endTime = Clock.getTime();

        int totalOrders = annealer.getStops().stream().mapToInt(stop -> stop.getOrders().size()).sum();

        Log.d(TAG, String.format("%3d completed trip, orders %d, trip %6.1fkm, time %s",
                transporter.getSerialNumber(),
                totalOrders,
                getTripLength() / 1000.0,
                Clock.formatAsHM(getTripTime())));

//        try {
//            Main.getDB().insertTransport(
//                    transporter,
//                    transporter.getSerialNumber(),
//                    delivered,
//                    this,
//                    distance);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

}
