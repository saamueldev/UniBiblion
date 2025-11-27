package com.example.unibiblion

import com.google.firebase.firestore.DocumentId

data class Restricao(
    @DocumentId // ID do documento no Firestore
    val id: String? = null,
    val cabineId: String? = null,
    val dataRestricao: String? = null, // Formato "YYYY-MM-DD"
    val horaInicio: String? = null, // Formato "HH:MM"
    val horaFim: String? = null, // Formato "HH:MM"
    val motivo: String? = "Restrição de Admin"
) {
    // Construtor sem argumentos necessário para o Firestore
    @Suppress("unused")
    constructor() : this(null, null, null, null, null, null)
}