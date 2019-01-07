package com.mike.sim;

import com.mike.routing.Metrics;
import com.mike.routing.Route;

import java.util.ArrayList;
import java.util.List;

public class Algorithm {

    private static int Return = 0;

    private static class Instruction {

        private final int opCode;

        public Instruction(int opCode) {
            this.opCode = opCode;
        }
    };

    private List<Instruction> instructions = new ArrayList<>();

    public Algorithm() {
        instructions.add(new Instruction(Return));
    }

    public Metrics evaluate(Route route) {
        Metrics metrics = new Metrics(route);
        return metrics;
    }

    public void breed() {

    }

    public void reap() {

    }
}
