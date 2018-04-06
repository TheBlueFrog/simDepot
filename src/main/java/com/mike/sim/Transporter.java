package com.mike.sim;

import com.mike.util.Log;
import javafx.util.Pair;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mike.sim.Transporter.State.Arrived;
import static com.mike.sim.Transporter.State.Enroute;
import static com.mike.sim.Transporter.State.NotStarted;

/**
 * Created by mike on 6/17/2016.
 */
public class Transporter extends LocatedAgent {

    static private final String TAG = Transporter.class.getSimpleName();

    public static double DollarsPerGallonFuel = 2.50;
    public static double VehicleMass = (3000.0 / 2.2046);
    public static double VehicleMileage = (10.0);
    public static double VehicleDollarPerKgM = (1.0 / (VehicleMileage * VehicleMass * 1600.0)) * DollarsPerGallonFuel;
    public static double VehicleAverageSpeedMiPHr = 30.0;
    public static double MaxLoadKg = 100.0; // maximumKg net buildWants

    private Location home;

    // get speed in meters per second
    public double speedMPS = VehicleAverageSpeedMiPHr * Constants.MetersPerMile / (60 * 60);

    public static double movementCostPerKgM = VehicleDollarPerKgM;

    // when moving how much (in meters) we move per tick
    private double metersPerTick;
    private double dxMetersPerTick;
    private double dyMetersPerTick;


    // roughly, we start with a list of pickups (with assigned suppliers) and
    // no deliveries.  We do some pickups, as we pickup each Consumable it
    // gets moved to the deliveries.  We do some deliveries and the Consumables
    // get moved to completed.  Repeat until no pickups or deliveries remain.
    // at the summarize we send the completed back to the Scheduler

    private Map<Consumer, List<Order>> pickups = new HashMap<>();
    private Map<Consumer, List<Order>> deliveries = new HashMap<>();

    public enum State {NotStarted, Enroute, Arrived };

    private class Task {
        // as task consist of going to a Location, the location
        // can be either a Supplier or a Consumer or the
        // Transporter's home

        private final Consumer consumer;
        private final Supplier supplier;
        private final Location destination;

        public State state;

        public Task () {
            consumer = null;
            supplier = null;
            destination = Transporter.this.getHome();
            state = NotStarted;
        }
        public Task (Consumer consumer) {
            this.consumer = consumer;
            this.supplier = null;
            destination = consumer.getLocation();

            this.state = NotStarted;
        }
        public Task (Supplier supplier) {
            this.consumer = null;
            this.supplier = supplier;
            destination = supplier.getLocation();

            this.state = NotStarted;
        }

        public Location getDestination() { return destination; }

        public boolean isPickup() {
            return supplier != null;
        }
        public boolean isDelivery() { return consumer != null; }
    }

    private Task task = null;

    private TripInfo tripInfo = null;

    @Override
    protected String getClassName() {
        return null;
    }

    /**
     * draw a circle where we are on the map
     * @param g2    graphics context to use to paint this agent
     */
    @Override
    void paint(Graphics2D g2) {

        Shape circle = null;
        if (circle == null) {
            double x = Constants.deg2PixelX(getLocation().x);
            double y = Constants.deg2PixelY(getLocation().y);

            circle = new Ellipse2D.Double(x, y, 5.0f, 5.0f);
        }

        Color c = Color.gray;
        if (task != null) {
            if (task.isPickup())
                c = Color.red;
            else if (task.isDelivery())
                c = Color.blue;
//            else
//                c = Color.gray;
        }

        g2.setColor(c);
        g2.draw(circle);
    }

    @Override
    public String toString () {
        return String.format("Transporter %d, %d pickups, %d deliveries",
                getSerialNumber(),
                countPickups(),
                countDeliveries());
    }

    private int countPickups() {
        int i = 0;
        for (Consumer consumer : pickups.keySet())
            for (Order c : pickups.get(consumer))
                i++;
        return i;
    }
    private int countDeliveries() {
        int i = 0;
        for (Consumer consumer : deliveries.keySet())
            for (Order c : deliveries.get(consumer))
                i++;
        return i;
    }


    public Transporter(Framework f, Long serialNumber) {
        super(f, serialNumber);

        this.home = new Location(
                Constants.MapLeft + ((Constants.MapRight - Constants.MapLeft) / 2.0),
                Constants.MapBottom + ((Constants.MapTop - Constants.MapBottom) / 2.0));
                // Constants.randomFarmLocation();

        setLocation(home);

        register();
    }

