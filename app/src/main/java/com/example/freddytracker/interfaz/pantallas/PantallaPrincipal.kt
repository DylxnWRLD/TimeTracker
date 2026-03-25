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

    LaunchedEffect(Unit) {
        while (true) {
            val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            val horaActual = sdf.format(java.util.Date())

            // Busca si alguna tarea está activa y coincide con la hora actual
            val tareaLista = viewModel.tasks.find {
                it.estado == EstadoTarea.ACTIVO && it.horaProgramada == horaActual
            }

            if (tareaLista != null) {
                // Pasamos a EN_PROGRESO para que no dispare esta navegación múltiple veces en el mismo minuto
                viewModel.updateTask(tareaLista.copy(estado = EstadoTarea.EN_PROGRESO))
                navController.navigate("interval/${tareaLista.id}")
            }

            kotlinx.coroutines.delay(5000) // Revisar cada 5 segundos
        }
    }

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
                                Text(text = "Hora de Inicio: ${task.horaProgramada}", fontSize = 17.sp)
//                                Text(text = "Tiempo Empleado: ${formatearTiempo(tiempoActual)}", fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Texto descriptivo del estado SIEMPRE VISIBLE
                                Text(
                                    text = if (task.estado == EstadoTarea.ACTIVO) "Activa" else "Inactiva",
                                    fontWeight = FontWeight.Bold,
                                    color = if (task.estado == EstadoTarea.ACTIVO) Color(0xFF4CAF50) else Color.Gray,
                                    modifier = Modifier.padding(end = 8.dp)
                                )

                                // Switch (Interruptor) SIEMPRE VISIBLE
                                Switch(
                                    checked = task.estado == EstadoTarea.ACTIVO,
                                    onCheckedChange = { activado ->
                                        val nuevoEstado = if (activado) EstadoTarea.ACTIVO else EstadoTarea.INACTIVO
                                        viewModel.updateTask(task.copy(estado = nuevoEstado))
                                    },
                                    colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF4CAF50))
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                // Botón Editar
                                FilledIconButton(
                                    onClick = { navController.navigate("editTask/${task.id}") },
                                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFffcf4d))
                                ) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Modificar", tint = Color.Black)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Botón Eliminar
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