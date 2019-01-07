package com.mike.sim;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.mike.routing.Route;
import com.mike.routing.RouteData;

/**
 * Created by mike on 6/17/2016.
 */
public class Main {

    private static final String TAG = Main.class.getSimpleName();

    public static Drawing drawing;
    private static Controls mControls;

    private static Framework mFramework;

    public static boolean animation = true;

    // all the agents that do the actual evaluation

    private static List<AgentInfo> mAgents = new ArrayList<>();
    private static int TargetPopulationSize = 10;

    static
    {
        mAgents.add(new AgentInfo(Clock.class, 1));
    };

    public static void main(String[] args)
    {
        {
            List<String> v = new ArrayList<String>(Arrays.asList(args));

            if (v.contains("-animation"))
                animation = true;

            doIt();
        }

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

    static private List<Algorithm> population = new ArrayList<>();

    private static void doIt() {
        boolean evolving = true;
        List<Route> routes = initializeRoutes();

        initialize(population);
        
        while (evolving) {
            population.forEach( algorithm -> {
                routes.forEach(route -> algorithm.evaluate(route));
            });
            
            population.forEach( Algorithm::reap);
            population.forEach( Algorithm::breed);

            routes.forEach(Route::reset);
        }
    }

    private static void initialize(List<Algorithm> population) {
        for(int i = 0; i < TargetPopulationSize; ++i) {
            population.add(new Algorithm());
        }
    }

    private static List<Route> initializeRoutes() {
        List<Route> routes = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            routes.add(new Route(new RouteData()));
        }
        return routes;
    }


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

    static private Random random = new Random(12739);
    public static Random getRandom() {
        return random;
    }

    public static boolean getRunning() {
        return mControls.running;
    }
}
