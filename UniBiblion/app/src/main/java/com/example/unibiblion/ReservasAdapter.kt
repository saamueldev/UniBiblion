package com.example.unibiblion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class ReservasAdapter(
    private val reservas: List<Reserva>,
    private val onActionClick: (Reserva) -> Unit // Função de callback para o botão Ação (ex: Cancelar)
) : RecyclerView.Adapter<ReservasAdapter.ReservaViewHolder>() {

    // Formatadores para Data e Hora
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    // 1. O ViewHolder: Mantém referências aos componentes de layout (item_reserva.xml)
    inner class ReservaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCabineData: TextView = itemView.findViewById(R.id.tv_cabine_data)
        val tvHorario: TextView = itemView.findViewById(R.id.tv_horario)
        val imgStatusIndicator: ImageView = itemView.findViewById(R.id.img_status_indicator)
        val btnAcaoReserva: Button = itemView.findViewById(R.id.btn_acao_reserva)

        fun bind(reserva: Reserva) {

            // CONTEÚDO PRINCIPAL
            val dataStr = dateFormat.format(reserva.startTime.time)
            tvCabineData.text = "Cabine ${reserva.numeroCabine} - $dataStr"

            val horaInicioStr = timeFormat.format(reserva.startTime.time)
            val horaFimStr = timeFormat.format(reserva.endTime.time)

            // Cálculo da Duração (em horas)
            val duracaoMillis = reserva.endTime.timeInMillis - reserva.startTime.timeInMillis
            val duracaoHoras = duracaoMillis / (1000 * 60 * 60)
            tvHorario.text = "$horaInicioStr às $horaFimStr (${duracaoHoras}h)"

            // ESTILIZAÇÃO E AÇÕES BASEADAS NO STATUS
            when (reserva.status) {
                StatusReserva.ATIVA -> {
                    // Mudar a cor para indicar que está Ativa
                    val color = ContextCompat.getColor(itemView.context, R.color.blue_500)
                    imgStatusIndicator.setColorFilter(color)
                    tvCabineData.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.black))

                    // Mostrar botão "Cancelar"
                    btnAcaoReserva.visibility = View.VISIBLE
                    btnAcaoReserva.text = "Cancelar"
                    btnAcaoReserva.setOnClickListener {
                        onActionClick(reserva)
                    }
                }
                StatusReserva.CONCLUIDA -> {
                    // Mudar a cor para indicar que está Concluída (cinza/passado)
                    val color = ContextCompat.getColor(itemView.context, android.R.color.darker_gray)
                    imgStatusIndicator.setColorFilter(color)
                    tvCabineData.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.darker_gray))

                    // Ocultar botão
                    btnAcaoReserva.visibility = View.GONE
                    btnAcaoReserva.setOnClickListener(null) // Remover listener por segurança
                }
                StatusReserva.CANCELADA -> {
                    // TO-DO: Implementar estilo de cancelada, se necessário
                    val color = ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark)
                    imgStatusIndicator.setColorFilter(color)
                    btnAcaoReserva.visibility = View.GONE
                }
            }
        }
    }

    // 2. Cria o ViewHolder (infla o item_reserva.xml)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reserva, parent, false)
        return ReservaViewHolder(view)
    }

    // 3. Vincula os dados ao ViewHolder
    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        holder.bind(reservas[position])
    }

    // 4. Retorna a quantidade de itens na lista
    override fun getItemCount(): Int = reservas.size
}