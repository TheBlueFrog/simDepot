package com.mike.sim;

import java.awt.*;
import java.util.*;
import java.util.List;

import com.mike.agents.Consumer;
import com.mike.agents.Supplier;

/**
 * Created by mike on 6/17/2016.
 */
public class Main {

    private static final String TAG = Main.class.getSimpleName();

    public static Drawing drawing;
    private static Controls mControls;

    private static Framework mFramework;

    public static boolean animation = true;

    private static List<AgentInfo> agents = new ArrayList<>();

    static
    {
        agents.add(new AgentInfo(Clock.class, 1));
        agents.add(new AgentInfo(Consumer.class, 1));
		agents.add(new AgentInfo(Supplier.class, 1));
    };

    public static void main(String[] args)
    {
        {
            List<String> v = new ArrayList<String>(Arrays.asList(args));

            if (v.contains("-animation"))
                animation = true;

//            doIt();
        }

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // create the controls and drawing windows
                mControls = new Controls();

                drawing = new Drawing(1.0);

                mFramework = new Framework(agents);
            }
        });
    }

    static private List<Algorithm> population = new ArrayList<>();

//    private static void doIt() {
//        boolean evolving = true;
//        List<Route> routes = initializeRoutes();
//
//        initialize(population);
//        Map<Algorithm, Metrics> evaluations = new HashMap<>();
//
////        while (evolving) {
//		for(int j = 0; j < 100; ++j) {
//            population.forEach( algorithm -> {
//                routes.forEach(route ->
//                        evaluations.put(algorithm, algorithm.evaluate(route)));
//            });
//
//            population.forEach( algorithm -> algorithm.reap(evaluations.get(algorithm)));
//            population.forEach( algorithm -> algorithm.breed(evaluations.get(algorithm)));
//
//            routes.forEach(Route::reset);
//        }
//    }
//
//    private static void initialize(List<Algorithm> population) {
//        for(int i = 0; i < TargetPopulationSize; ++i) {
//            population.add(new Algorithm(10, random));
//        }
//    }
//
//    private static List<Route> initializeRoutes() {
//        List<Route> routes = new ArrayList<>();
//        for (int i = 0; i < 10; ++i) {
//            routes.add(new Route());
//        }
//        return routes;
//    }


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
