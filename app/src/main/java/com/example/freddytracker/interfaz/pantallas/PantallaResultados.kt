package com.example.freddytracker.interfaz.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.freddytracker.datos.Intervalo
import com.example.freddytracker.viewModel.TareaViewModel
import java.util.concurrent.TimeUnit

@Composable
fun PantallaResultados(
    navController: NavController,
    viewModel: TareaViewModel,
    taskId: Int
) {
    val tarea = viewModel.tasks.find { it.id == taskId } ?: return

    // 1. Filtrar los intervalos por tipo
    val intervalosActividad = tarea.intervalos.filter { it.tipo.name == "ACTIVIDAD" }
    val intervalosDescanso = tarea.intervalos.filter { it.tipo.name == "DESCANSO" }

    // 2. Calcular los totales y el ahorro para Actividad
    val totalActividadEstablecido = intervalosActividad.sumOf { it.duracion }
    val totalActividadReal = intervalosActividad.sumOf { it.duracionReal }
    val ahorroActividad = totalActividadEstablecido - totalActividadReal

    // 3. Calcular los totales y el ahorro para Descanso
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
            modifier = Modifier.padding(bottom = 24.dp, top = 26.dp)
        )

        // Contenedor principal para las dos columnas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Esto permite que el botón de finalizar se quede abajo
        ) {
            // COLUMNA IZQUIERDA: ACTIVIDAD
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                EncabezadoColumna(
                    titulo = "Actividad",
                    colorEncabezado = Color(0xFF4CAF50) // Verde
                )

                // Lista de actividades
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(intervalosActividad) { intervalo ->
                        ItemIntervaloSimple(intervalo)
                    }
                }

                // Resumen de Actividad (El cuadro al final)
                ResumenColumna(
                    tiempoTotalEstablecido = totalActividadEstablecido,
                    tiempoTotalReal = totalActividadReal,
                    ahorroTotal = ahorroActividad,
                    colorFondo = Color(0xFF4CAF50).copy(alpha = 0.15f) // Verde translúcido
                )
            }

            // COLUMNA DERECHA: DESCANSO
            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                EncabezadoColumna(
                    titulo = "Descanso",
                    colorEncabezado = Color(0xFF2196F3) // Azul
                )

                // Lista de descansos
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(intervalosDescanso) { intervalo ->
                        ItemIntervaloSimple(intervalo)
                    }
                }

                // Resumen de Descanso (El cuadro al final)
                ResumenColumna(
                    tiempoTotalEstablecido = totalDescansoEstablecido,
                    tiempoTotalReal = totalDescansoReal,
                    ahorroTotal = ahorroDescanso,
                    colorFondo = Color(0xFF2196F3).copy(alpha = 0.15f) // Azul translúcido
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Finalizar (ocupa todo el ancho al final)
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
            // Aquí puedes agregar un icono si tienes los recursos:
            // Icon(imageVector = ..., contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            // Spacer(modifier = Modifier.width(8.dp))
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

/**
 * Función auxiliar para formatear ms a formato verbal: Xh Ym Zs
 */
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