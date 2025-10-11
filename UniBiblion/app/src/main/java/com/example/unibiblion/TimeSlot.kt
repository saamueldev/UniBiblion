package com.example.unibiblion

import java.util.Calendar

data class TimeSlot(
    val startTime: Calendar,  // Início do slot (ex: 13:00)
    val endTime: Calendar,    // Fim do slot (ex: 14:00)
    val isAvailable: Boolean, // Se está Livre ou Ocupado
    var isSelected: Boolean = false // Usado para seleção de range
)