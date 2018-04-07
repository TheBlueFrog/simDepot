package com.mike.sim;

import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.mike.sim.Agent;
import com.mike.sim.AgentInfo;
import com.mike.sim.Framework;
import com.mike.util.Log;

/**
 * Created by mike on 6/17/2016.
 */
public class Main {

    private static final String TAG = Main.class.getSimpleName();

    public static Drawing drawing;
    private static Controls mControls;

    private static Framework mFramework;

    public static boolean animation = true;

    private static DB db;
    private static int scenario;

    static public DB getDB() { return db; }

    public static void main(String[] args)
    {
        {
            List<String> v = new ArrayList<String>(Arrays.asList(args));

            if (v.contains("-animation"))
                animation = true;
        }


        // get the DB open

//        try {
//            // on Windows devenv, db is up one from build output dir which should
//            // have the sqlite jar file...all broken
//
////            assert new File(f, "sqlite-jdbc-3.8.11.2.jar").exists();
//
////            db = new DB(new File(f, Constants.DBfname).getAbsolutePath());
            db = new DB(Constants.DBfname);

            scenario = 0;
//
//        } catch (ClassNotFoundException | SQLException e) {
//            e.printStackTrace();
//        }

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // create the controls and drawing windows
                mControls = new Controls();

                drawing = new Drawing(1.0);

                mFramework = new Framework(mAgents);
            }
        });
    }

    // all the agents that do the actual evaluation

    private static List<AgentInfo> mAgents = new ArrayList<>();
    static
    {
        mAgents.add(new AgentInfo(Clock.class, 1));
        mAgents.add(new AgentInfo(DBAgent.class, 1));

        mAgents.add(new AgentInfo(Scheduler.class, 1));

        mAgents.add(new AgentInfo(Consumer.class, 3));
        mAgents.add(new AgentInfo(Supplier.class, 3));
        mAgents.add(new AgentInfo(Transporter.class, 1));
    };

    public static void paint(final Graphics2D g2) {
//        Log.d(TAG, "in paint");
        mFramework.walk(new Framework.agentWalker() {
            @Override
            public void f(Agent a) {
                if (a instanceof PaintableAgent) {
                    ((PaintableAgent) a).paint(g2);
                }
            }
        });
    }

    public static void repaint() {
        if (animation)
            Main.drawing.mFrame.repaint();
    }

    public static Order generateOrder(Consumer consumer) {
        Supplier supplier = Supplier.pickSupplier(consumer);
        long itemId = supplier.pickItem(consumer).getId();
        return new Order(consumer, supplier, itemId, 1);

    }

    static private Random random = new Random(12739);
    public static Random getRandom() {
        return random;
    }

    public static int getScenario() {
        return scenario;
    }

    public static boolean getRunning() {
        return mControls.running;
    }
}
