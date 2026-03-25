package com.example.freddytracker.viewModel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.example.freddytracker.datos.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class TareaViewModel(application: Application) : AndroidViewModel(application) {

    var intervalos = mutableStateListOf<Intervalo>()
        private set

    var tasks = mutableStateListOf<Tarea>()
        private set


    private val gson = Gson()
    private val archivoJson = File(application.filesDir, "tareas_guardadas.json")

    init {
        cargarTareas()
    }

    private fun guardarTareas() {
        try {
            val json = gson.toJson(tasks)
            archivoJson.writeText(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun cargarTareas() {
        if (archivoJson.exists()) {
            try {
                val json = archivoJson.readText()
                val tipoLista = object : TypeToken<List<Tarea>>() {}.type
                val tareasCargadas: List<Tarea> = gson.fromJson(json, tipoLista)
                tasks.clear()
                tasks.addAll(tareasCargadas)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addTask(task: Tarea) {
        tasks.add(task)
        guardarTareas()
    }

    fun deleteTask(task: Tarea) {
        tasks.remove(task)
        guardarTareas()
    }

    fun updateTask(updatedTask: Tarea) {
        val index = tasks.indexOfFirst { it.id == updatedTask.id }
        if (index != -1) {
            tasks[index] = updatedTask
            guardarTareas()
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
                tipo = TipoIntervalo.ACTIVIDAD,
                estado = EstadoIntervalo.PENDIENTE
            )
        )
    }

    fun actualizarIntervalo(index: Int, intervalo: Intervalo) {
        intervalos[index] = intervalo
    }

    fun actualizarIntervalosDeTarea(taskId: Int, nuevosIntervalos: MutableList<Intervalo>) {
        val task = tasks.find { it.id == taskId }
        if (task != null) {
            val tareaActualizada = task.copy(intervalos = nuevosIntervalos)
            updateTask(tareaActualizada)
        }
    }

    fun limpiarIntervalos() {
        intervalos.clear()
    }

    fun cargarIntervalosParaEdicion(tarea: Tarea) {
        intervalos.clear()
        // Hacemos una copia para no modificar la tarea original hasta guardar
        intervalos.addAll(tarea.intervalos.map { it.copy() })
    }

}