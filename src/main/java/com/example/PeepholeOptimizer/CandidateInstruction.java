package com.example.PeepholeOptimizer;

public class CandidateInstruction {

    private final String instruction;
    private final String operation;
    private final String pattern;
    private final String simplification;
    private final String explanation;

    public CandidateInstruction(
            String instruction,
            String operation,
            String pattern,
            String simplification,
            String explanation) {

        this.instruction = instruction;
        this.operation = operation;
        this.pattern = pattern;
        this.simplification = simplification;
        this.explanation = explanation;
    }

    public String getInstruction() {
        return instruction;
    }

    public String getOperation() {
        return operation;
    }

    public String getPattern() {
        return pattern;
    }

    public String getSimplification() {
        return simplification;
    }

    public String getExplanation() {
        return explanation;
    }
}