package com.example.unibiblion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TimeSlotAdapter(
    private val slots: MutableList<TimeSlot>,
    private val onSelectionChange: (startTime: Calendar?, endTime: Calendar?) -> Unit
) : RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder>() {

    // Rastreia o índice (posição) do primeiro slot selecionado
    private var startIndex: Int = -1

    // Classe ViewHolder interna
    inner class TimeSlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSlotTime: TextView = itemView.findViewById(R.id.tv_slot_time)
        val viewSlotBlock: View = itemView.findViewById(R.id.view_slot_block)

        init {
            viewSlotBlock.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    handleSlotClick(adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_time_slot, parent, false)
        return TimeSlotViewHolder(view)
    }

    override fun getItemCount(): Int = slots.size

    override fun onBindViewHolder(holder: TimeSlotViewHolder, position: Int) {
        val slot = slots[position]

        // Formata a hora de início para o TextView (ex: 13:00)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        holder.tvSlotTime.text = timeFormat.format(slot.startTime.time)

        // Aplica o estilo (cor)
        val backgroundResId = when {
            slot.isSelected -> R.drawable.slot_selecionado_bg
            !slot.isAvailable -> R.drawable.slot_ocupado_bg
            else -> R.drawable.slot_livre_bg
        }
        holder.viewSlotBlock.setBackgroundResource(backgroundResId)

        // Desabilita cliques em slots ocupados
        holder.viewSlotBlock.isEnabled = slot.isAvailable
        holder.viewSlotBlock.alpha = if (slot.isAvailable) 1.0f else 0.5f // Visualmente indisponível
    }

    /**
     * Lógica principal de seleção de range
     */
    private fun handleSlotClick(position: Int) {
        // Se a cabine não está livre, ignora o clique
        if (!slots[position].isAvailable) return

        if (startIndex == -1) {
            // Caso 1: Nenhuma seleção anterior. Começa o range/seleção única.

            // Limpa as seleções (segurança)
            clearAllVisualSelection()

            // Seleciona o slot clicado
            slots[position].isSelected = true
            startIndex = position

        } else {
            // Caso 2: Já existe um start index. Finaliza o range ou deseleciona.
            val endIndex = position

            if (startIndex == endIndex) {
                // CLIQUE DUPLO/TOGGLE: Usuário clicou no mesmo slot de início.
                clearSelection() // Limpa tudo

            } else {
                // CLIQUE DE RANGE: Usuário clicou em um slot diferente.

                // Verifica se o range é válido (sem slots ocupados no meio)
                if (isValidRange(startIndex, endIndex)) {

                    // Finaliza o range, selecionando os slots entre start e end
                    selectRange(startIndex, endIndex)

                } else {
                    // Range inválido (slot ocupado no meio). Limpa e começa um novo range.
                    clearSelection()

                    // Inicia uma nova seleção única no slot clicado agora.
                    slots[position].isSelected = true
                    startIndex = position
                }
            }
        }

        // Notifica o Adapter para redesenhar a linha do tempo
        notifyDataSetChanged()

        // Notifica a Activity sobre a mudança na seleção
        notifyActivitySelection()
    }

    private fun clearAllVisualSelection() {
        slots.forEach { it.isSelected = false }
    }

    // Zera todas as seleções e o start index
    private fun clearSelection() {
        clearAllVisualSelection()
        startIndex = -1
        notifyActivitySelection()
    }

    // Seleciona todos os slots entre o índice inicial e final (incluindo)
    private fun selectRange(start: Int, end: Int) {
        // Define o verdadeiro início e fim (em ordem numérica)
        val trueStart = minOf(start, end)
        val trueEnd = maxOf(start, end)

        // Zera as seleções antes de começar a nova
        slots.forEach { it.isSelected = false }

        for (i in trueStart..trueEnd) {
            slots[i].isSelected = true
        }
        startIndex = trueStart // Mantém o índice inicial para referência
    }

    // Verifica se há algum slot ocupado (isAvailable = false) dentro do range
    private fun isValidRange(start: Int, end: Int): Boolean {
        val trueStart = minOf(start, end)
        val trueEnd = maxOf(start, end)

        for (i in trueStart..trueEnd) {
            if (!slots[i].isAvailable) {
                return false // Encontrou um bloco ocupado
            }
        }
        return true
    }

    // Notifica a Activity qual é o período selecionado
    private fun notifyActivitySelection() {
        val selectedSlots = slots.filter { it.isSelected }

        if (selectedSlots.isEmpty()) {
            onSelectionChange(null, null)
            return
        }

        // O início é o startTime do primeiro slot selecionado
        val startTime = selectedSlots.first().startTime
        // O fim é o endTime do último slot selecionado
        val endTime = selectedSlots.last().endTime

        onSelectionChange(startTime, endTime)
    }
}