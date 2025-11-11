package com.example.unibiblion

data class Review(
    val id: String,
    val livroTitulo: String,
    val textoReview: String,
    val usuario: UsuarioReview,
    val rating: Float // Ex: 4.5 estrelas
)