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

    // as requests for Consumables come in retain them here until
    // we send it out for bids by Transporters
    private List<Order> requests = new ArrayList<>();

    private Map<Long, List<TransporterBid>> outForBids = new HashMap<>();
    private Map<Long, List<TransporterBid>> returnedBids = new HashMap<>();

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

            } else if (msg.mMessage instanceof TransporterBid) {
                TransporterBid tb = (TransporterBid) msg.mMessage;
                if (tb.rejected()) {
                    Log.d(TAG, String.format("%s handle rejected bid",
                            getSerialNumber()));

                    // Transporter we awarded it to rejected it, take the Consumables
                    // in the bid and merge with our unassigned list
                    List<Order> m = tb.getConsumables();
                    addRequestedConsumables(m);

                    getBidsForConsumables();
                }
                else {
                    // normal bid coming back
                    handleBid(tb);
                }
            } else if (msg.mMessage instanceof TripInfo) {
                TripInfo tripInfo = (TripInfo) msg.mMessage;

                handleTripInfo (tripInfo);
            }
        }
        else if (msg.mSender instanceof Supplier) {
            Supplier s = (Supplier) msg.mSender;

            Pair<Long, Boolean> p = (Pair<Long, Boolean>) msg.mMessage;
            updateSupplier(s, p.getKey(), p.getValue());
        }
        else if (msg.mSender instanceof Consumer) {

            // some consumer wants something

            Consumer consumer = (Consumer) msg.mSender;
            List<Order> orders = (List<Order>) msg.mMessage;

            addRequestedConsumables(orders);

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
                getBidsForConsumables();
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

    private void handleTripInfo(TripInfo tripInfo) {
        // look at the event history of the trip

        // collect failed pickups, due to Supplier shortage

        for (Order consumable : tripInfo.getConsumables()) {

            // look at current state don'e look at the whole history
            Order.State state = consumable.getState();
            switch (state) {
                case Ordered:
                case AtSupplier:
//                        Log.d(TAG, "oops");
                    break;

                case PickedUp:
                case Delivered:
//                        Log.d(TAG, "cool");
                    break;

                case DeliveryFailed:
                    break;

                case PickupFailed:
                    // pull these out
                    failedPickup.add(consumable);
//                        send(new Message(this,
//                                Consumer.class, consumable.getConsumer().getSerialNumber(),
//                                new Pair<String, Consumable>("pickupFailure", consumable)));
                    break;
            }
        }

        Log.d(TAG, String.format("%d failed pickups from Transporter %d, daily total %d",
                failedPickup.size(),
                tripInfo.getTransporter().getSerialNumber(),
                failedPickup.size()));
    }

    private void endOfDay() {
        clearFailedPickups();
        logDailyStats();
    }

    private void clearFailedPickups() {
//        for (Consumable consumable : failedPickup) {
//            send(new Message(this,
//                    Supplier.class, consumable.getSupplier().getSerialNumber(),
//                    new Pair<String, Consumable>("pickupFailure", consumable)));
//        }

        Log.d(TAG, String.format("Discarding %d failed pickups", failedPickup.size()));
        failedPickup.clear();
    }

//    private List<Supplier> findSuppliers(Order order) {
//        List<Supplier> v = new ArrayList<>();
//        v.add(order.getSupplier());
//        return v;
//    }

    private void addRequestedConsumables(List<Order> i) {
        requests.addAll(i);
    }

    private void logDailyStats() {
        StringBuilder sb = new StringBuilder("Start of day suppliers ");
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

    private void getBidsForConsumables() {
        // take the requests we have, send out for bids, start a
        // new list for incoming requests
        List<Order> orders = requests;
        requests = new ArrayList<>();

        if (orders.size() > 0)
            sendForBids(orders);
    }

    private void sendForBids(List<Order> orders) {

        // start a new set of bids
        long tag = TransporterBid.nextBidTag ();
        outForBids.put(tag, new ArrayList<TransporterBid>());
        returnedBids.put(tag, new ArrayList<TransporterBid>());

        Log.d(TAG, String.format("%4d Send out bid %4d, %d items",
                this.getSerialNumber(),
                tag,
                orders.size()));

        for (Transporter t : transporters) {
            TransporterBid tb = new TransporterBid(tag, orders, t);
            outForBids.get(tag).add(tb);
//            Log.d(TAG, String.format("%d Send bid %d to %s",
//                    this.getSerialNumber(),
//                    tb.getTag(),
//                    t.getLabel()));
            send(new Message(this, Transporter.class, t.getSerialNumber(), tb));
        }
    }

    /**
     * a bid came back, when they are all back pick one and
     * sent to Transporter
     * @param tb
     */
    private void handleBid(TransporterBid tb) {
        long tag = tb.getTag();
        outForBids.get(tag).remove(tb);
        returnedBids.get(tag).add(tb);

        if ( ! outForBids.get(tag).isEmpty())
            return;

        // all back, pick one
        TransporterBid low = null;
        for (TransporterBid t : returnedBids.get(tag)) {
            if (low == null)
                low = t;
            else {
                if (t.getBid() < low.getBid())
                    low = t;
            }
        }

        low.setAwarded(true);
        returnedBids.get(tag).clear();

        Log.d(TAG, String.format("Awarded bid %d to %3d, %3.1f",
                tb.getTag(),
                low.getTransporter().getSerialNumber(), low.getBid()));

        send(new Message(this, Transporter.class, low.getTransporter().getSerialNumber(), low));
    }


    // @TODO favors early registering suppliers? and ones that never run out


}
