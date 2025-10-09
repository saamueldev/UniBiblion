package com.example.unibiblion

data class Cabine(
    val numero: String,
    val estado: Int, // Usaremos constantes para definir o estado de ocupação (Livre/Ocupado)
    var isSelecionada: Boolean = false // Booleano para rastrear se o usuário clicou nela
) {
    // Definimos constantes para facilitar a leitura e evitar 'números mágicos'
    companion object {
        const val ESTADO_LIVRE = 1
        const val ESTADO_OCUPADO = 2
    }
}