package com.mike.sim;

import com.mike.util.Log;
import javafx.util.Pair;

import java.util.*;

/**
 * Created by mike on 6/17/2016.
 * <p>
 * This class receives Consumer requests for Consumables, they are
 * aggregated into Schedules, these are sent out to Transporters for
 * a bid, the Schedule is assigned to the low bidder.
 * <br>
 * ad inifinitum
 * <p>
 * The scheduler aggregates arriving requests are aggregated into a single
 * schedule until it's been too long, e.g. the Consumer is going to be waiting too long
 * or there are too many Consumables, e.g. we'll overload a Transporter
 *
 */
public class Scheduler extends Agent {

    static private final String TAG = Scheduler.class.getSimpleName();

    private class SupplierStatus {

        private final Supplier supplier;
        private Map<Long, Boolean> available = new HashMap<>();

        public SupplierStatus (Supplier supplier) {
            this.supplier = supplier;
        }

//        public void update(long itemID, boolean available) {
//
//            this.available.put(itemID, available);
//
//            Log.d(TAG, String.format("Supplier %d enabled for %s",
//                    supplier.getSerialNumber(),
//                    available ? "enabled" : "disabled",
//                    Order.getId(itemID)));
//        }

    }

    // list of known suppliers (indexed by their serial number) with their status
    private List<SupplierStatus> suppliers = new ArrayList<>();

    // list of known transporters, don't maintain status, we send out bids
    // to everyone, if they respond ok, if not ok
    private List<Transporter> transporters = new ArrayList<>();

    // as Orders come in retain them here until
    // we send it out to a Transporter
    private List<Order> requests = new ArrayList<>();

    private List<Order> failedPickup = new ArrayList<>();

    @Override
    protected String getClassName() {
        return null;
    }

    public Scheduler(Framework f, Long serialNumber) {
        super(f, serialNumber);

        assert serialNumber == 0; // we're supposed to be a singleton

        register();
    }

    @Override
    protected void onMessage(Message msg) {

        if ((msg.mSender == null) && (((Framework.State) msg.mMessage)).equals(Framework.State.AgentsRunning)) {
            send(new Message(this, Clock.class, 0, "subscribe"));
        }
        else if (msg.mSender instanceof Transporter) {
            if (msg.mMessage instanceof String) {
                switch (((String) msg.mMessage)) {
                    case "available":
                        transporters.add((Transporter) msg.mSender);
                        break;
                    default:
                        Log.e(TAG, "Unhandled message from a Transporter ");
                        break;
                }
            }
            else if (msg.mMessage instanceof TripInfo) {
                TripInfo tripInfo = (TripInfo) msg.mMessage;
                handleCompletedTripInfo(tripInfo);
            }
        }
        else if (msg.mSender instanceof Supplier) {
            Supplier s = (Supplier) msg.mSender;

            Pair<Long, Boolean> p = (Pair<Long, Boolean>) msg.mMessage;
            updateSupplier(s, p.getKey(), p.getValue());
        }
        else if (msg.mSender instanceof Consumer) {

            Consumer consumer = (Consumer) msg.mSender;
            List<Order> orders = (List<Order>) msg.mMessage;

            addRequestedOrders(orders);

            for (Order order : orders) {
                Log.d(TAG, String.format("%4d Consumer %4d wants %4d",
                        getSerialNumber(),
                        consumer.getSerialNumber(),
                        order.getID()));
            }

            if (transporters.size() == 0)
                return; // no transporters
            if (suppliers.size() == 0)
                return; // no suppliers
        }
        else if (msg.mSender instanceof Clock) {
            if (Clock.getTimeOfDayInMinutes() < 1) {
                endOfDay ();
            }

            if (haveEnoughRequests())
                giveOrdersToTransporter();
        }
    }

    private void updateSupplier(Supplier s, long itemID, boolean enabled) {
        SupplierStatus status = getStatus(s);
        if (status != null) {
            status.available.put(itemID, enabled);
            return;
        }

        status = new SupplierStatus(s);
        status.available.put(itemID, enabled);
        suppliers.add(status);
    }

    private SupplierStatus getStatus (Supplier s) {
        for (SupplierStatus ss : suppliers)
            if (ss.supplier.getSerialNumber() == s.getSerialNumber()) {
                return ss;
            }
        return null;
    }

    /** a transporter is returning this TripInto
     */
    private void handleCompletedTripInfo(TripInfo tripInfo) {
    }

    private void endOfDay() {
        logDailyStats();
    }

    private void addRequestedOrders(List<Order> i) {
        requests.addAll(i);
    }

    private void logDailyStats() {
        StringBuilder sb = new StringBuilder("Collect end-of-day stats");
//        for (SupplierStatus ss : suppliers) {
//            sb.append(String.format("(%d, ", ss.supplier.getSerialNumber()));
//            for (Long i : ss.available.keySet())
//                if (ss.available.get(i).booleanValue())
//                    sb.append(Order.getDescription(i)).append(", ");
//            sb.delete(sb.length()-2, sb.length());
//            sb.append(") ");
//        }

        Log.d(TAG, sb.toString());
    }

    private boolean haveEnoughRequests() {
        double mass = 0;
        long oldest = Clock.getTime();

        for (Order c : requests) {
            mass += 1;//c.getMass();
            if (c.getOriginationTime() < oldest)
                oldest = c.getOriginationTime();
        }

        if (mass > (Transporter.MaxLoadKg * 0.9))
            return true;

        if ((Clock.getTime() - oldest) > (60 * 60)) // more than an hour, make this sensitive to historical loading...
            return true;

        return false;
    }

    private void giveOrdersToTransporter() {
        List<Order> orders = requests;
        requests = new ArrayList<>();


        if (orders.size() > 0) {
            for (Transporter transporter : transporters) {
                TripInfo tripInfo = new TripInfo(transporter, orders);
                send(new Message(this, Transporter.class, transporter.getSerialNumber(), tripInfo));

                // no available any more
                transporters.remove(transporter);
                return;
            }
        }
    }

}
