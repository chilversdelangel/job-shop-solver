package mx.tecnm.cdmadero.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.tecnm.cdmadero.ui.components.ControlPanel
import mx.tecnm.cdmadero.ui.components.GanttChartSection
import mx.tecnm.cdmadero.ui.components.HeaderSection

@Composable
fun JobShopSolverScreen() {
    val viewModel: JobShopSolverViewModel = viewModel { JobShopSolverViewModel() }
    val state by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderSection()

            Row(modifier = Modifier.fillMaxSize()) {

                Box(modifier = Modifier.weight(0.7f).fillMaxHeight().padding(16.dp)) {
                    GanttChartSection(state)
                }

                VerticalDivider(thickness = 1.dp, color = Color.LightGray)

                Box(modifier = Modifier.weight(0.3f).fillMaxHeight()) {
                    ControlPanel(
                        state = state,
                        onAlgorithmSelected = viewModel::selectAlgorithm,
                        onAddJob = viewModel::addJob,
                        onClear = viewModel::clearJobs,
                        onSolve = viewModel::solve
                    )
                }
            }
        }
    }
}
