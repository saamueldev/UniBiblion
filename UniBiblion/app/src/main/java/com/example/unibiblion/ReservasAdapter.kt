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
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class ReservasAdapter(
    private val reservas: List<Reserva>,
    private val onActionClick: (Reserva) -> Unit
) : RecyclerView.Adapter<ReservasAdapter.ReservaViewHolder>() {

    // Formatadores
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    private val timeParser = SimpleDateFormat("HH:mm", Locale.getDefault())


    inner class ReservaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCabineData: TextView = itemView.findViewById(R.id.tv_cabine_data)
        val tvHorario: TextView = itemView.findViewById(R.id.tv_horario)
        val imgStatusIndicator: ImageView = itemView.findViewById(R.id.img_status_indicator)
        val btnAcaoReserva: Button = itemView.findViewById(R.id.btn_acao_reserva)

        fun bind(reserva: Reserva) {

            // 🎯 SIMPLIFICADO: Leitura direta dos campos (Sem .replace("\"", ""))
            val cabineNumero = reserva.cabineNumero
            val dataReserva = reserva.dataReserva
            val horaInicio = reserva.horaInicio
            val horaFim = reserva.horaFim
            val statusStr = reserva.status

            if (dataReserva == null || horaInicio == null || horaFim == null || cabineNumero == null || statusStr == null) {
                tvCabineData.text = "Erro: Dados da reserva incompletos"
                tvHorario.text = ""
                return
            }

            // 1. FORMATAR DATA
            tvCabineData.text = "Cabine $cabineNumero - $dataReserva"

            // 2. CALCULAR DURAÇÃO
            try {
                val dateInicio: Date = timeParser.parse(horaInicio) ?: throw IllegalStateException("Erro no parsing da hora de início")
                val dateFim: Date = timeParser.parse(horaFim) ?: throw IllegalStateException("Erro no parsing da hora de fim")

                val duracaoMillis = dateFim.time - dateInicio.time
                val duracaoHoras = TimeUnit.MILLISECONDS.toHours(duracaoMillis)

                tvHorario.text = "$horaInicio às $horaFim (${duracaoHoras}h)"

            } catch (e: Exception) {
                tvHorario.text = "$horaInicio às $horaFim (Erro no cálculo de duração)"
            }

            // 3. ESTILIZAÇÃO E AÇÕES BASEADAS NO STATUS
            val status = StatusReserva.valueOf(statusStr)

            when (status) {
                StatusReserva.ATIVA -> {
                    val color = ContextCompat.getColor(itemView.context, R.color.blue_500)
                    imgStatusIndicator.setColorFilter(color)
                    tvCabineData.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.black))

                    btnAcaoReserva.visibility = View.VISIBLE
                    btnAcaoReserva.text = "Cancelar"
                    btnAcaoReserva.setOnClickListener {
                        onActionClick(reserva)
                    }
                }
                StatusReserva.FINALIZADA -> {
                    val color = ContextCompat.getColor(itemView.context, R.color.blue_500)
                    imgStatusIndicator.setColorFilter(color)
                    tvCabineData.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.darker_gray))
                    btnAcaoReserva.visibility = View.GONE
                    btnAcaoReserva.setOnClickListener(null)
                }
                StatusReserva.CANCELADA -> {
                    val color = ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark)
                    imgStatusIndicator.setColorFilter(color)
                    btnAcaoReserva.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reserva, parent, false)
        return ReservaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        holder.bind(reservas[position])
    }

    override fun getItemCount(): Int = reservas.size
}