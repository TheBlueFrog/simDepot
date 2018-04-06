package com.mike.routing;

import com.mike.sim.Consumer;
import com.mike.sim.Order;
import com.mike.sim.Supplier;

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

    public void setupRun(List<Order> orders) {

        Map<Supplier, List<Order>> bySupplier = orders.stream()
                .collect(Collectors.groupingBy(order -> order.getSupplier()));

        Map<Consumer, List<Order>> byConsumer = orders.stream()
                .collect(Collectors.groupingBy(order -> order.getConsumer()));

        // provide an initial ordering to the stops that
        // ensures it's a valid route, e.g. all pick before any drop

        bySupplier.keySet().forEach(supplier -> stops.add(
                new Stop(supplier, bySupplier.get(supplier))));

        byConsumer.keySet().forEach(consumer -> stops.add(
                new Stop(consumer, byConsumer.get(consumer))));
    }

//    public void setupDepotRun(List<Order> orders) {
//
//        Map<Supplier, List<Order>> bySupplier = orders.stream()
//                .collect(Collectors.groupingBy(order -> order.getSupplier()));
//
//        Map<Depot, List<Order>> byConsumer = orders.stream()
//                .collect(Collectors.groupingBy(order -> order.getDepot()));
//
//        // provide an initial ordering to the stops that
//        // ensures it's a valid route, e.g. all pick before any drop
//
//        bySupplier.keySet().forEach(supplier -> stops.add(new Stop(supplier, bySupplier.get(supplier))));
//        byConsumer.keySet().forEach(consumer -> stops.add(new Stop(consumer, byConsumer.get(consumer))));
//    }

    public Random getRandom() {
        return random;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public boolean debug() {
        return false;
    }

    public void anneal() {
        Annealer annealer = new Annealer(this);
        route = annealer.anneal();
    }
}
