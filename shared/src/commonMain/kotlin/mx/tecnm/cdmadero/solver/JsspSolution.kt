package mx.tecnm.cdmadero.solver

/**
 * Represents a single task in the final schedule.
 */
data class ScheduledTask(
    val jobId: Int,
    val machineId: Int,
    val startTime: Int,
    val duration: Int
)

/**
 * Represents the result of a JSSP solution, including the total time
 * and the specific schedule for each task.
 */
data class JsspSolution(
    val makespan: Int,
    val schedule: List<ScheduledTask> = emptyList()
)
