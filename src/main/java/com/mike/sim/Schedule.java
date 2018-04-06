package com.mike.sim;

import com.mike.util.Log;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by mike on 6/26/2016.
 */
public class Schedule {

    private static final String TAG = Schedule.class.getSimpleName();

    static private long nextID = 1;

    private long id;
    private Map<Consumer, List<Order>> orders;
    private Transporter transporter;
    private List<TransporterTask> tasks;
    private int itemsDelivered = 0;
    private double distance;
    private long startTime;
    private long endTime;
    private double cost;

    public Schedule(Map<Consumer, List<Order>> orders, Transporter transporter) {

        this.id = nextID++;
        this.cost = 0.0;

        this.orders = orders;
        this.transporter = transporter;

        this.tasks = build();
    }

    public Schedule(Transporter transporter, Map<Consumer, List<Order>> pickups, Map<Consumer, List<Order>> deliveries) {
    }

    /**
     * really a TSP problem...we need -a- solution not necessarily
     * the best solution...
     *
     * build a sequence of tasks that will make all the deliveries
     *
     * pickup any needed requests until distance to next pickup is > distance
     * to any delivery.
     * @return
     */
    private List<TransporterTask> build () {

        Set<Supplier> suppliers = getSuppliers (orders);
        Set<Consumer> consumers = new HashSet<>(orders.keySet());
        List<Order> onBoard = new ArrayList<>();

        tasks = new ArrayList<>();
        Location loc = transporter.getHome();

        // always start by going to a Supplier since we have nothing on-board

        if (suppliers.size() == 0) {
            Log.d(TAG, "no suppliers?");
        }
//        assert suppliers.size() > 0;
        assert consumers.size() > 0;

        Supplier s = getClosestSupplier(loc, suppliers);
        if (s == null) {
            Log.d(TAG, "no suppliers");
        }

        loc = s.getLocation();
        gotoAndPickupAnyNeededConsumables(s, onBoard);
        suppliers.remove(s); // TODO bug, not handled, overloaded have to return to supplier

        while (stillHaveOrders()) {

            // either go to a Consumer or Supplier, whichever is closer
            s = getClosestSupplier(loc, suppliers);
            Consumer c = getClosestConsumer(loc, onBoard);

            if ((s != null) && (c != null)) {
                if (s.getLocation().distance(loc) < c.getLocation().distance(loc)) {
                    // supplier is closer, keep on keepin' on
                    gotoAndPickupAnyNeededConsumables(s, onBoard);
                    suppliers.remove(s); // TODO bug, not handled, overloaded have to return to supplier
                } else {
                    // consumer is closer
                    gotoAndDeliverAllConsumables(c, onBoard);
                }
            }
            else if ((s == null) && (c != null)) {
                gotoAndDeliverAllConsumables(c, onBoard);
            }
            else if ((s != null) && (c == null)) {
                // we probably picked up stuff, delivered it all, and
                // now we need to go to a supplier, unless orders are empty
                gotoAndPickupAnyNeededConsumables(s, onBoard);
                suppliers.remove(s); // TODO bug, not handled, overloaded have to return to supplier
            }
        }

        assert onBoard.size() == 0;

        tasks.add(new TransporterTask(TransporterTask.Type.Deadhead, transporter.getHome(), null));
        return tasks;
    }

    private boolean stillHaveOrders() {
        for (Consumer c : orders.keySet())
            if (orders.get(c).size() > 0)
                return true;
        return false;
    }

    /**
     * load all Consumables we need from this supplier
     * @param s
     * @param onBoard
     */
    private void gotoAndPickupAnyNeededConsumables(Supplier s, List<Order> onBoard) {
        List<Order> v = new ArrayList<>();
        for (Consumer consumer : orders.keySet())
            for (Order c : orders.get(consumer))
                if (c.getSupplier().getSerialNumber() == s.getSerialNumber())
                    v.add(c);

        tasks.add(new TransporterTask(TransporterTask.Type.Pickup, s.getLocation(), v));
        onBoard.addAll(v);
    }

    private void gotoAndDeliverAllConsumables(Consumer consumer, List<Order> onBoard) {
        List<Order> v = new ArrayList<>();
        for (Order c : orders.get(consumer))
            if (onBoard.contains(c))
                v.add(c);

        itemsDelivered += v.size();

        tasks.add(new TransporterTask(TransporterTask.Type.Delivery, consumer.getLocation(), v));
        onBoard.removeAll(v);

        // now remove everything we just delivered from the open orders
        for (Order c : v) {
            for (Consumer cons : orders.keySet()) {
                List<Order> vv = orders.get(cons);
                if (vv.contains(c)) {
                    vv.remove(c);
                }
            }
        }
    }

    private Supplier getClosestSupplier(Location t, Set<Supplier> suppliers) {
        double md = Double.MAX_VALUE;
        Supplier ss = null;
        for (Supplier s : suppliers) {
            double d = t.distance(s.getLocation());
            if (d < md) {
                md = d;
                ss = s;
            }
        }
        return ss;
    }
    private Consumer getClosestConsumer(Location t, List<Order> onBoard) {
        double md = Double.MAX_VALUE;
        Consumer ss = null;
        for (Order c : onBoard) {
            double d = t.distance(c.getConsumer().getLocation());
//            Log.d(TAG, String.format("%d, getClosestConsumer %.1f",
//                    getID(), d));
            if (d < md) {
                md = d;
                ss = c.getConsumer();
            }
        }
//        if (ss != null)
//            Log.d(TAG, String.format("%d, getClosestConsumer closest is %.1f",
//                    getID(), t.distance(ss.getLocation())));
        return ss;
    }

    /**
     * assemble a list of the suppliers we will have to visit to
     * get everything in the orders
     * @param orders
     * @return
     */
    private Set<Supplier> getSuppliers(Map<Consumer, List<Order>> orders) {
        Set<Supplier> v = new HashSet<>();
        for (Consumer c : orders.keySet())
            for (Order consumable : orders.get(c))
                if ( ! v.contains(consumable.getSupplier()))
                    v.add(consumable.getSupplier());
        return v;
    }


    public Map<Consumer, List<Order>> getOrders() { return orders; }

    public List<TransporterTask> getTasks () { return tasks; }

    public boolean hasTasks() {
        return tasks.size() > 0;
    }

    public TransporterTask removeFirstTask() {
        return tasks.remove(0);
    }

    public Object getID() {
        return id;
    }


    public void move (double meters) { distance += meters; }

    public void end(Transporter transporter)
    {
        endTime = Clock.getTime();

        Log.d(TAG, String.format("%3d completed Schedule %d, %d items, trip %6.1fkm, time %s, cost $%8.4f, start %2.2f",
                transporter.getSerialNumber(),
                getID(),
                getItemsDelivered(),
                getTripLength() / 1000.0,
                Clock.formatAsHM (getTripTime()),
                getCost(),
                Clock.getTimeOfDayDouble(Clock.getTimeOfDay(getTripStartTime()))));

        try {
            Main.getDB().insertTransport(
                    null,
                    transporter.getSerialNumber(),
                    0,
                    null,
                    getCost());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double getTripLength() { return distance; }
    public long getTripTime() { return endTime - startTime; }

    public long getTripStartTime() {
        return startTime;
    }

    public int getItemsDelivered() { return itemsDelivered; }

    public double getCost() { return cost; }
    public void addCost(double costToTravelTo) { cost += costToTravelTo; }
    public void startTrip() {
        distance = 0.0;
        startTime = Clock.getTime();
        endTime = 0;
    }
}
