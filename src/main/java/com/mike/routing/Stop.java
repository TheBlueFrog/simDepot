package com.mike.routing;

import com.mike.sim.Consumer;
import com.mike.sim.Order;
import com.mike.sim.Supplier;
import com.mike.util.Location;

import java.util.ArrayList;
import java.util.List;

public class Stop extends Location {

    static private long nextId = 1;

    private long id = nextId++;
    public long getId() {
        return id;
    }

    public Location getLocation() {
        return this;
    }

    static public enum Action {
        Drop,
        Pick,
        Stop
    };
    private Action action;

    public Action getAction() {
        return action;
    }

    private List<Order> orders = new ArrayList<>();
    public List<Order> getOrders() {
        return orders;
    }

    public boolean isPickup() {
        return action.equals(Action.Pick);
    }
    public boolean isDelivery() {
        return action.equals(Action.Drop);
    }

    @Override
    public String toString() {
        return Long.toString(getId()); // location.toString();
    }

    public Stop(Supplier supplier, List<Order> orders) {
        super(supplier.getLocation());

        action = Action.Pick;
        this.orders.addAll(orders);
    }

    public Stop(Consumer consumer, List<Order> orders) {
        super(consumer.getLocation());

        action = Action.Drop;
        this.orders.addAll(orders);
    }

//    public Stop(Consumer consumer, List<ConsumerOrder> consumerOrders) {
//        super(consumer.getLocation());
//
//        action = Action.Drop;
//        this.orders = consumerOrders;
//    }


}
