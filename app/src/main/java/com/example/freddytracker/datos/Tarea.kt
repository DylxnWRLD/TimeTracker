package com.example.freddytracker.datos

data class Tarea(
    val id: Int,
    var name: String,
    var startTime: String,
    var endTime: String?,
    var tiempoAcumulado: Long,
    var ultimoInicio: Long,
    var estado: EstadoTarea,

    var intervalos : MutableList<Intervalo> = mutableListOf()
)

enum class EstadoTarea {
    PENDIENTE,
    EN_PROGRESO,
    PAUSADO,
    FINALIZADO
}