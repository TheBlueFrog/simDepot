package com.mike.sim;

import com.mike.routing.Metrics;
import com.mike.routing.Route;
import com.mike.util.Log;
import com.mike.util.NYIError;

import java.util.ArrayList;
import java.util.List;

public class Machine {
    private static final String TAG = Machine.class.getSimpleName();
    private final Algorithm algorithm;
    private int curInstruction;
    private List<Registers> stack = new ArrayList<>();

    public Machine(Algorithm algorithm) {

        this.algorithm = algorithm;
        curInstruction = 0;
    }

    public Metrics run(Route route) {
        Metrics metrics = new Metrics();

        while(true) {
            if (curInstruction >= algorithm.getNumInstructions())
                return metrics; // run off the end, shouldn't happen, supposed to be a Halt at the end

            Instruction instruction = algorithm.getInstruction(curInstruction);
            metrics.incInstructionCount();

            switch (instruction.getOpCode()) {
                case Halt:
                    return metrics;

                case NOP:
                    curInstruction++;
                    break;

                case Call:
                    try {
                        curInstruction++;
                        pushRegisters(curInstruction);
                        curInstruction = instruction.getAddress();
                    } catch (StackOverException e) {
                        Log.e(TAG, e.toString());
                        return metrics;
                    }
                    break;
                case Return:
                    try {
                        popRegisters();
                    }
                    catch (StackUnderflowException e) {
                        Log.e(TAG, e.toString());
                        return metrics;
                    }
                    break;

                default:
                    Log.e(TAG, "Not Yet Implemented");
                    throw new NYIError("not implemented");
            }
        }
    }

    private void pushRegisters(int curInstruction) throws StackOverException {
        if (stack.size() > 100)
            throw new StackOverException("");

        stack.add(new Registers(this, curInstruction));
    }
    private void popRegisters() throws StackUnderflowException {
        if (stack.size() < 1)
            throw new StackUnderflowException("");

        int i = stack.size()-1;
        Registers registers = stack.remove(i);

        curInstruction = registers.curInstruction;
    }
}
