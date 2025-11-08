package com.example.unibiblion

import com.google.firebase.firestore.DocumentId

// Adicionamos valores padrão nulos e um construtor vazio implícito para o Firestore.
data class Cabine(
    @DocumentId // Mapeia o ID do documento (se você quiser usar o ID do Firestore como identificador)
    val id: String? = null,
    val numero: String? = null,
    val estado: String? = null // Armazenaremos a String do estado
) {
    // Definimos as constantes como strings que serão salvas no Firestore
    companion object {
        const val ESTADO_LIVRE = "LIVRE"
        const val ESTADO_OCUPADO = "OCUPADO"
    }

    // Construtor sem argumentos necessário para o Firestore
    @Suppress("unused")
    constructor() : this(null, null, null)
}