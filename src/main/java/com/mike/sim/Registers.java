package com.mike.sim;

public class Registers {
    public final int curInstruction;

    public Registers(Machine machine, int curInstruction) {
        this.curInstruction = curInstruction;
    }
}
