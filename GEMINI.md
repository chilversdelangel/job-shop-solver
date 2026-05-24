# GEMINI.md - Project Instruction Context

## Project Overview
**job-shop-solver** is a high-performance desktop application built with **Kotlin** and **Compose Multiplatform**. Its primary purpose is to solve the **Job-Shop Scheduling Problem (JSSP)**, a combinatorial optimization challenge.

- **Primary Target:** Desktop (JVM).
- **Architecture:** Clean separation of concerns between `shared` logic and platform-specific `desktopApp`. Currently exploring the best architectural fit (State-Driven vs. DDD).
- **Core Problem:** Optimal scheduling of multiple jobs across various machines, minimizing the total makespan under constraints (Task Precedence, Machine Exclusivity, Non-Preemption).

## Tech Stack
- **Language:** Kotlin 2.x
- **UI Framework:** Compose Multiplatform
- **Build System:** Gradle (Kotlin DSL) with Version Catalogs (`libs.versions.toml`)
- **JDK Requirement:** 17+

## Project Structure
- `/shared`: Contains the core logic and multiplatform Compose UI.
    - `src/commonMain`: Shared UI (`App.kt`) and eventual domain/algorithm logic.
    - `src/jvmMain`: JVM-specific implementations (if needed).
- `/desktopApp`: The thin entry point for the desktop application.
    - `src/main/kotlin/.../main.kt`: Configures the window and launches the shared `App()`.
- `/gradle`: Version management and Gradle wrapper.

## Building and Running
| Task | Command |
| :--- | :--- |
| **Run Desktop App** | `./gradlew :desktopApp:run` |
| **Build Distributions** | `./gradlew :desktopApp:package` (Output in `build/compose/binaries`) |
| **Run Tests** | `./gradlew :shared:jvmTest` |
| **Clean Build** | `./gradlew clean` |

## Development Conventions

### Git Workflow: "Always Branch Alone"
- **Initial Setup:** Foundation (configs, docs, cleanup) was built directly on `main`.
- **Feature Development:** **Always** create a new branch for actual features.
- **Workflow:** Create branch → Atomic commits → Push → PR → Merge to main.
- **Main Protection:** `main` should stay frozen and stable once feature development begins.

### Atomic Commits
- Follow [Conventional Commits](https://www.conventionalcommits.org/).
- **Rule:** Each commit must represent one single, logical unit of work.

### Coding Style
- **Wildcard Imports Allowed:** To keep files clean in this small-scale project, wildcard imports (`import package.*`) are acceptable, especially when more than 5 imports are needed from the same package.
- **Compose Best Practices:** Use `remember` and `mutableStateOf` for local UI state.
- **Desktop Focus:** While using multiplatform modules, prioritize JVM-specific performance and UI affordances.

## Algorithms to Implement
- **Dynamic Programming:** For optimal substructure and state memoization.
- **Branch and Bound:** For systematic search space pruning.

## Mandatory Constraints (JSSP)
1. **Task Precedence:** Tasks in a job must be sequential.
2. **Machine Exclusivity:** One job per machine at a time.
3. **Non-Preemption:** No interruptions once a task begins.
