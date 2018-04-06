package com.mike.sim;

import com.mike.sim.Agent;
import com.mike.sim.Framework;
import com.mike.sim.Message;
import com.mike.util.Log;
import com.mike.util.LogImp;
import javafx.util.Pair;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mike on 6/17/2016.
 *
 * A global clock for simulation time
 */
public class Clock extends Agent {

    static private final String TAG = Clock.class.getSimpleName();

    // the time, in seconds since midnight of the first day of
    // the run
    static private long time = 0;

    static public long getTime () { return time; }

    /**
     * @return current time of day (in seconds), days are all exactly 24hrs long
     */
    static public long getTimeOfDay() { return getTimeOfDay(time); }
    static public long getTimeOfDay(long t) {
        return t % (60 * 60 * 24);
    }

    /**
     * @return current time=of=day (in minutes), see getTimeOfDay
     */
    public static int getTimeOfDayInMinutes() { return getTimeOfDayInMinutes(time); }
    public static int getTimeOfDayInMinutes(long t) { return (int) (getTimeOfDay(t) / 60); }

    public static int getHourOfDay() { return getHourOfDay(time); }
    public static int getHourOfDay(long t) { return (int) (getTimeOfDay(t) / (60 * 60)); }

    public static int getMinuteOfHour()
    {
        return getMinuteOfHour(time);
    }
    public static int getMinuteOfHour(long t)
    {
        return (int) ((getTimeOfDay(t) / 60) - (getHourOfDay(t) * 60));
    }

    /**
     * @return current day-of-week, all weeks are exactly 7 days long
     */
    static public long getDayOfWeek() {
        return getDayOfWeek(time);
    }
    static public long getDayOfWeek(long t) { return getDay(t) % 7; }

    /*
    current day-of-month, all months are exactly 28 days long (makes life simple)
     */
    static public long getDayOfMonth() {
        return getDayOfMonth(time);
    }
    static public long getDayOfMonth(long t) { return (getDay(t) % 28); }

    // current day since start of run
    static public long getDay() { return getDay(time); }
    static public long getDay(long t) {
        return (t / (60 * 60 * 24));
    }


    private List<Agent> subscribers = new ArrayList<>();


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
                System.out.println(String.format("%4d %02d:%02d %30s  %s",
                        getDay(),
                        getHourOfDay(),
                        getMinuteOfHour(),
                        tag, msg));
            }
        };
        LogImp _e = new LogImp() {
            @Override
            public void d(String tag, String msg) {
                System.out.println(String.format("%%4d 02d:%02d %30s  ERROR %s",
                        getDay(),
                        getHourOfDay(),
                        getMinuteOfHour(),
                        tag, msg));
            }
        };

        Log.set_d(_d);
        Log.set_e(_e);

        register();
    }

    @Override
    protected void onMessage(Message msg) {

        assert msg.serialNumber == this.getSerialNumber();

        if ((msg.mSender == null) && (((Framework.State) msg.mMessage)).equals(Framework.State.AgentsRunning)) {
            // frameworks says everyone is ready, start clock ticking
            send(new Message(this, Clock.class, 0, null));
            return;
        }

        if (msg.mSender instanceof Clock) {
            // just talking to myself

            time += Constants.SecondsPerSimulationTick; // each tick moves simulation clock this many seconds

            for (Agent a : subscribers)
                send(new Message(this, a.getClass(), a.getSerialNumber(), (Long) time));

            try {
                sleep(Main.animation ? 1 : 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // next tick
            send(new Message(this, Clock.class, 0, null));
        }
        else {
            switch ((String) msg.mMessage) {
                case "subscribe":
                    subscribers.add(msg.mSender);
                    break;

                case "unsubscribe": {
                    for (Agent a : subscribers)
                        if (a.getID().equals(msg.mSender.getID()))
                            subscribers.remove(a);
                }
                break;
                default:
                    Log.e(TAG, "Unknown message " + (String) msg.mMessage);
                    break;
            }
        }
    }

    public static String formatAsHM(long t) {
        int hr = (int) (t / (60 * 60));
        int mn = (int) ((t / 60) - (hr * 60));
        return String.format("%02d:%02d", hr, mn);
    }

    /**
     * @param t
     * @return time-of-day as a real number, e.g. noon is 12.00, 18:15 as 18.25
     */
    public static double getTimeOfDayDouble() {
        return getTimeOfDayDouble(time);
    }
    public static double getTimeOfDayDouble(long t) {
        return (double) t / (double) (60 * 60);
    }
}
