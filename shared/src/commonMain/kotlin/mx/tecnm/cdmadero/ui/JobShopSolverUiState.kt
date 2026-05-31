package mx.tecnm.cdmadero.ui

import mx.tecnm.cdmadero.model.Job
import mx.tecnm.cdmadero.solver.JsspSolution

enum class Algorithm {
    BRANCH_AND_BOUND,
    DYNAMIC_PROGRAMMING
}

/**
 * State representation for the Main Screen.
 */
data class JobShopSolverUiState(
    val jobs: List<Job> = emptyList(),
    val selectedAlgorithm: Algorithm = Algorithm.BRANCH_AND_BOUND,
    val solution: JsspSolution? = null,
    val executionTimeMs: Long? = null,
    val isSolving: Boolean = false
)
