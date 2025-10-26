package com.example.unibiblion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

// O listener notifica a Activity ou Fragment sobre a mudança de estado
class TimeSlotAdminAdapter(
    private val slots: MutableList<TimeSlotAdmin>,
    private val onSlotStatusChanged: (TimeSlotAdmin, Int) -> Unit
) : RecyclerView.Adapter<TimeSlotAdminAdapter.SlotViewHolder>() {

    // CORREÇÃO: Usamos o LinearLayout do item_time_slot.xml como o container principal
    // E os IDs que corrigimos na resposta anterior: tv_slot_time e view_slot_block
    class SlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val slotBlockView: View = itemView.findViewById(R.id.view_slot_block) // O bloco colorido
        val timeText: TextView = itemView.findViewById(R.id.tv_slot_time) // O texto do horário
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        // CORREÇÃO: Usamos o nome de arquivo correto (item_time_slot)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_time_slot, parent, false)
        return SlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val slot = slots[position]
        holder.timeText.text = "${slot.horarioInicio} - ${slot.horarioFim}"

        // Define o estilo baseado no status (Indisponível = Vermelho, Disponível = Verde/Padrão)
        updateSlotStyle(holder, slot.isIndisponivel)

        // LÓGICA DE CLIQUE (APLICADA A UMA FUNÇÃO PRIVADA REUTILIZÁVEL)
        val clickAction = View.OnClickListener {
            // Alterna o status
            slot.isIndisponivel = !slot.isIndisponivel
            // Atualiza o estilo visual
            updateSlotStyle(holder, slot.isIndisponivel)
            // Notifica a Activity da mudança
            onSlotStatusChanged(slot, position)
        }

        // APLICAÇÃO DA CORREÇÃO: Atachamos o listener no bloco de cor E no texto.
        // Isso garante que o clique em qualquer área visível dispare a ação.
        holder.slotBlockView.setOnClickListener(clickAction) // Clicar no quadrado verde/vermelho
        holder.timeText.setOnClickListener(clickAction)     // Clicar no texto do horário

        // Opcional: Se houver padding ou margem ao redor, manter o listener no itemView
        // holder.itemView.setOnClickListener(clickAction)
    }

    private fun updateSlotStyle(holder: SlotViewHolder, isIndisponivel: Boolean) {
        val context = holder.itemView.context

        val backgroundResId = if (isIndisponivel) {
            // Cor para Indisponível (ex: vermelho, slot_ocupado_bg)
            R.drawable.slot_ocupado_bg
        } else {
            // Cor para Disponível (ex: verde, slot_livre_bg)
            R.drawable.slot_livre_bg
        }

        // Aplica o drawable de cor no bloco (view_slot_block)
        holder.slotBlockView.setBackgroundResource(backgroundResId)

        // Opcional: Ajustar a cor do texto para melhor contraste
        holder.timeText.setTextColor(context.getColor(android.R.color.black))
    }

    override fun getItemCount() = slots.size
}