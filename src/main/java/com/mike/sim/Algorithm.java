package com.mike.sim;

import com.mike.routing.Metrics;
import com.mike.routing.Route;
import com.mike.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Algorithm {

    private static final String TAG = Algorithm.class.getSimpleName();

    private List<Instruction> instructions = new ArrayList<>();

    public Algorithm(Algorithm partner) {
        partner.instructions.forEach(instruction -> instructions.add(new Instruction(instruction)));
    }

    public Instruction getInstruction(int i) {
        return instructions.get(i);
    }
    public int getNumInstructions() {
        return instructions.size();
    }

    public Algorithm(int maxInitialLength, Random random) {

        instructions.add(new Instruction(Instruction.OpCode.NOP));
        instructions.add(new Instruction(Instruction.OpCode.Call, 5));
        instructions.add(new Instruction(Instruction.OpCode.Halt));
        instructions.add(new Instruction(Instruction.OpCode.NOP));
        instructions.add(new Instruction(Instruction.OpCode.NOP));
        instructions.add(new Instruction(Instruction.OpCode.Return));


//        // start with some random code
//        for(int i = 0; i < maxInitialLength; ++i) {
//            int j = random.nextInt(Instruction.OpCode.values().length);
//            instructions.add(new Instruction(Instruction.OpCode.values()[j]));
//        }

        instructions.add(new Instruction(Instruction.OpCode.Halt));
    }

    public Metrics evaluate(Route route) {

        Machine machine = new Machine(this);
        Metrics metrics = machine.run(route);

        Log.d(TAG, String.format("Evaluate [%s], %s", route.toString(), metrics.toString()));
        return metrics;
    }

    public void breed(Metrics metrics) {
        double fitness = metrics.getFitness();
        double prob = ;
        if (prob > threshold) {
            Algorithm partner = findPartner(this);
            Algorithm child = new Algorithm(partner);
        }
    }

    public void reap(Metrics metrics) {

    }

}
