package com.example.unibiblion

data class TimeSlotAdmin(
    val id: String,
    val horarioInicio: String, // Ex: "09:00"
    val horarioFim: String,    // Ex: "10:00"
    var isIndisponivel: Boolean = false // O administrador marca como indisponível
)