package com.mike.sim;

import com.mike.sim.Framework;
import com.mike.sim.Message;

import java.awt.*;
import java.util.*;

/**
 * Created by mike on 6/17/2016.
 */
public abstract class LocatedAgent extends PaintableAgent {

    static private final String TAG = LocatedAgent.class.getSimpleName();

    private Location location;

    @Override
    protected String getClassName() {
        return null;
    }

    public LocatedAgent(Framework f, long serialNumber) {
        super(f, serialNumber);

        location = new Location(0, 0);
    }

//    @Override
//    protected void onMessage(Message msg) {
//    }

    protected Location getLocation () { return new Location(location); }
    protected void setLocation (Location location) { this.location = new Location(location); }

}
