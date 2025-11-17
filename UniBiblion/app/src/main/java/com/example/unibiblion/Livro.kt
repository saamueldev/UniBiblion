package com.example.unibiblion

import com.google.firebase.firestore.PropertyName

// A linha com erro foi removida e a declaração da classe está correta agora.
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

    // NOVO CAMPO ADICIONADO PARA O FILTRO
    @get:PropertyName("curso") @set:PropertyName("curso")
    var curso: String = "",

    @get:PropertyName("idioma") @set:PropertyName("idioma")
    var idioma: String = "",

    @get:PropertyName("qEstoque") @set:PropertyName("qEstoque")
    var qEstoque: Long = 0
)
