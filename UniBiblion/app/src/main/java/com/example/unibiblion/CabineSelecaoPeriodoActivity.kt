package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CabineSelecaoPeriodoActivity : AppCompatActivity() {

    // VARIÁVEIS DE REFERÊNCIA
    private lateinit var tvCabineDetalhes: TextView
    private lateinit var recyclerTimeSlots: RecyclerView
    private lateinit var tvResumoPeriodo: TextView
    private lateinit var btnConfirmar: Button

    private lateinit var listaSlots: MutableList<TimeSlot>

    // VARIÁVEIS PARA OTIMIZAÇÃO: Armazenam a seleção atual para o botão CONFIRMAR
    private var selectedStartTime: Calendar? = null
    private var selectedEndTime: Calendar? = null

    // CONSTANTES
    // O horário máximo que pode ser reservado
    private val HORARIO_FIM_DIA = 22

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cabine_selecao_periodo)

        supportActionBar?.hide()

        // 1. Obter referências dos IDs
        tvCabineDetalhes = findViewById(R.id.tv_cabine_detalhes)
        recyclerTimeSlots = findViewById(R.id.recycler_time_slots)
        tvResumoPeriodo = findViewById(R.id.tv_resumo_periodo)
        btnConfirmar = findViewById(R.id.btn_confirmar_reserva_periodo)

        // 2. Receber os dados da Activity anterior
        val numeroCabine = intent.getStringExtra("EXTRA_NUMERO_CABINE") ?: "00"
        val dataHoraMillis = intent.getLongExtra("EXTRA_DATA_HORA", 0L)

        // 3. Criar a data inicial
        val dataHoraInicial = Calendar.getInstance().apply {
            timeInMillis = dataHoraMillis
        }

        // 4. Exibir detalhes no topo
        val dateFormat = SimpleDateFormat("EEEE, dd/MM/yyyy", Locale("pt", "BR"))
        val textoDetalhes = "Cabine $numeroCabine - ${dateFormat.format(dataHoraInicial.time)}"
        tvCabineDetalhes.text = textoDetalhes

        // 5. Gerar a lista de slots de tempo
        listaSlots = gerarSlotsDeTempo(dataHoraInicial)

        // PRÉ-SELECIONAR O PRIMEIRO SLOT DISPONÍVEL (UX aprimorada)
        val primeiroSlotDisponivel = listaSlots.firstOrNull { it.isAvailable }
        if (primeiroSlotDisponivel != null) {
            primeiroSlotDisponivel.isSelected = true
        }

        // 6. Configurar o RecyclerView (Linha do Tempo)
        val adapter = TimeSlotAdapter(listaSlots) { startTime, endTime ->
            // Callback: Chamado toda vez que o usuário muda a seleção
            atualizarResumoSelecao(startTime, endTime)
        }

        // Define que a lista será horizontal
        recyclerTimeSlots.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerTimeSlots.adapter = adapter

        // Notificar a Activity sobre a pré-seleção inicial, se houver, para habilitar o botão
        if (primeiroSlotDisponivel != null) {
            atualizarResumoSelecao(primeiroSlotDisponivel.startTime, primeiroSlotDisponivel.endTime)
        }

        // 7. Define a Ação do Botão UMA ÚNICA VEZ (Otimização)
        btnConfirmar.setOnClickListener {
            // Verifica se as variáveis de classe têm os dados de seleção
            if (selectedStartTime != null && selectedEndTime != null) {

                // 1. Coleta os dados essenciais
                val cabineNumero = intent.getStringExtra("EXTRA_NUMERO_CABINE")
                val dataGeral = selectedStartTime!!.timeInMillis // A data base da reserva

                // 2. Cria o Intent para a tela de Sucesso
                val intentSucesso = Intent(this, ReservaSucessoActivity::class.java).apply {
                    putExtra("EXTRA_NUMERO_CABINE", cabineNumero)
                    putExtra("EXTRA_DATA", dataGeral)
                    putExtra("EXTRA_INICIO_HORA", selectedStartTime!!.timeInMillis)
                    putExtra("EXTRA_FIM_HORA", selectedEndTime!!.timeInMillis)
                }
                startActivity(intentSucesso)

                // Finaliza esta Activity, para que o botão "Voltar" da tela de sucesso leve ao Home
                finish()
            } else {
                Toast.makeText(this, "Erro: Seleção inválida. Tente novamente.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Função que simula a disponibilidade da cabine para o dia (sem alterações)
    private fun gerarSlotsDeTempo(dataHoraBase: Calendar): MutableList<TimeSlot> {
        val slots = mutableListOf<TimeSlot>()

        val HORA_INICIO_SELECAO = dataHoraBase.get(Calendar.HOUR_OF_DAY)

        val slotCalendar = dataHoraBase.clone() as Calendar
        slotCalendar.set(Calendar.MINUTE, 0)
        slotCalendar.set(Calendar.SECOND, 0)

        for (hora in HORA_INICIO_SELECAO until HORARIO_FIM_DIA) {

            val startTime = slotCalendar.clone() as Calendar
            slotCalendar.add(Calendar.HOUR_OF_DAY, 1)
            val endTime = slotCalendar.clone() as Calendar

            val isAvailable = when (hora) {
                15, 18 -> false
                else -> true
            }

            slots.add(TimeSlot(startTime, endTime, isAvailable))
        }

        return slots
    }

    /**
     * Atualiza o resumo de tempo e o estado do botão com base na seleção do adapter.
     */
    private fun atualizarResumoSelecao(startTime: Calendar?, endTime: Calendar?) {
        // Salva a seleção nas variáveis de classe para o botão CONFIRMAR usar
        selectedStartTime = startTime
        selectedEndTime = endTime

        if (startTime == null || endTime == null) {
            tvResumoPeriodo.visibility = View.GONE
            btnConfirmar.isEnabled = false // Desabilita o botão
            return
        }

        // 1. Calcular a duração em horas
        val duracaoMillis = endTime.timeInMillis - startTime.timeInMillis
        val duracaoHoras = duracaoMillis / (1000 * 60 * 60)

        // 2. Formatar a hora
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val horaInicioStr = timeFormat.format(startTime.time)
        val horaFimStr = timeFormat.format(endTime.time)

        // 3. Montar e exibir o resumo
        val resumo = "Reserva: $horaInicioStr até $horaFimStr ($duracaoHoras horas)"
        tvResumoPeriodo.text = resumo
        tvResumoPeriodo.visibility = View.VISIBLE

        // 4. Habilitar o botão
        btnConfirmar.isEnabled = true

        // NOTA: O setOnClickListener foi removido daqui e colocado no onCreate.
    }
}