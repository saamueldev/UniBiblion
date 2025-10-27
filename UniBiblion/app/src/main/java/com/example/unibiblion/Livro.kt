package com.example.unibiblion

import androidx.annotation.DrawableRes

// A anotação @DrawableRes é opcional, mas ajuda a garantir que você sempre passe um ID de imagem válido.
data class Livro(
    val id: Int,
    val titulo: String,
    val autor: String,
    @DrawableRes val imagemId: Int // <-- CORRIGIDO de String para Int
)
