package com.example.freddytracker.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.freddytracker.datos.EstadoTarea
import com.example.freddytracker.datos.Intervalo
import com.example.freddytracker.datos.Tarea
import com.example.freddytracker.datos.TipoIntervalo

class TareaViewModel : ViewModel() {

    var intervalos = mutableStateListOf<Intervalo>()
        private set

    var tasks = mutableStateListOf<Tarea>()
        private set

    fun addTask(task: Tarea) {
        tasks.add(task)
    }

    fun deleteTask(task: Tarea) {
        tasks.remove(task)
    }

    fun updateTask(updatedTask: Tarea) {
        val index = tasks.indexOfFirst { it.id == updatedTask.id }
        if (index != -1) {
            tasks[index] = updatedTask
        }
    }

    fun iniciarTarea(tarea: Tarea) {
        val ahora = System.currentTimeMillis()

        val tareaActualizada = tarea.copy(
            ultimoInicio = ahora,
            estado = EstadoTarea.EN_PROGRESO
        )

        updateTask(tareaActualizada)
    }

    fun pausarTarea(tarea: Tarea) {
        if (tarea.estado != EstadoTarea.EN_PROGRESO) return

        val ahora = System.currentTimeMillis()
        val diferencia = ahora - tarea.ultimoInicio

        val tareaActualizada = tarea.copy(
            tiempoAcumulado = tarea.tiempoAcumulado + diferencia,
            estado = EstadoTarea.PAUSADO
        )

        updateTask(tareaActualizada)
    }

    fun reanudarTarea(tarea: Tarea) {
        if (tarea.estado != EstadoTarea.PAUSADO) return

        val ahora = System.currentTimeMillis()

        val tareaActualizada = tarea.copy(
            ultimoInicio = ahora,
            estado = EstadoTarea.EN_PROGRESO
        )

        updateTask(tareaActualizada)
    }
    fun obtenerTiempoActual(tarea: Tarea): Long {
        return if (tarea.estado == EstadoTarea.EN_PROGRESO) {
            val ahora = System.currentTimeMillis()
            tarea.tiempoAcumulado + (ahora - tarea.ultimoInicio)
        } else {
            tarea.tiempoAcumulado
        }
    }

    fun agregarIntervalo() {
        intervalos.add(
            Intervalo(
                id = intervalos.size + 1,
                nombre = "",
                duracion = 5 * 60 * 1000L,
                tipo = TipoIntervalo.ACTIVIDAD
            )
        )
    }

    fun actualizarIntervalo(index: Int, intervalo: Intervalo) {
        intervalos[index] = intervalo
    }


}