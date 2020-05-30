package com.mike.util;

import com.mike.sim.Main;

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

    static public double MapLeft = 0;
    static public double MapTop = 1000;

    static public double MapRight = 1000;
    static public double MapBottom = 0;

    static public double MapWidth = (MapRight - MapLeft);
    static public double MapHeight = (MapTop - MapBottom);

    public Location(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Location(Location location) {
        this.x = location.x;
        this.y = location.y;
    }
	
	public static Location getRandom(Location center, double radius) {
    	double t = Main.getRandom().nextDouble() * (2.0 * Math.PI);
    	double x = Math.cos(t) * radius;
    	double y = Math.sin(t) * radius;
		return new Location(center.x + x, center.y + y);
	}
	
	public double distance(Location location) {
        double dx = this.x - location.x;
        double dy = this.y - location.y;
        return Math.sqrt((dx * dx) + (dy * dy));
    }

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

}
