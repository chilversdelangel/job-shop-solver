package mx.tecnm.cdmadero.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tecnm.cdmadero.ui.JobShopSolverUiState

@OptIn(ExperimentalTextApi::class)
@Composable
fun GanttChartSection(state: JobShopSolverUiState) {
    if (state.solution == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "Agrega trabajos y haz clic en SOLUCIONAR para ver el Diagrama de Gantt",
                color = Color.Gray,
                fontSize = 18.sp
            )
        }
        return
    }

    val solution = state.solution
    val machines = solution.schedule.map { it.machineId }.distinct().sorted()
    val textMeasurer = rememberTextMeasurer()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text("Makespan Óptimo: ${solution.makespan}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(16.dp))
            Text("Tiempo de Ejecución: ${state.executionTimeMs} ms", fontSize = 14.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Canvas(modifier = Modifier.fillMaxSize().background(Color(0xFFF9F9F9))) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val paddingLeft = 60f
            val paddingTop = 40f
            val chartWidth = canvasWidth - paddingLeft - 40f
            val chartHeight = canvasHeight - paddingTop - 40f

            if (machines.isEmpty()) return@Canvas

            val rowHeight = chartHeight / machines.size
            val timeScale = chartWidth / solution.makespan

            val jobColors = listOf(
                Color(0xFF4285F4), Color(0xFFEA4335), Color(0xFFFBBC05),
                Color(0xFF34A853), Color(0xFF8E44AD), Color(0xFF2C3E50)
            )

            machines.forEachIndexed { index, mId ->
                val y = paddingTop + index * rowHeight
                drawLine(
                    Color.LightGray,
                    Offset(paddingLeft, y + rowHeight),
                    Offset(paddingLeft + chartWidth, y + rowHeight),
                    strokeWidth = 1f
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = "M$mId",
                    topLeft = Offset(10f, y + rowHeight / 2 - 10f),
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                )
            }

            solution.schedule.forEach { task ->
                val machineIndex = machines.indexOf(task.machineId)
                val color = jobColors[task.jobId % jobColors.size]

                val x = paddingLeft + task.startTime * timeScale
                val y = paddingTop + machineIndex * rowHeight + 5f
                val width = task.duration * timeScale
                val height = rowHeight - 10f

                drawRect(color = color, topLeft = Offset(x, y), size = Size(width, height))

                if (width > 20f) {
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "J${task.jobId}",
                        topLeft = Offset(x + 5f, y + height / 2 - 8f),
                        style = TextStyle(fontSize = 10.sp, color = Color.White)
                    )
                }
            }

            drawLine(
                Color.Black,
                Offset(paddingLeft, paddingTop + chartHeight),
                Offset(paddingLeft + chartWidth, paddingTop + chartHeight),
                strokeWidth = 2f
            )
        }
    }
}
