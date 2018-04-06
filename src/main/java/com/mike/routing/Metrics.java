package com.mike.routing;

import com.mike.util.Location;

import java.util.List;

public class Metrics {
    private List<Stop> stops;
    private boolean needCompute;
    private double totalDistance;

    public Metrics(List<Stop> stops) {
        needCompute = true;
        this.stops = stops;
    }

    public double getCost() {
        if (needCompute) {
            needCompute = false;
            double d = 0;
            Location loc = stops.get(0).getLocation();
            for (int i = 1; i < stops.size(); ++i) {
                d += loc.distance(stops.get(i).getLocation());
                loc = stops.get(i).getLocation();
            }
            totalDistance = d;
        }
        return totalDistance;
    }

    public void needCompute() {
        needCompute = true;
    }
}
