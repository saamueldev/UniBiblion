package com.example.unibiblion

// Usamos um data class para facilitar a manipulação de dados
data class Noticia(
    val titulo: String,
    val preview: String,
    val urlImagem: String,
    val tipoLayout: Int // ESSENCIAL: define se usa Imagem Grande (1) ou Lateral (2)
) {
    // Definimos constantes para os tipos de layout para evitar números mágicos
    companion object {
        const val TIPO_IMAGEM_GRANDE = 1
        const val TIPO_IMAGEM_LATERAL = 2
    }
}