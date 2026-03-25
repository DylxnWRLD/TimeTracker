package com.example.freddytracker.datos

data class Intervalo(
    val id: Int,
    var nombre: String,
    var duracion: Long,
    var tipo: TipoIntervalo,
    var estado: EstadoIntervalo,
    var duracionReal: Long = 0L
)

enum class TipoIntervalo {
    ACTIVIDAD,
    DESCANSO
}

enum class EstadoIntervalo {
    PENDIENTE,
    EN_PROGRESO,
    FINALIZADO
}