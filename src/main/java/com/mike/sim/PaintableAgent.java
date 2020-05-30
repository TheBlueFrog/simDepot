package com.mike.sim;

import com.mike.sim.Agent;
import com.mike.sim.Framework;

import java.awt.*;

/**
 * Created by mike on 6/17/2016.
 */
abstract public class PaintableAgent extends Agent {

    static private final String TAG = PaintableAgent.class.getSimpleName();

    @Override
    protected String getClassName() {
        return null;
    }

    /**
     *
     * @param g2    graphics context to use to paint this agent
     */
    protected abstract void paint(Graphics2D g2);

    public PaintableAgent(Framework f, Long id) {
        super(f, id);
    }

}
