package com.mike.sim;

import com.mike.util.Log;
import com.mike.util.LogImp;

/**
 * Created by mike on 6/17/2016.
 *
 * A global clock for simulation time
 */
public class Clock extends Agent {

    static private final String TAG = Clock.class.getSimpleName();

    static private long time = 0;
    static public long getTime () { return time; }

    @Override
    protected String getClassName() {
        return null;
    }


    public Clock(Framework f, Long serialNumber) {
        super(f, serialNumber);

        assert serialNumber == 0; // singleton

        LogImp _d = new LogImp() {
            @Override
            public void d(String tag, String msg) {
                System.out.println(String.format("%8d %30s: %s", time, tag, msg));
            }
        };
        LogImp _e = new LogImp() {
            @Override
            public void d(String tag, String msg) {
                System.out.println(String.format("%8d %30s: ERROR %s", time, tag, msg));
            }
        };

        Log.set_d(_d);
        Log.set_e(_e);

        register();
    }

    @Override
    protected void onMessage(Message msg) {

        assert msg.targetSerialNumber == this.getSerialNumber();

        if ((msg.sender == null) && (((Framework.State) msg.message)).equals(Framework.State.AgentsRunning)) {
            // frameworks says everyone is ready, start clock ticking
            send(new Message(this, Clock.class, 0, null));
            return;
        }

        if (msg.sender instanceof Clock) {
            time++;

            doClock();

            try {
                sleep(Main.animation ? 1 : 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // next tick
            send(new Message(this, Agent.class, 0, null));
        }
    }

    private void doClock () {
	    Main.drawing.mFrame.repaint();
    }

}
