package com.example.unibiblion

data class Livro(
    val id: Int,
    val titulo: String,
    val autor: String,
    // Usamos Int para referenciar o recurso de imagem Drawable (ex: R.drawable.sommervile)
    val capaResourceId: Int
)