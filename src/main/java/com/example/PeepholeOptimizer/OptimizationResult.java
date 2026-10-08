package com.example.PeepholeOptimizer;

import java.util.List;

public class OptimizationResult {

    private final String before;
    private final String after;
    private final int beforeCount;
    private final int afterCount;
    private final int arithmeticCount;
    private final double reduction;
    private final List<String> optimizations;
    private final List<CandidateInstruction> candidates;

    public OptimizationResult(
            String before,
            String after,
            int beforeCount,
            int afterCount,
            int arithmeticCount,
            double reduction,
            List<String> optimizations,
            List<CandidateInstruction> candidates) {

        this.before = before;
        this.after = after;
        this.beforeCount = beforeCount;
        this.afterCount = afterCount;
        this.arithmeticCount = arithmeticCount;
        this.reduction = reduction;
        this.optimizations = optimizations;
        this.candidates = candidates;
    }

    public String getBefore() {
        return before;
    }

    public String getAfter() {
        return after;
    }

    public int getBeforeCount() {
        return beforeCount;
    }

    public int getAfterCount() {
        return afterCount;
    }

    public int getArithmeticCount() {
        return arithmeticCount;
    }

    public double getReduction() {
        return reduction;
    }

    public List<String> getOptimizations() {
        return optimizations;
    }

    public List<CandidateInstruction> getCandidates() {
        return candidates;
    }
}