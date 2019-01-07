package com.mike.routing;

import java.util.*;
import java.util.stream.Collectors;

public class RouteData {
    private Random random = new Random(123775L);
    public Random getRandom() {
        return random;
    }

    private List<Stop> stops = new ArrayList<>();
    public List<Stop> getStops() {
        return stops;
    }

    private Route route = null;
    public Route getRoute() {
        return route;
    }

    public boolean isDebug() {
        return false;
    }

    public RouteData() {
    }

}
