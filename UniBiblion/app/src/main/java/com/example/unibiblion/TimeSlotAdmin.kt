package com.example.unibiblion

// Este modelo serve para a lista de horários na tela de edição do Admin
data class TimeSlotAdmin(
    val id: String, // ex: "9" (referente à hora 09)
    val startHour: String, // ex: "09:00"
    val endHour: String, // ex: "10:00"
    var isIndisponivel: Boolean = false, // Status de seleção do admin
    var firestoreDocId: String? = null, // ID do documento do Firebase, se já existir
    var isReservadoPeloUsuario: Boolean = false
)