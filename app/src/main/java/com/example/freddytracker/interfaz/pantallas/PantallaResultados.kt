package com.example.freddytracker.interfaz.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.freddytracker.datos.Intervalo
import com.example.freddytracker.viewModel.TareaViewModel

@Composable
fun PantallaResultados(
    navController: NavController,
    viewModel: TareaViewModel,
    taskId: Int
) {
    val tarea = viewModel.tasks.find { it.id == taskId } ?: return

    val intervalosActividad = tarea.intervalos.filter { it.tipo.name == "ACTIVIDAD" }
    val intervalosDescanso = tarea.intervalos.filter { it.tipo.name == "DESCANSO" }

    val totalActividadEstablecido = intervalosActividad.sumOf { it.duracion }
    val totalActividadReal = intervalosActividad.sumOf { it.duracionReal }
    val ahorroActividad = totalActividadEstablecido - totalActividadReal

    val totalDescansoEstablecido = intervalosDescanso.sumOf { it.duracion }
    val totalDescansoReal = intervalosDescanso.sumOf { it.duracionReal }
    val ahorroDescanso = totalDescansoEstablecido - totalDescansoReal

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Resultados: ${tarea.name}",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp, top = 30.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                EncabezadoColumna(
                    titulo = "Actividad",
                    colorEncabezado = Color(0xFF4CAF50) // Verde
                )

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(intervalosActividad) { intervalo ->
                        ItemIntervaloSimple(intervalo)
                    }
                }

                ResumenColumna(
                    tiempoTotalEstablecido = totalActividadEstablecido,
                    tiempoTotalReal = totalActividadReal,
                    ahorroTotal = ahorroActividad,
                    colorFondo = Color(0xFF4CAF50).copy(alpha = 0.15f) // Verde translúcido
                )
            }

            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                EncabezadoColumna(
                    titulo = "Descanso",
                    colorEncabezado = Color(0xFF2196F3) // Azul
                )

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(intervalosDescanso) { intervalo ->
                        ItemIntervaloSimple(intervalo)
                    }
                }

                ResumenColumna(
                    tiempoTotalEstablecido = totalDescansoEstablecido,
                    tiempoTotalReal = totalDescansoReal,
                    ahorroTotal = ahorroDescanso,
                    colorFondo = Color(0xFF2196F3).copy(alpha = 0.15f) // Azul translúcido
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Finalizar", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun EncabezadoColumna(titulo: String, colorEncabezado: Color) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = colorEncabezado),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = titulo,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun ItemIntervaloSimple(intervalo: Intervalo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = intervalo.nombre.ifEmpty { "Sin nombre" }, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = "Est: ${formatearMsVerbal(intervalo.duracion)}", fontSize = 12.sp, color = Color.Gray)
                Text(text = "Real: ${formatearMsVerbal(intervalo.duracionReal)}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun ResumenColumna(
    tiempoTotalEstablecido: Long,
    tiempoTotalReal: Long,
    ahorroTotal: Long,
    colorFondo: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            TextoResumen(label = "Tiempo Total:", valor = formatearMsVerbal(tiempoTotalEstablecido))
            TextoResumen(
                label = "Ahorro total:",
                valor = if (ahorroTotal >= 0) formatearMsVerbal(ahorroTotal) else "- ${formatearMsVerbal(-ahorroTotal)}",
                valorColor = if (ahorroTotal > 0) Color(0xFF4CAF50) else if (ahorroTotal < 0) Color.Red else Color.Unspecified,
                esNegrita = ahorroTotal > 0
            )
            Divider(modifier = Modifier.padding(vertical = 6.dp), color = Color.White.copy(alpha = 0.4f))
            TextoResumen(label = "Total Real:", valor = formatearMsVerbal(tiempoTotalReal), esNegrita = true)
        }
    }
}

@Composable
fun TextoResumen(
    label: String,
    valor: String,
    valorColor: Color = Color.Unspecified,
    esNegrita: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp)
        Text(
            text = valor,
            fontSize = 13.sp,
            fontWeight = if (esNegrita) FontWeight.Bold else FontWeight.Normal,
            color = valorColor
        )
    }
}

fun formatearMsVerbal(millis: Long): String {
    if (millis <= 0) return "0s"

    val totalSegundos = millis / 1000
    val horas = totalSegundos / 3600
    val minutos = (totalSegundos % 3600) / 60
    val segundos = totalSegundos % 60

    return buildString {
        if (horas > 0) append("${horas}h ")
        if (minutos > 0) append("${minutos}m ")
        if (segundos > 0 || isEmpty()) append("${segundos}s")
    }.trim()
}