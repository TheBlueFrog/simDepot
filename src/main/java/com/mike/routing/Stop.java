package com.mike.routing;

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

    public Stop(Location location, Action action) {
        super(location);

        action = Action.Pick;
    }

}
