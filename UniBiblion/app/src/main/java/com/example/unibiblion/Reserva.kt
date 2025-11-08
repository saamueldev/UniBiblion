package com.example.unibiblion

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId // Importe esta anotação

data class Reserva(
    @DocumentId // Mapeia o ID do documento do Firestore para este campo
    val id: String? = null, // ID gerado pelo Firestore
    val cabineNumero: String? = null,
    val usuarioId: String? = null, // ID do Firebase Auth do usuário
    val dataReserva: String? = null, // Ex: "2025-11-09" (Para consulta por dia)
    val horaInicio: String? = null,  // Ex: "14:00"
    val horaFim: String? = null,     // Ex: "16:00"
    val status: String? = null,      // Armazena a String do StatusReserva (Ex: "ATIVA")
    val timestampCriacao: Timestamp? = null // Momento da criação
)

enum class StatusReserva {
    ATIVA,
    CONCLUIDA,
    CANCELADA
}