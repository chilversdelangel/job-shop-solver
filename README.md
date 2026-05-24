# 🏗️ job-shop-solver - JSSP Optimization

A high-performance scheduling application built with **Compose Multiplatform** and **Kotlin**, designed to solve the complex **Job-Shop Scheduling Problem (JSSP)** using advanced optimization algorithms.

![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-Desktop-blue?style=for-the-badge)
![JVM](https://img.shields.io/badge/Platform-JVM%20Desktop-orange?style=for-the-badge)

## 🎯 Project Objective

The goal of this application is to find the optimal schedule for multiple jobs across various machines, minimizing the total time (makespan) while adhering to strict operational constraints.

### The Challenge
Implement robust solutions for the JSSP, where:
- **Task Precedence:** No task can start until the previous task of the same job is completed.
- **Machine Exclusivity:** Each machine can only process one job at a time.
- **Non-Preemption:** Once a task starts, it cannot be interrupted.

## 🧠 The Algorithms

The application implements two major strategies for solving the JSSP:

### 1. Dynamic Programming
Breaks the problem into overlapping sub-problems to find optimal substructures, using memoization to avoid redundant calculations.

### 2. Branch and Bound
A state-space search method that systematically explores potential schedules. It uses:
- **Branching:** Splitting the search space into smaller sub-regions.
- **Bounding:** Calculating lower bounds to prune branches that cannot lead to a better solution than the current best.

## 🚦 Getting Started

### Prerequisites
- JDK 17 or higher
- IntelliJ IDEA (Recommended)

### Running the App
```bash
./gradlew :desktopApp:run
```
