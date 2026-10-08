# Peephole Optimizer

An LLVM-based web application for analyzing and demonstrating peephole optimization on C programs.

## Overview

Peephole optimization is a compiler optimization technique that examines a small sequence of intermediate instructions and replaces redundant or unnecessary operations with simpler equivalent instructions.

This project provides a simple web interface where a user can enter a C program and analyze how LLVM's `InstCombine` optimization pass transforms the generated LLVM IR.

The application:

1. Accepts C source code
2. Compiles the code into LLVM Intermediate Representation
3. Extracts arithmetic and bitwise instruction candidates
4. Applies LLVM InstCombine
5. Compares the LLVM IR before and after optimization
6. Detects common peephole optimization patterns
7. Displays the optimization results through a web interface

## Project Workflow

```text
C Program
    ↓
Spring Boot Web Application
    ↓
Clang
    ↓
LLVM IR Before Optimization
    ↓
Candidate Instruction Extraction
    ↓
LLVM InstCombine
    ↓
LLVM IR After Optimization
    ↓
Comparison & Analysis
    ↓
Web Interface
```

## Example

### Input

```c
int calculate(int a) {
    int x = a + 0;
    int y = x * 1;
    int z = y - 0;
    return z;
}
```

### Before Optimization

The generated LLVM IR contains redundant arithmetic operations such as:

```text
add ... , 0
mul ... , 1
sub ... , 0
```

### After Optimization

LLVM InstCombine simplifies the computation to an equivalent result:

```llvm
ret i32 %0
```

The redundant arithmetic instructions are removed.

## Supported Optimization Patterns

The application detects common patterns including:

- Addition by zero: `a + 0 → a`
- Subtraction by zero: `a - 0 → a`
- Multiplication by one: `a × 1 → a`
- Division by one: `a ÷ 1 → a`
- Multiplication by zero: `a × 0 → 0`
- Bitwise AND with zero: `a & 0 → 0`
- Bitwise OR with zero: `a | 0 → a`
- Bitwise XOR with zero: `a ^ 0 → a`

## Technologies Used

- Java
- Spring Boot
- Thymeleaf
- HTML
- CSS
- LLVM
- Clang
- LLVM InstCombine
- Maven
- Git & GitHub
- IntelliJ IDEA

## Project Structure

```text
PeepholeOptimizer
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.example.PeepholeOptimizer
│   │   │       ├── CandidateInstruction.java
│   │   │       ├── CompilerService.java
│   │   │       ├── HomeController.java
│   │   │       ├── OptimizationResult.java
│   │   │       └── PeepholeOptimizerApplication.java
│   │   └── resources
│   │       ├── static
│   │       │   └── style.css
│   │       ├── templates
│   │       │   └── index.html
│   │       └── application.properties
│   └── test
│       └── java
│           └── com.example.PeepholeOptimizer
│               └── PeepholeOptimizerApplicationTests.java
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## Requirements

Before running the application, make sure the following are installed:

- Java
- Maven
- LLVM
- Clang
- IntelliJ IDEA

The current implementation uses LLVM installed through Homebrew on macOS.

The LLVM executable paths are configured in:

```text
CompilerService.java
```

If LLVM is installed at a different location, update the `clang` and `opt` paths accordingly.

## Running the Application

Clone the repository:

```bash
git clone https://github.com/snehadebnath26/PeepholeOptimizer.git
```

Move into the project:

```bash
cd PeepholeOptimizer
```

Run the application:

```bash
./mvnw spring-boot:run
```

The application runs on:

```text
http://localhost:8081
```

## LLVM Commands Used

The application internally performs the equivalent LLVM workflow:

```bash
clang -S -emit-llvm -O0 -Xclang -disable-O0-optnone test.c -o before.ll
```

followed by:

```bash
opt -S -passes=instcombine before.ll -o after.ll
```

The first command generates LLVM IR without applying normal optimization.

The second command applies LLVM's InstCombine pass.

## Important Project Scope

This project is a simplified educational implementation inspired by research on discovering missed peephole optimizations.

It does not reproduce the complete LPO framework.

The current implementation uses:

- LLVM Clang for compilation
- LLVM IR
- deterministic candidate extraction
- pattern-based optimization detection
- LLVM InstCombine for optimization
- before/after IR comparison

LLM-based optimization discovery and formal verification are outside the scope of the current implementation.

## Research Inspiration

This project is inspired by the research paper:

**LPO: Discovering Missed Peephole Optimizations with Large Language Models**

The research combines LLM-based exploration with verification techniques to discover missed compiler optimizations.

This project focuses on demonstrating the LLVM peephole optimization workflow and providing an interactive educational interface.

## Future Scope

Possible future improvements include:

- LLM-based optimization candidate generation
- Formal verification of candidate transformations
- Support for additional LLVM optimization passes
- Automatic performance benchmarking
- Optimization history and visualization
- More advanced LLVM IR analysis
- User-selectable optimization passes
- Automatic generation of optimization reports

## Author

**Sneha Debnath**

M.S. Ramaiah Institute of Technology

B.E. Computer Science and Engineering

## License

This project is intended for educational and academic purposes.