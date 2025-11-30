package com.example.unibiblion

import com.google.firebase.Timestamp

/**
 * Classe de dados para exibir informações completas de uma reserva de cabine nos relatórios.
 * Combina dados da coleção 'reservas' com informações do usuário.
 */
data class RelatorioCabine(
    val id: String = "", // ID do documento em 'reservas'
    val cabineNumero: String = "",
    val usuarioId: String = "",
    val nomeUsuario: String = "Usuário não identificado",
    val emailUsuario: String = "",
    val dataReserva: String = "", // Formato "YYYY-MM-DD"
    val horaInicio: String = "",
    val horaFim: String = "",
    val status: String = "", // ATIVA, CANCELADA, FINALIZADA
    val timestampCriacao: Timestamp? = null
)
