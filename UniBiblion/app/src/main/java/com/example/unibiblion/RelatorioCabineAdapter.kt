package com.example.unibiblion

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class RelatorioCabineAdapter(
    private var listaRelatorios: List<RelatorioCabine>
) : RecyclerView.Adapter<RelatorioCabineAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNumeroCabine: TextView = itemView.findViewById(R.id.tv_numero_cabine)
        val tvNomeUsuario: TextView = itemView.findViewById(R.id.tv_nome_usuario)
        val tvDataReserva: TextView = itemView.findViewById(R.id.tv_data_reserva)
        val tvPeriodo: TextView = itemView.findViewById(R.id.tv_periodo)
        val tvStatus: TextView = itemView.findViewById(R.id.tv_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_relatorio_cabine, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val relatorio = listaRelatorios[position]
        val sdfInput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sdfOutput = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Define os textos
        holder.tvNumeroCabine.text = "🏢 Cabine: ${relatorio.cabineNumero}"
        holder.tvNomeUsuario.text = "👤 Reservado por: ${relatorio.nomeUsuario}"

        // Formata a data
        try {
            val data = sdfInput.parse(relatorio.dataReserva)
            holder.tvDataReserva.text = "📅 Data: ${data?.let { sdfOutput.format(it) } ?: relatorio.dataReserva}"
        } catch (e: Exception) {
            holder.tvDataReserva.text = "📅 Data: ${relatorio.dataReserva}"
        }

        holder.tvPeriodo.text = "🕒 Período: ${relatorio.horaInicio} - ${relatorio.horaFim}"

        // Define o status com cores diferentes
        when (relatorio.status) {
            "ATIVA" -> {
                holder.tvStatus.text = "ATIVA"
                holder.tvStatus.setBackgroundResource(R.drawable.bg_button_purple)
                holder.tvStatus.setTextColor(Color.WHITE)
            }
            "FINALIZADA" -> {
                holder.tvStatus.text = "FINALIZADA"
                holder.tvStatus.setBackgroundResource(R.drawable.rounded_edittext_background)
                holder.tvStatus.setTextColor(Color.parseColor("#2196F3"))
            }
            "CANCELADA" -> {
                holder.tvStatus.text = "CANCELADA"
                holder.tvStatus.setBackgroundResource(R.drawable.rounded_edittext_background_red)
                holder.tvStatus.setTextColor(Color.parseColor("#D32F2F"))
            }
            else -> {
                holder.tvStatus.text = relatorio.status
                holder.tvStatus.setBackgroundResource(R.drawable.rounded_edittext_background)
                holder.tvStatus.setTextColor(Color.BLACK)
            }
        }
    }

    override fun getItemCount(): Int = listaRelatorios.size

    /**
     * Atualiza a lista de relatórios e notifica o adapter
     */
    fun atualizarLista(novaLista: List<RelatorioCabine>) {
        listaRelatorios = novaLista
        notifyDataSetChanged()
    }

    /**
     * Retorna a lista atual de cabines
     */
    fun getListaCabines(): List<RelatorioCabine> = listaRelatorios
}