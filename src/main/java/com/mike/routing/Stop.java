package com.mike.routing;

import com.mike.util.Location;

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
        Pick
    };
    private Action action;
    public Action getAction() {
        return action;
    }

    private final Item item;

    @Override
    public String toString() {
        return Long.toString(getId()); // location.toString();
    }

    public Stop(Location location, Action action, Item item) {
        super(location);

        this.action = Action.Pick;
        this.item = item;
    }

}
