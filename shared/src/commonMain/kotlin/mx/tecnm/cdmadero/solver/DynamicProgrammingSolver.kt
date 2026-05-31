package mx.tecnm.cdmadero.solver

import mx.tecnm.cdmadero.model.Job

object DynamicProgrammingSolver {
    private data class SolverState(
        val jobProgress: List<Int>,
        val machineAvailability: List<Int>
    )

    private val cache = mutableMapOf<SolverState, Int>()

    fun solve(jobs: List<Job>): JsspSolution {
        cache.clear()

        val machineCount = if (jobs.isEmpty()) 0 else jobs.flatMap { it.tasks }.maxOf { it.machineId } + 1

        val result = findOptimal(
            progress = jobs.map { 0 },
            machineTimes = List(machineCount) { 0 },
            jobReadyTimes = List(jobs.size) { 0 },
            jobs = jobs
        )
        return JsspSolution(result)
    }

    private fun findOptimal(
        progress: List<Int>,
        machineTimes: List<Int>,
        jobReadyTimes: List<Int>,
        jobs: List<Job>
    ): Int {
        // Base case: all jobs have finished their tasks
        if (allJobsFinished(progress, jobs)) {
            return machineTimes.maxOrNull() ?: 0
        }

        // Check if we already solved this specific situation
        val currentState = SolverState(progress, machineTimes)
        cache[currentState]?.let { return it }

        var bestTotalTime = Int.MAX_VALUE

        // Try to branch out by picking the next task of each job
        for ((jobIdx, taskIdx) in progress.withIndex()) {
            if (taskIdx < jobs[jobIdx].tasks.size) {
                val nextPathTime = tryPath(jobIdx, taskIdx, progress, machineTimes, jobReadyTimes, jobs)
                bestTotalTime = minOf(bestTotalTime, nextPathTime)
            }
        }

        // Save our findings and return
        cache[currentState] = bestTotalTime
        return bestTotalTime
    }

    private fun allJobsFinished(progress: List<Int>, jobs: List<Job>) =
        progress.withIndex().all { (idx, taskIdx) -> taskIdx >= jobs[idx].tasks.size }

    /**
     * Helper to calculate the result of taking one specific step (one task).
     */
    private fun tryPath(
        jobIdx: Int,
        taskIdx: Int,
        progress: List<Int>,
        machineTimes: List<Int>,
        jobReadyTimes: List<Int>,
        jobs: List<Job>
    ): Int {
        val task = jobs[jobIdx].tasks[taskIdx]

        // The task starts when both the machine is free and the job is ready
        val start = maxOf(jobReadyTimes[jobIdx], machineTimes[task.machineId])
        val end = start + task.duration

        return findOptimal(
            progress = progress.toMutableList().apply { this[jobIdx] = taskIdx + 1 },
            machineTimes = machineTimes.toMutableList().apply { this[task.machineId] = end },
            jobReadyTimes = jobReadyTimes.toMutableList().apply { this[jobIdx] = end },
            jobs = jobs
        )
    }
}
