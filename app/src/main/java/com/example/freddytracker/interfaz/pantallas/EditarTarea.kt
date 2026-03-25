package com.example.freddytracker.interfaz.pantallas

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.freddytracker.datos.EstadoTarea
import com.example.freddytracker.viewModel.TareaViewModel

@SuppressLint("NewApi")
@Composable
fun EditarTarea(
    navController: NavController,
    viewModel: TareaViewModel,
    taskId: Int
) {
    val task = viewModel.tasks.find { it.id == taskId } ?: return

    var name by remember { mutableStateOf(task.name) }
    var horaProgramada by remember { mutableStateOf(task.horaProgramada) }
    val intervalos = viewModel.intervalos

    // Cargamos los intervalos de la tarea en la lista del ViewModel
    LaunchedEffect(taskId) {
        viewModel.cargarIntervalosParaEdicion(task)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Modificar Tarea",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 20.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre Tarea") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = horaProgramada,
            onValueChange = { horaProgramada = it },
            label = { Text("Hora Programada (HH:mm)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sección para editar intervalos
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Modificar intervalos:",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Button(
            onClick = { viewModel.agregarIntervalo() },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFffcf4d), contentColor = Color.Black)
        ) {
            Text(text = "Añadir nuevo intervalo", fontSize = 19.sp)
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            intervalos.forEachIndexed { index, intervalo ->
                // Reutilizamos el componente de AñadirTareaPantalla
                IntervaloItem(
                    intervalo = intervalo,
                    onUpdate = { actualizado -> viewModel.actualizarIntervalo(index, actualizado) }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val updatedTask = task.copy(
                    name = name,
                    horaProgramada = horaProgramada,
                    intervalos = intervalos.toMutableList() // Guardamos los intervalos modificados
                )
                viewModel.updateTask(updatedTask)
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFffcf4d), contentColor = Color.Black)
        ) {
            Text("Guardar cambios", fontSize = 19.sp)
        }
    }
}