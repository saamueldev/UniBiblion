package com.example.unibiblion

// Simulação de dados do usuário para a review
data class UsuarioReview(
    val nome: String,
    // Em uma app real, este seria um URL ou URI de imagem,
    // mas vamos usar um ID de recurso para simulação
    val fotoResourceId: Int
)