package com.example.freddytracker.interfaz.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.freddytracker.datos.EstadoTarea
import com.example.freddytracker.datos.Tarea
import com.example.freddytracker.viewModel.TareaViewModel
import kotlinx.coroutines.delay

@Composable
fun PantallaPrincipal(
    navController: NavController,
    viewModel: TareaViewModel
) {
    var tareaAEliminar by remember { mutableStateOf<Tarea?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Registro de \ntiempos",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 55.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(top = 25.dp, start = 8.dp, end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            if(viewModel.tasks.isEmpty()){
                item {
                    Column(
                        modifier = Modifier.fillParentMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Presiona el botón de abajo \npara agregar una tarea",
                            fontSize = 24.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 45.dp)
                        )
                    }
                }
            } else {
                items(viewModel.tasks) { task ->
                    var tiempoActual by remember {
                        mutableStateOf(viewModel.obtenerTiempoActual(task))
                    }

                    LaunchedEffect(task.estado) {
                        while (true) {
                            if (task.estado == EstadoTarea.EN_PROGRESO) {
                                tiempoActual = viewModel.obtenerTiempoActual(task)
                            }
                            delay(1000)
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFedebeb))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = task.name,
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic,
                                fontSize = 22.sp
                            )

                            Column (
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(text = "Hora de Inicio: ${task.startTime}", fontSize = 17.sp)
                                Text(text = "Tiempo Empleado: ${formatearTiempo(tiempoActual)}", fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Botones de control de estado con ICONOS
                                // Verificamos si la tarea ya está cerrada definitivamente
                                val estaFinalizada = task.estado == EstadoTarea.FINALIZADO || !task.endTime.isNullOrEmpty()

                                if (!estaFinalizada) {
                                    when (task.estado) {
                                        EstadoTarea.PENDIENTE, EstadoTarea.PAUSADO -> {
                                            FilledIconButton(
                                                onClick = {
                                                    if (task.estado == EstadoTarea.PENDIENTE) {
                                                        // Navegar a la pantalla de intervalos
                                                        navController.navigate("interval/${task.id}")
                                                    } else {
                                                        viewModel.reanudarTarea(task)
                                                        navController.navigate("interval/${task.id}")
                                                    }
                                                },
                                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF75de5d))
                                            ) {
                                                Icon(Icons.Filled.PlayArrow, contentDescription = "Iniciar", tint = Color.Black)
                                            }
                                        }
                                        EstadoTarea.EN_PROGRESO -> {
                                            FilledIconButton(
                                                onClick = { viewModel.pausarTarea(task) },
                                                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFf2a435))
                                            ) {
                                                Icon(Icons.Filled.Pause, contentDescription = "Pausar", tint = Color.Black)
                                            }
                                        }
                                        else -> {}
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    FilledIconButton(
                                        onClick = {
                                            val ahora = System.currentTimeMillis()
                                            val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                                            val horaFin = sdf.format(java.util.Date(ahora))

                                            val tiempoFinal = if (task.estado == EstadoTarea.EN_PROGRESO) {
                                                task.tiempoAcumulado + (ahora - task.ultimoInicio)
                                            } else {
                                                task.tiempoAcumulado
                                            }

                                            viewModel.updateTask(task.copy(
                                                estado = EstadoTarea.FINALIZADO,
                                                endTime = horaFin,
                                                tiempoAcumulado = tiempoFinal
                                            ))
                                        },
                                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.Gray)
                                    ) {
                                        Icon(Icons.Filled.Stop, contentDescription = "Finalizar", tint = Color.White)
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))
                                }

                                FilledIconButton(
                                    onClick = { navController.navigate("editTask/${task.id}") },
                                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFffcf4d))
                                ) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Modificar", tint = Color.Black)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                FilledIconButton(
                                    onClick = { tareaAEliminar = task },
                                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.Red)
                                ) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = { navController.navigate("addTask") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 25.dp, start = 8.dp, end = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFffcf4d), contentColor = Color.Black)
        ) {
            Text(text = "Agregar Tarea", fontSize = 19.sp)
        }

        if (tareaAEliminar != null) {
            AlertDialog(
                onDismissRequest = { tareaAEliminar = null },
                title = { Text("Alerta") },
                text = { Text("¿Esta seguro que quiere borrar esta tarea?") },

                confirmButton = {
                    Button(
                        onClick = {
                        viewModel.deleteTask(tareaAEliminar!!)
                        tareaAEliminar = null },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White)
                    ) {
                        Text("Sí")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { tareaAEliminar = null },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFffcf4d), contentColor = Color.Black)
                    ) {
                        Text("No")
                    }
                }
            )
        }
    }
}

fun formatearTiempo(millis: Long): String {
    val segundos = (millis / 1000) % 60
    val minutos = (millis / (1000 * 60)) % 60
    val horas = (millis / (1000 * 60 * 60))

    return String.format("%02d:%02d:%02d", horas, minutos, segundos)
}