    @Override
    protected void onMessage(Message msg) {

        assert msg.serialNumber == this.getSerialNumber();

        if ((msg.mSender == null) && (((Framework.State) msg.mMessage)).equals(Framework.State.AgentsRunning)) {
            // frameworks says everyone is ready, we're waiting
            send(new Message(this, Clock.class, 0, "subscribe"));
            send(new Message(this, Scheduler.class, 0, "available"));
            return;
        }
        if (msg.mSender instanceof Clock) {
            tick (Clock.getTime());
        }
        else if ((msg.mSender instanceof Scheduler) && (msg.mMessage instanceof TransporterBid)) {
            // either a consumable list for us to bid on or a winning bid we made
            TransporterBid tb = (TransporterBid) msg.mMessage;

            if (tb.getAwarded()) {
                // Scheduler accepted our bid so deal with it

                if (tripInfo != null) {
                    // we can already be busy, if so send it back
                    tb.rejectAwardedBid ();
                    send(new Message(this, Scheduler.class, 0, msg.mMessage));
                    Log.d(TAG, String.format("%3d rejecting awarded Bid %d, already busy",
                            getSerialNumber(),
                            tb.getTag()));
                    return;
                }

                startBid (tb);

                Log.d(TAG, String.format("%3d start executing bid %d",
                        getSerialNumber(),
                        tb.getTag()));

            } else {
                // put together our bid on this set of tasks
                // and return to the scheduler

//                Schedule s = new Schedule(tb.getConsumables(), this);
                double bid = calcBid(null);

                tb.setBid(bid);
//                tb.setSchedule(s);
//                Log.d(TAG, String.format("%3d return bid %d",
//                        getSerialNumber(),
//                        tb.getTag()));

                send(new Message(this, Scheduler.class, 0, tb));
            }
        }
        else if ((msg.mSender instanceof Transporter) && (msg.serialNumber == getSerialNumber())) {
            // msg from myself
        }
        else {
            Log.e(TAG, "Unhandled message ignored.");
        }
    }

    private Map<Consumer,List<Order>> listToMap(List<Order> consumables) {
        Map<Consumer,List<Order>> m = new HashMap<>();
        for (Order c : consumables) {
            if ( ! m.containsKey(c.getConsumer()))
                m.put(c.getConsumer(), new ArrayList<Order>());

            m.get(c.getConsumer()).add(c);
        }
        return m;
    }

    private void startBid(TransporterBid tb) {
        pickups = listToMap(tb.getConsumables());

        tripInfo = new TripInfo(this);
    }

    private void stopBid() {
        tripInfo.summarize();
        send(new Message(this, Scheduler.class, 0, tripInfo));
        tripInfo = null;
    }


    private double calcBid(Schedule schedule) {
        if (tripInfo == null)
            return 1.0;

        // already busy don't bid
        Log.d(TAG, String.format("%3d is busy, don't bid", getSerialNumber()));
        return Double.POSITIVE_INFINITY;

    }

    private void tick(long curTime) {
        if (tripInfo == null)
            return;

        if (task == null) {
            // start first task
            task = getNextTask();

            if (task == null) {
                // nothing more to do
                return;
            }
        }

        switch (task.state) {
            case NotStarted:
                task.state = Enroute;
                addCost(costToTravelTo (task.getDestination()));
                break;
            case Enroute:
                move();
                break;
            case Arrived:
                if ( ! markDone()) {
                    Log.d(TAG, "argh");
                }
        }

        Main.repaint();
    }

    /**
     * @return the next Task to do, the hard one
     */
    private Task getNextTask() {
        List<Supplier> suppliers = getSuppliersToVisit();
        List<Consumer> consumers = getConsumersToVisit();

        if ((suppliers.size() == 0) && (consumers.size() == 0)) {
            return new Task(); // nothing left to do
        }

        Pair<Supplier, Double> s = closestSupplier(suppliers);
        Pair<Consumer, Double> c = closestConsumer(consumers);

        assert (s != null) || (c != null);

        if ((s != null) && (c != null)) {
            if (s.getValue() < c.getValue()) {
                // goto the supplier
                return new Task(s.getKey());
            } else {
                // goto the consumer
                return new Task(c.getKey());
            }
        } else if (s != null) {
            // only a supplier go there
            return new Task(s.getKey());
        } else {
            // only a consumer, go there
            return new Task(c.getKey());
        }
    }

