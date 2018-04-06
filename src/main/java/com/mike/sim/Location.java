package com.mike.sim;

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

    /**
     *
     * @param lat coordinates of a location
     * @param lon
     */
    public Location (double lon, double lat) {
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
        double dx = Constants.deg2MeterX(this.x - location.x);
        double dy = Constants.deg2MeterY(this.y - location.y);
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
     * move so many meters, locations are in Lon/Lat
     * @param dx
     * @param dy
     */
    public void moveMeters(double dx, double dy) {
        x += Constants.meter2DegX(dx);
        y += Constants.meter2DegY(dy);
    }
}
