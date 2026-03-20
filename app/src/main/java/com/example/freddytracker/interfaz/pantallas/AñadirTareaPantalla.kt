package com.example.freddytracker.interfaz.pantallas

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.freddytracker.datos.EstadoTarea
import com.example.freddytracker.datos.Intervalo
import com.example.freddytracker.datos.Tarea
import com.example.freddytracker.datos.TipoIntervalo
import com.example.freddytracker.viewModel.TareaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun AñadirTareaPantalla(
    navController: NavController,
    viewModel: TareaViewModel
) {
    var name by remember { mutableStateOf("") }
    val intervalos = viewModel.intervalos

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Agregar Tarea",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre Tarea") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            modifier = Modifier.padding(end = 18.dp),
            text = "Añade los intervalos para tu tarea:",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Button(
            onClick = {
                viewModel.agregarIntervalo()
            },
            modifier = Modifier.fillMaxWidth()
        ){
            Text(text = "Añadir intervalo")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column{
            intervalos.forEachIndexed { index, intervalo ->
                IntervaloItem(
                    intervalo = intervalo,
                    onUpdate = { actualizado ->
                        viewModel.actualizarIntervalo(index, actualizado)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                val formattedTime = sdf.format(Date())
                val task = Tarea(
                    id = viewModel.tasks.size + 1,
                    name = name,
                    startTime = formattedTime,
                    endTime = null,
                    tiempoAcumulado = 0L,
                    ultimoInicio = 0L,
                    estado = EstadoTarea.PENDIENTE,
                    intervalos = intervalos.toMutableList()
                )
                viewModel.addTask(task)
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp, start = 8.dp, end = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFffcf4d),
                contentColor = Color.Black
            )
        ) {
            Text(
                text = "Agregar Tarea",
                fontSize = 19.sp
            )
        }
    }
}

@Composable
fun IntervaloItem(
    intervalo: Intervalo,
    onUpdate: (Intervalo) -> Unit
){
    var nombre by remember { mutableStateOf(intervalo.nombre) }
    var minutos by remember {
        mutableStateOf((intervalo.duracion / 60000).toString())
    }
    var segundos by remember {
        mutableStateOf(((intervalo.duracion % 60000) / 1000).toString())
    }
    var tipo by remember { mutableStateOf(intervalo.tipo) }
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                onUpdate(intervalo.copy(nombre = it))
            },
            label = { Text("Nombre") },
            modifier = Modifier.weight(1.8f)
        )

        Row(
            modifier = Modifier.weight(2f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            OutlinedTextField(
                value = minutos,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) {
                        minutos = it
                        val min = it.toLongOrNull() ?: 0L
                        val sec = segundos.toLongOrNull() ?: 0L
                        val total = (min * 60 + sec) * 1000
                        onUpdate(intervalo.copy(duracion = total))
                    }
                },
                label = { Text("Min") },
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = segundos,
                onValueChange = {
                    if (it.all { char -> char.isDigit() }) {
                        val secValue = it.toLongOrNull() ?: 0L
                        if (secValue <= 59) {
                            segundos = it
                            val min = minutos.toLongOrNull() ?: 0L
                            val total = (min * 60 + secValue) * 1000
                            onUpdate(intervalo.copy(duracion = total))
                        }
                    }
                },
                label = { Text("Seg") },
                modifier = Modifier.weight(1f)
            )
        }

        ComboTipoIntervalo(
            tipoActual = tipo,
            onTipoSeleccionado = {
                tipo = it
                onUpdate(intervalo.copy(tipo = it))
            },
            modifier = Modifier.weight(1.8f)
        )
        }
    }



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComboTipoIntervalo(
    tipoActual: TipoIntervalo,
    onTipoSeleccionado: (TipoIntervalo) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = if (tipoActual == TipoIntervalo.ACTIVIDAD) "Actividad" else "Descanso",
            onValueChange = {},
            readOnly = true,
            label = { Text("Tipo") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Actividad") },
                onClick = {
                    onTipoSeleccionado(TipoIntervalo.ACTIVIDAD)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Descanso") },
                onClick = {
                    onTipoSeleccionado(TipoIntervalo.DESCANSO)
                    expanded = false
                }
            )
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun PreviewScreen() {
    val navController = rememberNavController()
    val viewModel = TareaViewModel()
    AñadirTareaPantalla(
        navController = navController,
        viewModel = viewModel
    )
}