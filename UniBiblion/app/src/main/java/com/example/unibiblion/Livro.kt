package com.example.unibiblion

import com.google.firebase.firestore.PropertyName

/**
 * Modelo de dados (POJO) que representa um Livro no Firestore.
 * O construtor vazio com valores padrão é essencial para o Firestore.
 */
data class Livro(
    @get:PropertyName("titulo") @set:PropertyName("titulo")
    var titulo: String = "",

    @get:PropertyName("autor") @set:PropertyName("autor")
    var autor: String = "",

    @get:PropertyName("ano") @set:PropertyName("ano")
    var ano: Long = 0,

    @get:PropertyName("capaURL") @set:PropertyName("capaURL")
    var capaUrl: String = "",

    @get:PropertyName("estado") @set:PropertyName("estado")
    var estado: String = "",

    @get:PropertyName("idioma") @set:PropertyName("idioma")
    var idioma: String = "",

    @get:PropertyName("qEstoque") @set:PropertyName("qEstoque")
    var qEstoque: Long = 0
)
