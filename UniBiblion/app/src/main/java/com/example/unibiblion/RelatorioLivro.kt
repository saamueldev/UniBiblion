package com.example.unibiblion

import com.google.firebase.Timestamp

/**
 * Classe de dados para exibir informações completas de um aluguel de livro nos relatórios.
 * Combina dados da coleção 'livrosalugados' com informações do usuário.
 */
data class RelatorioLivro(
    val id: String = "", // ID do documento em 'livrosalugados'
    val livroId: String = "",
    val tituloLivro: String = "",
    val capaURL: String = "",
    val usuarioId: String = "",
    val nomeUsuario: String = "Usuário não identificado",
    val emailUsuario: String = "",
    val dataRetirada: String = "",
    val horarioRetirada: String = "",
    val dataDevolucao: Timestamp? = null,
    val renovado: Boolean = false,
    val timestampCriacao: Timestamp? = null, // Timestamp da criação do aluguel
    val devolvido: Boolean = false, // Se o livro já foi devolvido
    val dataRealDevolucao: Timestamp? = null // Data real em que foi devolvido
)
