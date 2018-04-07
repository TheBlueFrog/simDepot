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
    private Supplier supplier;
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
        public State getState() { return state; }

        private Agent agent = null;

        public Event (State state) {
            this.state = state;
        }

        // this constructor handles Transporter state transitions
        public Event (State newState, Agent agent) {
            this.agent = agent;
            this.state = newState;
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

    public Order(Consumer consumer, Supplier supplier, long itemId, int quantity) {
        id = nextId++;
        origination = Clock.getTime();

        this.consumer = consumer;
        this.itemId = itemId;
        this.quantity = quantity;
        this.supplier = supplier;

        history.add(new Event(State.Ordered));
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
    public Supplier getSupplier() {
        return this.supplier;
    }
    public void pickup(Transporter transporter) {
        history.add(new Event(State.PickedUp, transporter));
    }
    public void deliver(Transporter transporter) {
        consumer.deliver(transporter, this);
        history.add(new Event(State.Delivered, transporter));
    }
    public State getState() {
        return history.getLast().getState();
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
