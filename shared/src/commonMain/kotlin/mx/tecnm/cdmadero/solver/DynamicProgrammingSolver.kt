package mx.tecnm.cdmadero.solver

import mx.tecnm.cdmadero.model.Job

object DynamicProgrammingSolver {
    
    private data class SolverState(
        val jobProgress: List<Int>, 
        val machineAvailability: List<Int>
    )

    // Cache stores the optimal remaining solution from a given state
    private val cache = mutableMapOf<SolverState, JsspSolution>()

    fun solve(jobs: List<Job>): JsspSolution {
        cache.clear()
        
        val machineCount = if (jobs.isEmpty()) 0 else jobs.flatMap { it.tasks }.maxOf { it.machineId } + 1
        
        return findOptimal(
            progress = jobs.map { 0 }, 
            machineTimes = List(machineCount) { 0 }, 
            jobReadyTimes = List(jobs.size) { 0 }, 
            jobs = jobs
        )
    }

    private fun findOptimal(
        progress: List<Int>,
        machineTimes: List<Int>,
        jobReadyTimes: List<Int>,
        jobs: List<Job>
    ): JsspSolution {
        if (allJobsFinished(progress, jobs)) {
            return JsspSolution(makespan = machineTimes.maxOrNull() ?: 0, schedule = emptyList())
        }

        val currentState = SolverState(progress, machineTimes)
        cache[currentState]?.let { return it }

        var bestSolution = JsspSolution(makespan = Int.MAX_VALUE)

        for ((jobIdx, taskIdx) in progress.withIndex()) {
            if (taskIdx < jobs[jobIdx].tasks.size) {
                val currentTask = jobs[jobIdx].tasks[taskIdx]
                val start = maxOf(jobReadyTimes[jobIdx], machineTimes[currentTask.machineId])
                val end = start + currentTask.duration
                
                val scheduledTask = ScheduledTask(
                    jobId = jobs[jobIdx].id,
                    machineId = currentTask.machineId,
                    startTime = start,
                    duration = currentTask.duration
                )

                val result = findOptimal(
                    progress = progress.toMutableList().apply { this[jobIdx] = taskIdx + 1 },
                    machineTimes = machineTimes.toMutableList().apply { this[currentTask.machineId] = end },
                    jobReadyTimes = jobReadyTimes.toMutableList().apply { this[jobIdx] = end },
                    jobs = jobs
                )
                
                if (result.makespan < bestSolution.makespan) {
                    bestSolution = result.copy(
                        schedule = listOf(scheduledTask) + result.schedule
                    )
                }
            }
        }

        cache[currentState] = bestSolution
        return bestSolution
    }

    private fun allJobsFinished(progress: List<Int>, jobs: List<Job>) =
        progress.withIndex().all { (idx, taskIdx) -> taskIdx >= jobs[idx].tasks.size }
}
