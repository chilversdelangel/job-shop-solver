package mx.tecnm.cdmadero.solver

import mx.tecnm.cdmadero.model.Job
import mx.tecnm.cdmadero.model.Task
import kotlin.test.Test
import kotlin.test.assertEquals

class BranchAndBoundSolverTest {

    @Test
    fun `test simple 2x2 JSSP instance`() {
        // Job 0: M0(3) -> M1(2)
        val job0 = Job(
            id = 0,
            tasks = listOf(
                Task(machineId = 0, duration = 3),
                Task(machineId = 1, duration = 2)
            )
        )

        // Job 1: M1(2) -> M0(1)
        val job1 = Job(
            id = 1,
            tasks = listOf(
                Task(machineId = 1, duration = 2),
                Task(machineId = 0, duration = 1)
            )
        )

        val jobs = listOf(job0, job1)

        val solution = BranchAndBoundSolver.solve(jobs)

        assertEquals(5, solution.makespan, "The optimal makespan should be 5")
    }
}
