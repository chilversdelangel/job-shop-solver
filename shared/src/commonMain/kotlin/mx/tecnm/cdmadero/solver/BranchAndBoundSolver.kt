package mx.tecnm.cdmadero.solver

import mx.tecnm.cdmadero.model.Job
import kotlin.math.max

object BranchAndBoundSolver {
    private var bestMakespan = Int.MAX_VALUE

    fun solve(jobs: List<Job>): JsspSolution {
        bestMakespan = Int.MAX_VALUE

        val initialState = ScheduleState(
            nextTaskIndexByJob = jobs.associate { it.id to 0 },
            machineFreeTime = emptyMap(),
            jobReadyTime = jobs.associate { it.id to 0 },
            currentMakespan = 0
        )

        search(initialState, jobs)

        return JsspSolution(makespan = if (bestMakespan == Int.MAX_VALUE) 0 else bestMakespan)
    }

    private fun search(state: ScheduleState, jobs: List<Job>) {
        // Base case: check if everything is already scheduled
        val allScheduled = jobs.all { job ->
            val nextIdx = state.nextTaskIndexByJob[job.id] ?: 0
            nextIdx >= job.tasks.size
        }

        if (allScheduled) {
            bestMakespan = minOf(bestMakespan, state.currentMakespan)
            return
        }

        // Lower bound pruning: stop if this branch can't beat our current best
        if (calculateLowerBound(state, jobs) >= bestMakespan) {
            return
        }

        // Try to schedule the next available task for each job
        for (job in jobs) {
            val taskIndex = state.nextTaskIndexByJob[job.id] ?: 0

            if (taskIndex < job.tasks.size) {
                val task = job.tasks[taskIndex]

                // Earliest start depends on both machine and job availability
                val startTime = max(
                    state.jobReadyTime[job.id] ?: 0,
                    state.machineFreeTime[task.machineId] ?: 0
                )
                val endTime = startTime + task.duration

                val nextState = ScheduleState(
                    nextTaskIndexByJob = state.nextTaskIndexByJob + (job.id to taskIndex + 1),
                    machineFreeTime = state.machineFreeTime + (task.machineId to endTime),
                    jobReadyTime = state.jobReadyTime + (job.id to endTime),
                    currentMakespan = max(state.currentMakespan, endTime)
                )

                search(nextState, jobs)
            }
        }
    }

    private fun calculateLowerBound(state: ScheduleState, jobs: List<Job>): Int {
        var lowerBound = state.currentMakespan

        // Criteria 1: Check remaining work per job
        for (job in jobs) {
            val nextIdx = state.nextTaskIndexByJob[job.id] ?: 0
            if (nextIdx < job.tasks.size) {
                val remainingJobWork = job.tasks.drop(nextIdx).sumOf { it.duration }
                lowerBound = max(lowerBound, (state.jobReadyTime[job.id] ?: 0) + remainingJobWork)
            }
        }

        // Criteria 2: Check total work left on each machine
        val machineRemainingWork = mutableMapOf<Int, Int>()
        for (job in jobs) {
            val nextIdx = state.nextTaskIndexByJob[job.id] ?: 0
            for (i in nextIdx until job.tasks.size) {
                val mId = job.tasks[i].machineId
                machineRemainingWork[mId] = (machineRemainingWork[mId] ?: 0) + job.tasks[i].duration
            }
        }

        for ((machineId, work) in machineRemainingWork) {
            lowerBound = max(lowerBound, (state.machineFreeTime[machineId] ?: 0) + work)
        }

        return lowerBound
    }

    private data class ScheduleState(
        val nextTaskIndexByJob: Map<Int, Int>,
        val machineFreeTime: Map<Int, Int>,
        val jobReadyTime: Map<Int, Int>,
        val currentMakespan: Int
    )
}
