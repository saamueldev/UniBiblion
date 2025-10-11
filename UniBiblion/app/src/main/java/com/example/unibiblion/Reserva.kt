package com.example.unibiblion

import java.util.Calendar

data class Reserva(
    val id: String, // ID único da reserva (simulado)
    val numeroCabine: String,
    val startTime: Calendar, // Data e hora de início
    val endTime: Calendar,   // Data e hora de fim
    val status: StatusReserva
)

enum class StatusReserva {
    ATIVA,      // Reservas no futuro ou em andamento
    CONCLUIDA,  // Reservas que já passaram
    CANCELADA   // Reservas que foram canceladas (opcional, mas bom para o modelo)
}