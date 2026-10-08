package com.example.PeepholeOptimizer;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CompilerService {

    private final String clang = "/opt/homebrew/opt/llvm/bin/clang";
    private final String opt = "/opt/homebrew/opt/llvm/bin/opt";

    public OptimizationResult analyze(String code) throws IOException, InterruptedException {

        Path tempDir = Files.createTempDirectory("peephole");
        Path cFile = tempDir.resolve("test.c");
        Path beforeFile = tempDir.resolve("before.ll");
        Path afterFile = tempDir.resolve("after.ll");

        Files.writeString(cFile, code);

        Process clangProcess = new ProcessBuilder(
                clang,
                "-S",
                "-emit-llvm",
                "-O0",
                "-Xclang",
                "-disable-O0-optnone",
                cFile.toString(),
                "-o",
                beforeFile.toString()
        ).redirectErrorStream(true).start();

        String clangOutput = new String(
                clangProcess.getInputStream().readAllBytes()
        );

        int clangExit = clangProcess.waitFor();

        if (clangExit != 0) {
            throw new RuntimeException(
                    "Compilation failed:\n" + clangOutput
            );
        }

        Process optProcess = new ProcessBuilder(
                opt,
                "-S",
                "-passes=instcombine",
                beforeFile.toString(),
                "-o",
                afterFile.toString()
        ).redirectErrorStream(true).start();

        String optOutput = new String(
                optProcess.getInputStream().readAllBytes()
        );

        int optExit = optProcess.waitFor();

        if (optExit != 0) {
            throw new RuntimeException(
                    "Optimization failed:\n" + optOutput
            );
        }

        String before = Files.readString(beforeFile);
        String after = Files.readString(afterFile);

        int beforeCount = countInstructions(before);
        int afterCount = countInstructions(after);
        int arithmeticCount = countArithmeticInstructions(before);

        double reduction = 0;

        if (beforeCount > 0) {
            reduction =
                    ((double) (beforeCount - afterCount) / beforeCount) * 100;
        }

        List<CandidateInstruction> candidates =
                extractCandidates(before);

        List<String> optimizations =
                detectOptimizations(before);

        return new OptimizationResult(
                before,
                after,
                beforeCount,
                afterCount,
                arithmeticCount,
                reduction,
                optimizations,
                candidates
        );
    }

    private int countInstructions(String ir) {

        Pattern pattern = Pattern.compile(
                "^\\s*(%[\\w.]+\\s*=|ret\\b|store\\b|load\\b|br\\b|call\\b)",
                Pattern.MULTILINE
        );

        Matcher matcher = pattern.matcher(ir);

        int count = 0;

        while (matcher.find()) {
            count++;
        }

        return count;
    }

    private int countArithmeticInstructions(String ir) {

        Pattern pattern = Pattern.compile(
                "^\\s*%[\\w.]+\\s*=\\s*(add|sub|mul|sdiv|udiv|and|or|xor)\\b",
                Pattern.MULTILINE
        );

        Matcher matcher = pattern.matcher(ir);

        int count = 0;

        while (matcher.find()) {
            count++;
        }

        return count;
    }

    private List<CandidateInstruction> extractCandidates(String ir) {

        List<CandidateInstruction> candidates = new ArrayList<>();

        Pattern pattern = Pattern.compile(
                "^\\s*(%[\\w.]+\\s*=\\s*(add|sub|mul|sdiv|udiv|and|or|xor)\\s+[^\\n]+)",
                Pattern.MULTILINE
        );

        Matcher matcher = pattern.matcher(ir);

        while (matcher.find()) {

            String instruction = matcher.group(1).trim();
            String operation = matcher.group(2);

            String patternName;
            String simplification;
            String explanation;

            switch (operation) {

                case "add":
                    patternName = "Addition by zero";
                    simplification = "a + 0 → a";
                    explanation = "The instruction adds zero to a value, which may be simplified to the original value.";
                    break;

                case "sub":
                    patternName = "Subtraction by zero";
                    simplification = "a - 0 → a";
                    explanation = "The instruction subtracts zero from a value, which may be simplified to the original value.";
                    break;

                case "mul":
                    patternName = "Multiplication by one";
                    simplification = "a × 1 → a";
                    explanation = "The instruction multiplies a value by one, which may be simplified to the original value.";
                    break;

                case "sdiv":
                case "udiv":
                    patternName = "Division candidate";
                    simplification = "a ÷ 1 → a";
                    explanation = "The instruction performs integer division and may contain a simplifiable constant operand.";
                    break;

                case "and":
                    patternName = "Bitwise AND candidate";
                    simplification = "a & 0 → 0";
                    explanation = "A bitwise AND with zero can potentially be simplified to zero.";
                    break;

                case "or":
                    patternName = "Bitwise OR candidate";
                    simplification = "a | 0 → a";
                    explanation = "A bitwise OR with zero can potentially be simplified to the original value.";
                    break;

                case "xor":
                    patternName = "Bitwise XOR candidate";
                    simplification = "a ^ 0 → a";
                    explanation = "A bitwise XOR with zero can potentially be simplified to the original value.";
                    break;

                default:
                    patternName = "Arithmetic candidate";
                    simplification = "Potential simplification";
                    explanation = "This instruction was extracted as an arithmetic or bitwise candidate.";
            }

            candidates.add(
                    new CandidateInstruction(
                            instruction,
                            operation,
                            patternName,
                            simplification,
                            explanation
                    )
            );
        }

        return candidates;
    }

    private List<String> detectOptimizations(String ir) {

        List<String> optimizations = new ArrayList<>();

        Pattern addZero = Pattern.compile(
                "\\badd\\b[^\\n]*,\\s*0\\b"
        );

        Pattern subZero = Pattern.compile(
                "\\bsub\\b[^\\n]*,\\s*0\\b"
        );

        Pattern mulOne = Pattern.compile(
                "\\bmul\\b[^\\n]*,\\s*1\\b"
        );

        Pattern divOne = Pattern.compile(
                "\\b(sdiv|udiv)\\b[^\\n]*,\\s*1\\b"
        );

        Pattern mulZero = Pattern.compile(
                "\\bmul\\b[^\\n]*,\\s*0\\b"
        );

        Pattern andZero = Pattern.compile(
                "\\band\\b[^\\n]*,\\s*0\\b"
        );

        Pattern orZero = Pattern.compile(
                "\\bor\\b[^\\n]*,\\s*0\\b"
        );

        Pattern xorZero = Pattern.compile(
                "\\bxor\\b[^\\n]*,\\s*0\\b"
        );

        if (addZero.matcher(ir).find()) {
            optimizations.add("Addition by zero: a + 0 → a");
        }

        if (subZero.matcher(ir).find()) {
            optimizations.add("Subtraction by zero: a − 0 → a");
        }

        if (mulOne.matcher(ir).find()) {
            optimizations.add("Multiplication by one: a × 1 → a");
        }

        if (divOne.matcher(ir).find()) {
            optimizations.add("Division by one: a ÷ 1 → a");
        }

        if (mulZero.matcher(ir).find()) {
            optimizations.add("Multiplication by zero: a × 0 → 0");
        }

        if (andZero.matcher(ir).find()) {
            optimizations.add("Bitwise AND with zero: a & 0 → 0");
        }

        if (orZero.matcher(ir).find()) {
            optimizations.add("Bitwise OR with zero: a | 0 → a");
        }

        if (xorZero.matcher(ir).find()) {
            optimizations.add("Bitwise XOR with zero: a ^ 0 → a");
        }

        return optimizations;
    }
}