package com.mike.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by mike on 6/17/2016.
 * we assume a flat rectangular coodinate system, e.g. not
 * factoring in spherical earth
 *
 * we do work in Lat/Lon however
 */
public class Location {
    public double x;
    public double y;

    // setup map where the objects are located

    // lat lon of map upper left
    static public double MapLeft = -123.36;
    static public double MapTop = 45.8;

    // lat lon of map lower right
    static public double MapRight = -121.6;
    static public double MapBottom = 44.9;

    // width/height of map
    static public double MapWidthDeg = (MapRight - MapLeft);
    static public double MapHeightDeg = (MapTop - MapBottom);

    static public double MapWidthMeters = 133341.0;   // measured in Google Earth at mid-latitude
    static public double MapHeightMeters = 116030.0;

    public static double meter2DegX(double dx) {
        return dx / MapWidthMeters * MapWidthDeg;
    }
    public static double meter2DegY(double dy) {
        return dy / MapHeightMeters * MapHeightDeg;
    }

    static public double deg2MeterX(double xDeg) {
        return (xDeg / MapWidthDeg) * MapWidthMeters;
    }
    static public double deg2MeterY(double yDeg) {
        return (yDeg / MapHeightDeg) * MapHeightMeters;
    }

    static public Location MapCenter = null;

    static {
        MapCenter = new Location(MapLeft + (MapWidthDeg / 2.0), MapBottom + (MapHeightDeg / 2.0));
    }


    /**
     *
     * @param lat coordinates of a location
     * @param lon
     */
    public Location(double lon, double lat) {
        this.x = lon;
        this.y = lat;
    }

    public Location(Location location) {
        this.x = location.x;
        this.y = location.y;
    }

    /**
     * @param location
     * @return distance, in meters, between this location and another location
     */
    public double distance(Location location) {
        // location is in lat/lon
//        return (distance(y, x, location.y, location.x));
//    }
        double dx = deg2MeterX(this.x - location.x);
        double dy = deg2MeterY(this.y - location.y);
        return Math.sqrt((dx * dx) + (dy * dy));
    }

    static private double d2r = (Math.PI / 180.0);

//    //calculate haversine distance for linear distance, in meters
//    double distance(double lat1, double long1, double lat2, double long2)
//    {
//        double dlong = (long2 - long1) * d2r;
//        double dlat = (lat2 - lat1) * d2r;
//        double a = Math.pow(Math.sin(dlat/2.0), 2) + Math.cos(lat1*d2r) * Math.cos(lat2*d2r) * Math.pow(Math.sin(dlong/2.0), 2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
//        double d = 6367 * c;
//
//        return d * 1000.0;
//    }

//        double haversine_mi(double lat1, double long1, double lat2, double long2)
//        {
//            double dlong = (long2 - long1) * d2r;
//            double dlat = (lat2 - lat1) * d2r;
//            double a = pow(sin(dlat/2.0), 2) + cos(lat1*d2r) * cos(lat2*d2r) * pow(sin(dlong/2.0), 2);
//            double c = 2 * atan2(sqrt(a), sqrt(1-a));
//            double d = 3956 * c;
//
//            return d;
//        }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (! Location.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final Location other = (Location) obj;

        if ((this.x != other.x)) {
            return false;
        }
        if ((this.y != other.y)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = (int) (53 * hash + this.x);
        hash = (int) (53 * hash + this.y);
        return hash;
    }

    /**
     * move so many meters, remember, locations are in Lon/Lat
     * @param dx
     * @param dy
     */
    public void moveMeters(double dx, double dy) {
        x += meter2DegX(dx);
        y += meter2DegY(dy);
    }

    /**
     * @param holeRadiusM the locations have a hole in
     *                  the middle and are randomly distributed in a band around the hole, this is
     *                  the radium of the hole, in meters
     * @param radiusM   radius of populated band, in meters
     * @return          random location
     */
    public static Location getRandomLoc(double holeRadiusM, double radiusM, Random random) {
        double theta = random.nextDouble() * 2 * Math.PI;
        double r = random.nextDouble() * radiusM;
        r += holeRadiusM;
        double x = Math.cos(theta) * r;
        double y = Math.sin(theta) * r;

        Location loc = new Location(
                MapCenter.x + meter2DegX(x),
                MapCenter.y + meter2DegY(y));
        return loc;
    }

    /**
     * @param radiusM   radius of a circle with 1 std dev of locations
     * @return          random location
     */
    public static Location getRandomLoc(double radiusM, Random random) {
        double theta = random.nextDouble() * 2 * Math.PI;
        double r = random.nextDouble() * radiusM;
        double x = Math.cos(theta) * r;
        double y = Math.sin(theta) * r;

        Location loc = new Location(
                MapCenter.x + meter2DegX(x),
                MapCenter.y + meter2DegY(y));
        return loc;
    }
}
