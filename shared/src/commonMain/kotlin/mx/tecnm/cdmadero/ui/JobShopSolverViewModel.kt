package mx.tecnm.cdmadero.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mx.tecnm.cdmadero.model.Job
import mx.tecnm.cdmadero.model.Task
import mx.tecnm.cdmadero.solver.BranchAndBoundSolver
import mx.tecnm.cdmadero.solver.DynamicProgrammingSolver
import kotlin.system.measureTimeMillis

class JobShopSolverViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(JobShopSolverUiState())
    val uiState = _uiState.asStateFlow()

    fun selectAlgorithm(algorithm: Algorithm) {
        _uiState.update { it.copy(selectedAlgorithm = algorithm) }
    }

    fun addJob(tasks: List<Task>) {
        val newJob = Job(
            id = _uiState.value.jobs.size,
            tasks = tasks
        )
        _uiState.update { it.copy(jobs = it.jobs + newJob) }
    }

    fun clearJobs() {
        _uiState.update { it.copy(jobs = emptyList(), solution = null, executionTimeMs = null) }
    }

    fun solve() {
        val currentJobs = _uiState.value.jobs
        if (currentJobs.isEmpty()) return

        _uiState.update { it.copy(isSolving = true) }

        viewModelScope.launch {
            var solution: mx.tecnm.cdmadero.solver.JsspSolution? = null
            val time = measureTimeMillis {
                solution = when (_uiState.value.selectedAlgorithm) {
                    Algorithm.BRANCH_AND_BOUND -> BranchAndBoundSolver.solve(currentJobs)
                    Algorithm.DYNAMIC_PROGRAMMING -> DynamicProgrammingSolver.solve(currentJobs)
                }
            }

            _uiState.update {
                it.copy(
                    solution = solution,
                    executionTimeMs = time,
                    isSolving = false
                )
            }
        }
    }
}
