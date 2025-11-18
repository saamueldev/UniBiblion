package com.example.unibiblion

import com.google.firebase.Timestamp

data class Noticia(
    // NOVO CAMPO: id do documento no Firestore.
    val id: String = "",

    val titulo: String = "",
    val preview: String = "",
    val corpo: String = "",
    val urlImagem: String = "",
    val tipoLayout: Int = 0,
    val timestamp: Timestamp? = null
) {
    companion object {
        const val TIPO_IMAGEM_GRANDE = 1
        const val TIPO_IMAGEM_LATERAL = 2
    }
}