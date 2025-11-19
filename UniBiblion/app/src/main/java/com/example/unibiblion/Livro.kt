package com.example.unibiblion

import com.google.firebase.firestore.PropertyName
import java.io.Serializable

data class Livro(

    var id: String = "",

    @get:PropertyName("titulo") @set:PropertyName("titulo")
    var titulo: String = "",

    @get:PropertyName("autor") @set:PropertyName("autor")
    var autor: String = "",

    @get:PropertyName("ano") @set:PropertyName("ano")
    var ano: Long = 0,

    // Corrigido para corresponder à capitalização comum em Kotlin (camelCase)
    @get:PropertyName("capaURL") @set:PropertyName("capaURL")
    var capaUrl: String = "",

    @get:PropertyName("estado") @set:PropertyName("estado")
    var estado: String = "",

    @get:PropertyName("curso") @set:PropertyName("curso")
    var curso: String = "",

    @get:PropertyName("idioma") @set:PropertyName("idioma")
    var idioma: String = "",

    @get:PropertyName("qEstoque") @set:PropertyName("qEstoque")
    var qEstoque: Long = 0,

    // --- CAMPO ADICIONADO ---
    @get:PropertyName("resumo") @set:PropertyName("resumo")
    var resumo: String = ""
) : Serializable
