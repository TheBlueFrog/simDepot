package com.mike.routing;

import java.util.*;
import java.util.stream.Collectors;

public class AnnealData {
    private Random random = new Random(123775L);

    private List<Stop> stops = new ArrayList<>();

    private Route route = null;
    public Route getRoute() {
        return route;
    }

    public AnnealData() {
    }

    public Random getRandom() {
        return random;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public boolean isDebug() {
        return false;
    }

}
