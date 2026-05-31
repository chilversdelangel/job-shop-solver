package mx.tecnm.cdmadero.solver

import mx.tecnm.cdmadero.model.Job
import mx.tecnm.cdmadero.model.Task
import kotlin.test.Test
import kotlin.test.assertEquals

class DynamicProgrammingSolverTest {

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

        val solution = DynamicProgrammingSolver.solve(jobs)

        assertEquals(5, solution.makespan, "The optimal makespan should be 5")
    }

    @Test
    fun `test Google OR-Tools JSSP instance`() {
        // Data from: https://developers.google.com/optimization/scheduling/job_shop

        // Job 0: (M0, 3), (M1, 2), (M2, 2)
        val job0 = Job(
            id = 0,
            tasks = listOf(
                Task(machineId = 0, duration = 3),
                Task(machineId = 1, duration = 2),
                Task(machineId = 2, duration = 2)
            )
        )

        // Job 1: (M0, 2), (M2, 1), (M1, 4)
        val job1 = Job(
            id = 1,
            tasks = listOf(
                Task(machineId = 0, duration = 2),
                Task(machineId = 2, duration = 1),
                Task(machineId = 1, duration = 4)
            )
        )

        // Job 2: (M1, 4), (M2, 3)
        val job2 = Job(
            id = 2,
            tasks = listOf(
                Task(machineId = 1, duration = 4),
                Task(machineId = 2, duration = 3)
            )
        )

        val jobs = listOf(job0, job1, job2)

        val solution = DynamicProgrammingSolver.solve(jobs)

        assertEquals(11, solution.makespan, "The optimal makespan should be 11")
    }
}
