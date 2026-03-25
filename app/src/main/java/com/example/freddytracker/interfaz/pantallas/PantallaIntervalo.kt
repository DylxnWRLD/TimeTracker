package com.example.freddytracker.interfaz.pantallas

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.freddytracker.datos.EstadoIntervalo
import com.example.freddytracker.datos.EstadoTarea
import com.example.freddytracker.datos.Tarea
import com.example.freddytracker.viewModel.TareaViewModel
import kotlinx.coroutines.delay

@SuppressLint("NewApi")
@Composable
fun PantallaIntervalo(
    navController: NavController,
    viewModel: TareaViewModel,
    taskId: Int
) {
    val task = viewModel.tasks.find { it.id == taskId } ?: return
    val intervalos = task.intervalos.toMutableStateList()


    var currentIndex by remember { mutableStateOf(0) }
    var tiempoRestante by remember { mutableStateOf(0L) }
    var tiempoTranscurrido by remember { mutableStateOf(0L) }
    var isActive by remember { mutableStateOf(true) }


    LaunchedEffect(currentIndex) {
        if (currentIndex < intervalos.size) {
            val intervaloActual = intervalos[currentIndex]
            intervalos[currentIndex] = intervaloActual.copy(estado = EstadoIntervalo.EN_PROGRESO)
            tiempoRestante = intervaloActual.duracion
            tiempoTranscurrido = 0L

            // Cronómetro
            while (tiempoRestante > 0 && isActive) {
                delay(1000)
                tiempoRestante -= 1000
                tiempoTranscurrido += 1000
            }

            if (tiempoRestante <= 0 && isActive) {
                intervalos[currentIndex] = intervaloActual.copy(
                    estado = EstadoIntervalo.FINALIZADO,
                    duracionReal = tiempoTranscurrido
                )

                viewModel.actualizarIntervalosDeTarea(taskId, intervalos.toMutableList())

                // Pasar al siguiente intervalo
                if (currentIndex + 1 < intervalos.size) {
                    currentIndex++
                } else {
                    finalizarTarea(task, viewModel, navController)
                }
            }
        } else {
            finalizarTarea(task, viewModel, navController)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (currentIndex < intervalos.size && intervalos[currentIndex].tipo.name == "ACTIVIDAD") {
                    Color(0xFF4CAF50)
                } else {
                    Color(0xFF2196F3)
                }
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (currentIndex < intervalos.size) {
            val intervaloActual = intervalos[currentIndex]

            Text(
                text = "Tarea: ${task.name}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Text(
                text = intervaloActual.nombre.ifEmpty {
                    if (intervaloActual.tipo.name == "ACTIVIDAD") "Actividad" else "Descanso"
                },
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            Text(
                text = formatearTiempoIntervalo(tiempoRestante),
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            Text(
                text = if (intervaloActual.tipo.name == "ACTIVIDAD") "Tiempo de actividad" else "Tiempo de descanso",
                fontSize = 18.sp,
                color = Color.White
            )

            LinearProgressIndicator(
                progress = if (intervaloActual.duracion > 0) {
                    (intervaloActual.duracion - tiempoRestante).toFloat() / intervaloActual.duracion.toFloat()
                } else 0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = {
                    tiempoRestante = 0L
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White,
                    containerColor = Color.White.copy(alpha = 0.2f)
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color.White)
                )
            ) {
                Text("Saltar este intervalo", color = Color.White)
            }
        } else {
            Text(
                text = "¡Tarea completada!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

private fun finalizarTarea(
    task: Tarea,
    viewModel: TareaViewModel,
    navController: NavController
) {
    val ahora = System.currentTimeMillis()
    val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
    val horaFin = sdf.format(java.util.Date(ahora))

    val tiempoTotal = if (task.estado == EstadoTarea.EN_PROGRESO) {
        task.tiempoAcumulado + (ahora - task.ultimoInicio)
    } else {
        task.tiempoAcumulado
    }

    val tareaFinalizada = task.copy(
        estado = EstadoTarea.FINALIZADO,
        endTime = horaFin,
        tiempoAcumulado = tiempoTotal
    )

    viewModel.updateTask(tareaFinalizada)

    navController.navigate("resultados/${task.id}") {
        popUpTo("home") { inclusive = false }
    }
}

private fun formatearTiempoIntervalo(millis: Long): String {
    val segundos = (millis / 1000) % 60
    val minutos = (millis / (1000 * 60)) % 60
    val horas = (millis / (1000 * 60 * 60))

    return if (horas > 0) {
        String.format("%02d:%02d:%02d", horas, minutos, segundos)
    } else {
        String.format("%02d:%02d", minutos, segundos)
    }
}