    private Pair<Supplier, Double> closestSupplier(List<Supplier> suppliers) {
        try {
            Location loc = getLocation();
            double md = Double.MAX_VALUE;
            Supplier ss = null;
            for (Supplier s : suppliers) {
                double d = loc.distance(s.getLocation());
                if (d < md) {
                    md = d;
                    ss = s;
                }
            }
            if (ss != null)
                return new Pair<Supplier, Double>(ss, md);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return null;
    }
    private Pair<Consumer, Double> closestConsumer(List<Consumer> consumers) {
        Location loc = getLocation();
        double md = Double.MAX_VALUE;
        Consumer ss = null;
        for (Consumer c : consumers) {
            double d = loc.distance(c.getLocation());
            if (d < md) {
                md = d;
                ss = c;
            }
        }
        if (ss != null)
            return new Pair<Consumer, Double>(ss, md);

        return null;
    }

    private List<Supplier> getSuppliersToVisit() {
        List<Supplier> v = new ArrayList<>();
        for (Consumer consumer : pickups.keySet())
            for (Order consumable : pickups.get(consumer)) {
                Supplier s = consumable.getSupplier();
                if (s == null)
                    Log.d(TAG, "Consumable with no Supplier");

                if ( ! v.contains(s))
                    v.add(s);
            }
        return v;
    }
    private List<Consumer> getConsumersToVisit() {
        List<Consumer> v = new ArrayList<>();
        for (Consumer consumer : deliveries.keySet())
            if (deliveries.get(consumer).size() > 0)
                v.add(consumer);
        return v;
    }


    private void move() {
        // we are enroute to the next destination, gets ugly because
        // x & y are not uniformly scaled...but we do know the width
        // and height of the map in both meters and degrees, so we
        // can approximate it...

        metersPerTick = speedMPS * Constants.SecondsPerSimulationTick;
        Location destination = task.getDestination();
        double theta = Math.atan2(
                Constants.deg2MeterY(destination.y - getLocation().y),
                Constants.deg2MeterX(destination.x - getLocation().x));
        dxMetersPerTick = Math.cos(theta) * metersPerTick;
        dyMetersPerTick = Math.sin(theta) * metersPerTick;

        Location loc = getLocation();
        loc.moveMeters(dxMetersPerTick, dyMetersPerTick);
        setLocation(loc);

        double metersToDest = getLocation().distance(destination);

//        Log.d(TAG, String.format("%3d Remaing distance %f", this.getSerialNumber(), metersToDest));
        if (metersToDest <= (metersPerTick)) {
            // we have arrived
            task.state = Arrived;
            setLocation(destination);
            tripInfo.move(metersToDest);
        } else {
            tripInfo.move(metersPerTick);
        }
    }

    private boolean markDone() {
        task.state = Arrived;

        if (task.isPickup()) {
            if ( ! pickup(task)) {
                // argh, supplier has run out...
                return false;
            }
        } else if (task.isDelivery()) {
            deliver(task);
        } else {
            stopBid ();
        }

        task = null;
        return true;
    }

    /**
     * @param task we have arrived at a supplier, pickup everything
     *             given by this.pickups, for every Consumable picked up
     *             move it to the deliveries list
     * @return
     */
    private boolean pickup(Task task)
    {
        for (Consumer consumer : pickups.keySet()) {
            // make copy of the list we're going to remove from and iterate that
            List<Order> v = new ArrayList<>(pickups.get(consumer));
            for (Order consumable : v) {
                if (consumable.getSupplier().getSerialNumber() == task.supplier.getSerialNumber()) {

                    pickups.get(consumer).remove(consumable);
//                    Log.d(TAG, String.format("%d remove %s from pickups", getSerialNumber(), consumable.toString()));

                    consumable.pickup(this);

                    addDelivery(consumable.getConsumer(), consumable);
                }
            }
        }
        return true;
    }

    private void addDelivery(Consumer consumer, Order consumable) {
        if ( ! deliveries.containsKey(consumer))
            deliveries.put(consumer, new ArrayList<Order>());

        deliveries.get(consumer).add(consumable);
    }

    private void deliver(Task task)
    {
        boolean didDeliver = false;
//        Log.d(TAG, String.format("%d has delivery for %d",
//                getSerialNumber(),
//                task.consumer.getSerialNumber()));

        for (Consumer consumer : deliveries.keySet()) {
            // make copy of the list we're going to remove from and iterate that
            List<Order> v = new ArrayList<>(deliveries.get(consumer));
            for (Order consumable : v) {
                if (consumable.getConsumer().getSerialNumber() == task.consumer.getSerialNumber()) {

                    didDeliver = true;
                    deliveries.get(consumer).remove(consumable); // no longer a delivery
//                    Log.d(TAG, String.format("%d remove %s from deliveries", getSerialNumber(), consumable.toString()));

                    consumable.deliver(this);

                    tripInfo.addCompleted (consumable);
                }
            }
        }

        if ( ! didDeliver)
            Log.d(TAG, String.format("%d wtf", getSerialNumber()));
    }

    private double getLoad() {
        double mass = 0;
        for (Consumer c : deliveries.keySet())
            for(Order i : deliveries.get(c))
                mass += 1;//i.getMass();
        return mass;
    }

    /**
     * @param a Location
     * @param b Location
     *
     * @return cost to travel from location a to location b
     */
    private double costToTravelTo(Location a, Location b) {

        double mass = VehicleMass + getLoad();

        return a.distance (b) * mass * movementCostPerKgM;
    }

    /**
     * @param location
     * @return cost to travel from current location to another location
     */
    private double costToTravelTo(Location location) {
        return costToTravelTo(getLocation(), location);
    }

    public double getCost() { return tripInfo.getCost(); }
    private void addCost(double d) { tripInfo.addCost(d); }

    public Location getHome() {
        return new Location(home);
    }

    public String getLabel() {
        return String.format("Transporter %d", getSerialNumber());
    }

}
