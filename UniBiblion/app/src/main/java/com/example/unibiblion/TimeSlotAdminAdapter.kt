package com.example.unibiblion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.unibiblion.R // Assumindo que R está disponível

// Definição da função de clique que será passada para a Activity
typealias TimeSlotClickListener = (slot: TimeSlotAdmin, position: Int) -> Unit

class TimeSlotAdminAdapter(
    initialSlots: MutableList<TimeSlotAdmin>,
    private val listener: TimeSlotClickListener
) : RecyclerView.Adapter<TimeSlotAdminAdapter.TimeSlotViewHolder>() {

    // Propriedade que a Activity precisa acessar
    var slots: MutableList<TimeSlotAdmin> = initialSlots
        private set

    // ----------------------------------------------------
    // 1. ViewHolder
    // ----------------------------------------------------
    inner class TimeSlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTime: TextView = itemView.findViewById(R.id.tv_slot_time) // Ajuste o ID conforme seu layout
        val container: View = itemView // Ou o ID do layout principal se for um container

        fun bind(slot: TimeSlotAdmin) {
            tvTime.text = "${slot.startHour}\n${slot.endHour}"

            // 🎯 LÓGICA DE CORREÇÃO: Três estados visuais
            val context = itemView.context

            if (slot.isReservadoPeloUsuario) {
                // 1. ESTADO RESERVADO (CINZA): Não pode ser alterado pelo Admin

                // 💡 Nota: Você precisará criar este drawable, por exemplo, slot_reservado_user_bg.
                // Pode ser cinza escuro ou apenas cinza claro com borda sólida.
                container.background = ContextCompat.getDrawable(context, R.drawable.slot_reservado_user_bg)

                // Desativa o clique
                container.setOnClickListener(null)
                container.isClickable = false
                container.alpha = 0.5f // Diminui a opacidade para reforçar que está bloqueado (Opcional)

            } else {
                // 2. ESTADO LIVRE/RESTRITO (VERDE/VERMELHO): Pode ser alternado pelo Admin

                val backgroundResId = if (slot.isIndisponivel) {
                    R.drawable.slot_indisponivel_admin_bg // Restrito pelo Admin (ex: Vermelho)
                } else {
                    R.drawable.slot_disponivel_admin_bg // Disponível (ex: Verde)
                }
                container.background = ContextCompat.getDrawable(context, backgroundResId)

                container.isClickable = true
                container.alpha = 1.0f

                // Lógica de Clique: Alterna o estado (isIndisponivel) e notifica a Activity
                container.setOnClickListener {
                    slot.isIndisponivel = !slot.isIndisponivel
                    notifyItemChanged(adapterPosition)
                    listener.invoke(slot, adapterPosition)
                }
            }
        }
    }

    // ----------------------------------------------------
    // 2. Implementações do Adapter
    // ----------------------------------------------------

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_time_slot, parent, false)
        return TimeSlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimeSlotViewHolder, position: Int) {
        holder.bind(slots[position])
    }

    override fun getItemCount(): Int = slots.size

    // ----------------------------------------------------
    // 3. Método updateSlots
    // ----------------------------------------------------

    fun updateSlots(newSlots: List<TimeSlotAdmin>) {
        this.slots.clear()
        // Adiciona a lista recebida, que agora contém as marcações isReservadoPeloUsuario
        this.slots.addAll(newSlots)
        notifyDataSetChanged()
    }
}