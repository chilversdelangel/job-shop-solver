package mx.tecnm.cdmadero.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tecnm.cdmadero.model.Task
import mx.tecnm.cdmadero.ui.Algorithm
import mx.tecnm.cdmadero.ui.JobShopSolverUiState

@Composable
fun ControlPanel(
    state: JobShopSolverUiState,
    onAlgorithmSelected: (Algorithm) -> Unit,
    onAddJob: (List<Task>) -> Unit,
    onClear: () -> Unit,
    onSolve: () -> Unit
) {
    var machineId by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    val currentTasks = remember { mutableStateListOf<Task>() }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Algoritmo", fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Algorithm.entries.forEach { algo ->
                FilterChip(
                    selected = state.selectedAlgorithm == algo,
                    onClick = { onAlgorithmSelected(algo) },
                    label = {
                        val label = when(algo) {
                            Algorithm.BRANCH_AND_BOUND -> "Branch & Bound"
                            Algorithm.DYNAMIC_PROGRAMMING -> "Prog. Dinámica"
                        }
                        Text(label, fontSize = 12.sp)
                    }
                )
            }
        }

        HorizontalDivider()

        Text("Crear Trabajo", fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = machineId,
                onValueChange = { machineId = it },
                label = { Text("ID Máquina", fontSize = 10.sp) },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("Duración", fontSize = 10.sp) },
                modifier = Modifier.weight(1f)
            )
        }

        Button(
            onClick = {
                val m = machineId.toIntOrNull()
                val d = duration.toIntOrNull()
                if (m != null && d != null) {
                    currentTasks.add(Task(m, d))
                    machineId = ""
                    duration = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Agregar Tarea")
        }

        if (currentTasks.isNotEmpty()) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Trabajo actual: " + currentTasks.joinToString(" → ") { "M${it.machineId}(${it.duration}h)" },
                    fontSize = 11.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Button(
                onClick = {
                    onAddJob(currentTasks.toList())
                    currentTasks.clear()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar y Agregar Trabajo")
            }
        }

        HorizontalDivider()

        Text("Resumen del Problema", fontWeight = FontWeight.Bold)
        Text("Total de Trabajos: ${state.jobs.size}", fontSize = 14.sp)

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onSolve,
                modifier = Modifier.weight(1.5f),
                enabled = state.jobs.isNotEmpty() && !state.isSolving
            ) {
                if (state.isSolving) CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                else Text("SOLUCIONAR")
            }
            OutlinedButton(onClick = onClear, modifier = Modifier.weight(1f)) {
                Text("LIMPIAR")
            }
        }
    }
}
