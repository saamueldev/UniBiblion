package com.example.unibiblion

import com.google.firebase.Timestamp // NOVO IMPORT NECESSÁRIO!

// Usamos um data class para facilitar a manipulação de dados
data class Noticia(
    val titulo: String = "", // Adicionado valor padrão para mapeamento do Firestore
    val preview: String = "", // Adicionado valor padrão
    val corpo: String = "", // Adicionado valor padrão
    val urlImagem: String = "", // Adicionado valor padrão
    val tipoLayout: Int = 0, // Adicionado valor padrão
    val timestamp: Timestamp? = null // NOVO CAMPO para ordenação e mapeamento
) {
    // Definimos constantes para os tipos de layout para evitar números mágicos
    companion object {
        const val TIPO_IMAGEM_GRANDE = 1
        const val TIPO_IMAGEM_LATERAL = 2
    }
}