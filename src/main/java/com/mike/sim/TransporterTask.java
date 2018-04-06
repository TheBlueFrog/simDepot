package com.mike.sim;

import java.util.List;

/**
 * Created by mike on 6/24/2016.
 *
 * The Scheduler sets up a sequence of TransporterTasks which
 * are sent out for bids by the Transporters.  When all the bids
 * are the list of tasks is given to a selected Transporter to execute.
 */
public class TransporterTask {

    enum Type { Delivery, Pickup, Deadhead };
    enum State {NotStarted, Enroute, Arrived };

    private Type type = Type.Deadhead;
    private State state = State.NotStarted;

    private List<Order> consumables;
    private Location destination;

//    public TransporterTask(Type type, Consumable item) {
//        this.type = type;
//        this.state = State.NotStarted;
//        this.item = item;
//    }

    public TransporterTask(Type type, Location location, List<Order> consumables) {
        this.type = type;
        this.state = State.NotStarted;
        this.consumables = consumables;
        this.destination = location;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Location getDestination() { return new Location(destination); }

    public List<Order> getConsumables() {
        return consumables;
    }

    public Type getType() {
        return type;
    }

}
