package com.example.unibiblion // ⬅️ LINHA QUE ESTAVA FALTANDO!

import com.google.firebase.Timestamp

data class Review(
    var id: String? = null,
    val livroId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhotoUrl: String? = null,
    val livroTitulo: String = "",
    val textoReview: String = "",
    val rating: Float = 0.0f,
    val timestamp: Timestamp? = null
)