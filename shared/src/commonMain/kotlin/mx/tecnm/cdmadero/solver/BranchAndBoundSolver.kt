package mx.tecnm.cdmadero.solver

import mx.tecnm.cdmadero.model.Job
import kotlin.math.max

object BranchAndBoundSolver {
    private var bestMakespan = Int.MAX_VALUE
    private var bestSchedule = emptyList<ScheduledTask>()

    fun solve(jobs: List<Job>): JsspSolution {
        bestMakespan = Int.MAX_VALUE
        bestSchedule = emptyList()
        
        val initialState = ScheduleState(
            nextTaskIndexByJob = jobs.associate { it.id to 0 },
            machineFreeTime = emptyMap(),
            jobReadyTime = jobs.associate { it.id to 0 },
            currentMakespan = 0,
            scheduledTasks = emptyList()
        )

        search(initialState, jobs)

        return JsspSolution(
            makespan = if (bestMakespan == Int.MAX_VALUE) 0 else bestMakespan,
            schedule = bestSchedule
        )
    }

    private fun search(state: ScheduleState, jobs: List<Job>) {
        val allScheduled = jobs.all { job ->
            val nextIdx = state.nextTaskIndexByJob[job.id] ?: 0
            nextIdx >= job.tasks.size
        }

        if (allScheduled) {
            if (state.currentMakespan < bestMakespan) {
                bestMakespan = state.currentMakespan
                bestSchedule = state.scheduledTasks
            }
            return
        }

        if (calculateLowerBound(state, jobs) >= bestMakespan) {
            return
        }

        for (job in jobs) {
            val taskIndex = state.nextTaskIndexByJob[job.id] ?: 0
            
            if (taskIndex < job.tasks.size) {
                val task = job.tasks[taskIndex]
                
                val startTime = max(
                    state.jobReadyTime[job.id] ?: 0,
                    state.machineFreeTime[task.machineId] ?: 0
                )
                val endTime = startTime + task.duration

                val nextState = ScheduleState(
                    nextTaskIndexByJob = state.nextTaskIndexByJob + (job.id to taskIndex + 1),
                    machineFreeTime = state.machineFreeTime + (task.machineId to endTime),
                    jobReadyTime = state.jobReadyTime + (job.id to endTime),
                    currentMakespan = max(state.currentMakespan, endTime),
                    scheduledTasks = state.scheduledTasks + ScheduledTask(
                        jobId = job.id,
                        machineId = task.machineId,
                        startTime = startTime,
                        duration = task.duration
                    )
                )

                search(nextState, jobs)
            }
        }
    }

    private fun calculateLowerBound(state: ScheduleState, jobs: List<Job>): Int {
        var lowerBound = state.currentMakespan
        for (job in jobs) {
            val nextIdx = state.nextTaskIndexByJob[job.id] ?: 0
            if (nextIdx < job.tasks.size) {
                val remainingJobWork = job.tasks.drop(nextIdx).sumOf { it.duration }
                lowerBound = max(lowerBound, (state.jobReadyTime[job.id] ?: 0) + remainingJobWork)
            }
        }
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
        val currentMakespan: Int,
        val scheduledTasks: List<ScheduledTask>
    )
}
