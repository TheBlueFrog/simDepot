package com.mike.sim;

import java.util.*;

/**
 * Created by mike on 6/18/2016.
 */
public class Constants {

//    static public final double MetersPerMile = 1609.34;

//    static public final double FarmRadius = 20 * MetersPerMile; // size of areas
//    static public final double CityRadius = 10 * MetersPerMile;
//
//    // location of areas
//    public static final Location CityCenter = new Location(-20 * MetersPerMile, 0);
//    public static final Location FarmCenter = new Location(+20 * MetersPerMile, 0);

//    // lat lon of map upper left
//    static public double MapLeft = -123.36;
//    static public double MapTop = 45.8;
//
//    // lat lon of map lower right
//    static public double MapRight = -121.6;
//    static public double MapBottom = 44.9;
//
//    // width/height of map
//    static public double MapWidthDeg = (MapRight - MapLeft);
//    static public double MapHeightDeg = (MapTop - MapBottom);
//
//    static public double MapWidthMeters = 133341.0;   // measured in Google Earth at mid-latitude
//    static public double MapHeightMeters = 116030.0;
//
//    static public double WindowX = 750;            // window size in pixels
//    static public double WindowY = 500;
//
//    public static int PorkDaysToSlaughter = (6 * 30);
//    public static int LambDaysToSlaughter = (6 * 30);
//    public static int BeefDaysToSlaughter = (15 * 30);
//
//
//    public static double meter2DegX(double dx) {
//        return dx / MapWidthMeters * MapWidthDeg;
//    }
//    public static double meter2DegY(double dy) {
//        return dy / MapHeightMeters * MapHeightDeg;
//    }
//
//    static public double deg2PixelX(double xDeg) { return ((xDeg - MapLeft) / MapWidthDeg) * WindowX; }
//    static public double deg2PixelY(double yDeg) {
//        return WindowY - (((yDeg - MapBottom) / MapHeightDeg) * WindowY);
//    }
//
//    static public double deg2MeterX(double xDeg) {
//        return (xDeg / MapWidthDeg) * MapWidthMeters;
//    }
//    static public double deg2MeterY(double yDeg) {
//        return (yDeg / MapHeightDeg) * MapHeightMeters;
//    }
//
//    static public double ScaleX = WindowX / MapWidthMeters;  // pixels/meter
//    static public double ScaleY = WindowY / MapHeightMeters;
//
//    // a simulation tick is this many simulated seconds
//    static public double SecondsPerSimulationTick = 60;

//    public static Random random = new Random(1327L);

    // the simulator dumps logging into a db for posterity
    public static String DBfname = "sim.db";

//    static public Location randomFarmLocation () {
//        double x = Constants.FarmCenter.x + (random.nextGaussian() * FarmRadius);
//        double y = Constants.FarmCenter.y + (random.nextGaussian() * FarmRadius);
//        x = Math.max(-MapWidth, Math.min(MapWidth, x));
//        y = Math.max(-MapHeight, Math.min(MapHeight, y));
//        return new Location(x, y);
//    }
//    static public Location randomCityLocation() {
//        double x = Constants.CityCenter.x + (random.nextGaussian() * CityRadius);
//        double y = Constants.CityCenter.y + (random.nextGaussian() * CityRadius);
//        x = Math.max(-MapWidth, Math.min(MapWidth, x));
//        y = Math.max(-MapHeight, Math.min(MapHeight, y));
//        return new Location (x, y);
//    }



}
