package com.mike.routing;

import com.mike.util.Location;

import java.util.List;

public class Metrics {
    private double totalDistance = 0;
    private int numInstructions = 0;
    private int totalInstructions = 0;

    public Metrics() {

    }

    public void incInstructionCount() {
        totalInstructions++;
    }

    @Override
    public String toString() {
        return String.format("Metrics: %5d instructions", totalInstructions);
    }

    public double getFitness() {
        return 0;
    }
}
