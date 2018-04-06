package com.mike.sim;

import com.mike.util.Location;

import java.util.List;

/**
 * Created by mike on 6/24/2016.
 *
 * which stop we are enroute to in the route
 * we do not have tasks for travel before the first
 * stop or after the last stop
 */
public class TransporterTask {

    enum State {NotStarted, Enroute, Arrived };

    private State state = State.NotStarted;

    private int nextStop = 0;

    public TransporterTask(int nextStop) {
        this.nextStop = nextStop;
        this.state = State.NotStarted;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getNextStop() {
        return nextStop;
    }

}
