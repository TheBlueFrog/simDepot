package com.mike.sim;

import com.mike.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mike on 8/20/2016.
 */
public class TripInfo {

    private static final String TAG = TripInfo.class.getSimpleName();

    private double cost;
    private double distance;
    private long endTime;
    private long startTime;

    private List<Order> completed = new ArrayList<>();
    private Transporter transporter;

    public TripInfo(Transporter transporter) {
        cost = 0;
        distance = 0;
        endTime = 0;
        this.transporter = transporter;

        startTime = Clock.getTime();
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
        return cost;
    }

    public void addCost(double costToTravelTo) {
        cost += costToTravelTo;
    }

    public void move(double meters) {
        distance += meters;
    }

    public Transporter getTransporter() { return transporter; }

    public List<Order> getConsumables() { return completed; }

    public void addCompleted(Order consumable) { completed.add(consumable); }

    public void summarize() {
        endTime = Clock.getTime();

        int delivered = 0;
        int failedPickup = 0;
        int failedDelivery = 0;
        for (Order consumable : completed) {
            switch (consumable.getState()) {
                case Delivered:
                    delivered++;
                    break;
                case PickupFailed:
                    failedPickup++;
                    break;
                case DeliveryFailed:
                    failedDelivery++;
            }
        }

        Log.d(TAG, String.format("%3d completed trip, items (%d, %d, %d), trip %6.1fkm, time %s, cost $%8.4f, start %2.2f",
                transporter.getSerialNumber(),
                delivered,
                failedPickup,
                failedDelivery,
                getTripLength() / 1000.0,
                Clock.formatAsHM(getTripTime()),
                getCost(),
                Clock.getTimeOfDayDouble(Clock.getTimeOfDay(getTripStartTime()))));

        try {
            Main.getDB().insertTransport(
                    transporter,
                    transporter.getSerialNumber(),
                    delivered,
                    this,
                    getCost());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
