package com.mike.sim;

import java.util.*;


/**
 * Created by mike on 6/18/2016.
 *
 * Consumers create orders, they get passed around
 * until matched up with a real item at a supplier/depot
 * and then passed to the Consumer who ticks it off as
 * complete
 */
public class Order {

    static private long nextId = 1;

    private long origination;       // time of creation
    private long id;

    private Consumer consumer;      // who ordered this

    private long itemId;
    private double quantity;


    public Long getID() {
        return id;
    }

    public enum State {
        Ordered,
        AtSupplier,
        AtDepot,
        PickedUp,
        Delivered,

        PickupFailed, DeliveryFailed
    };

    public class Event {
        private long time = Clock.getTime();
        private State state = State.Ordered;
        private Supplier supplier = null;

        public Event () {
        }

        public State getState() { return state; }
        public Supplier getSupplier() { return supplier; }

        public Event(Supplier supplier) {
            time = Clock.getTime();
            state = State.AtSupplier;
            this.supplier = supplier;
        }

        // this constructor handles Transporter state transitions
        public Event (Deque<Event> history, Transporter transporter) {
            Event previous = history.getLast();

            time = Clock.getTime();
            supplier = previous.getSupplier();

            switch (previous.getState()) {
                case Ordered:
                    throw new IllegalStateException("shouldn't happen, covered by above constructor");

                case AtSupplier:
                    state = supplier.pickup(transporter, Order.this)
                            ? State.PickedUp : State.PickupFailed;
                    break;

                case PickedUp:
                    state = consumer.deliver(transporter, Order.this)
                            ? State.Delivered : State.DeliveryFailed;
                    break;

                case Delivered:
                case DeliveryFailed:
                case PickupFailed:
                    state = previous.getState();
                    break;

                default:
                    throw new IllegalStateException(String.format("Transporter can't do this %s", state));
            }
        }

        /** only has a defined Supplier sometimes
         *
         * @return Supplier if defined, else null
         */
        public Location getPickupLocation() {
            if (hasSupplier())
                return new Location(getSupplier().getLocation());
            else
                return null;
        }

        public boolean hasSupplier () {
            switch (state) {
                case AtSupplier:
                case PickedUp:
                case PickupFailed:
                case Delivered:
                case DeliveryFailed:
                    return true;

                default:
                    return false;
            }
        }
    }

    // as things happen they are added to Event list
    private Deque<Event> history  = new ArrayDeque<>();

    public Deque<Event> getHistory() {
        return history;
    }

//    /**
//     * construct an item for use by consumers, suppliers and transporters
//     *
//     * @param consumer      who wants some
//     * @param description   what they want
//     * @param quantity      how much (kg)
//     */
//    public Consumable(Consumer consumer, String description, double quantity) {
//        origination = Clock.getTime();
//
//        this.description = description;
//        this.consumer = consumer;
//        this.quantity = quantity;
//        this.mass = quantity; // @TODO // FIXME: 6/25/2016 assumes mass and orderQuantity are identical
//    }

    public Order(Consumer consumer, long itemId, int quantity) {
        id = nextId++;
        origination = Clock.getTime();

        this.consumer = consumer;
        this.itemId = itemId;
        this.quantity = quantity;

        history.add(new Event());
    }

    public Location getDeliveryLocation() {
        return new Location(consumer.getLocation());
    }
    public double getQuantity() {
        return quantity;
    }
    public long getOriginationTime() {
        return origination;
    }
    public Consumer getConsumer() {
        return this.consumer;
    }
    public void clearSupplier() {
        history.add (new Event());
    }
    public Location getPickupLocation() {
        return history.getLast().getPickupLocation();
    }
    public Supplier getSupplier() {
        return history.getLast().getSupplier();
    }
    public boolean hasSupplier() {
        return history.getLast().hasSupplier();
    }
    public void setSupplier(Supplier supplier) {
        history.add(new Event(supplier));
    }
    public void pickup(Transporter transporter) {
        assert hasSupplier();
        history.add(new Event(history, transporter));
    }
    public void deliver(Transporter transporter) {
        history.add(new Event(history, transporter));
    }
    public State getState() {
        return history.getLast().getState();
    }

    public boolean supplierHasFailed(Supplier s) {
        for (Event event : history)
            if ((event.getState().equals(State.PickupFailed)
                    && (event.getSupplier().getSerialNumber() == s.getSerialNumber())))
                return true;

        return false;
    }

    @Override
    public String toString () {
        return String.format("Order %4d for %4d, %s",
                getID(),
                getConsumer().getID(),
                itemId,
                consumer.getSerialNumber());
    }

    public List<Event> getPickupFailures() {
        List<Event> v = new ArrayList<>();
        for (Event event : history)
            if (event.state.equals(State.PickupFailed))
                v.add(event);

        return v;
    }
}
