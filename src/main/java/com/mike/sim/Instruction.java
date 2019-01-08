package com.mike.sim;

import com.mike.util.NYIError;

class Instruction {


    static enum OpCode {
        Halt,
        NOP,
        Call,
        Return,
        ;


        @Override
        public String toString() {
            switch (this) {
                case Halt: return "Halt";
                case NOP: return "NOP";
                case Call: return "Call";
                case Return: return "Return";

                default: throw new NYIError("not implemented");
            }
        }
    }

    private final OpCode opCode;
    public OpCode getOpCode() {
        return opCode;
    }

    private final int address;
    public int getAddress() {
        return address;
    }


    public Instruction(OpCode opCode) {
        this.opCode = opCode;
        this.address = 0;
    }
    public Instruction(OpCode opCode, int address) {
        this.opCode = opCode;
        this.address = address;
    }

    // copy constructor
    public Instruction(Instruction instruction) {
        this.opCode = instruction.opCode;
        this.address = instruction.address;
    }


    @Override
    public String toString() {
        switch (opCode) {
            case NOP:
            case Return:
            case Halt:
                return String.format("%-6s", opCode.toString());

            case Call:
                return String.format("%-6s  %4d", opCode.toString(), address);
        }

        return "??";
    }
}
