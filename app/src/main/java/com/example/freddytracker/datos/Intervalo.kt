package com.example.freddytracker.datos

data class Intervalo(
    val id: Int,
    var nombre: String,
    var duracion: Long,
    var tipo: TipoIntervalo
)

enum class TipoIntervalo {
    ACTIVIDAD,
    DESCANSO
}