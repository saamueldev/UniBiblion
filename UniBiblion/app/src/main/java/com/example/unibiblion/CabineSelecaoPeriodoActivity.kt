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
import java.util.Date
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

// Assumindo que TimeSlot, Reserva e StatusReserva estão definidos em outros arquivos/classes

class CabineSelecaoPeriodoActivity : AppCompatActivity() {

    // VARIÁVEIS DE REFERÊNCIA
    private lateinit var tvCabineDetalhes: TextView
    private lateinit var recyclerTimeSlots: RecyclerView
    private lateinit var tvResumoPeriodo: TextView
    private lateinit var btnConfirmar: Button

    private lateinit var listaSlots: MutableList<TimeSlot>

    // VARIÁVEIS DO FIREBASE
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // VARIÁVEIS PARA OTIMIZAÇÃO
    private var selectedStartTime: Calendar? = null
    private var selectedEndTime: Calendar? = null
    private lateinit var dataHoraInicial: Calendar

    // CONSTANTES
    private val HORARIO_FIM_DIA = 22
    // Formato para armazenamento no Firebase (agora ordenável)
    private val dateFormatDatabase = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cabine_selecao_periodo)

        supportActionBar?.hide()

        // INICIALIZAÇÃO DO FIREBASE
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        iniciarConfiguracaoDaActivity()
    }

    // ==========================================================
    // CONTEÚDO ORIGINAL DO ONCREATE
    // ==========================================================
    private fun iniciarConfiguracaoDaActivity() {
        // 1. Obter referências dos IDs
        tvCabineDetalhes = findViewById(R.id.tv_cabine_detalhes)
        recyclerTimeSlots = findViewById(R.id.recycler_time_slots)
        tvResumoPeriodo = findViewById(R.id.tv_resumo_periodo)
        btnConfirmar = findViewById(R.id.btn_confirmar_reserva_periodo)

        // 2. Receber os dados da Activity anterior
        val numeroCabine = intent.getStringExtra("EXTRA_NUMERO_CABINE") ?: "00"
        val dataHoraMillis = intent.getLongExtra("EXTRA_DATA_HORA", 0L)

        // 3. Criar a data inicial
        dataHoraInicial = Calendar.getInstance().apply {
            timeInMillis = dataHoraMillis
        }

        // 4. Exibir detalhes no topo
        val dateFormatDisplay = SimpleDateFormat("EEEE, dd/MM/yyyy", Locale("pt", "BR"))
        val textoDetalhes = "Cabine $numeroCabine - ${dateFormatDisplay.format(dataHoraInicial.time)}"
        tvCabineDetalhes.text = textoDetalhes

        // 5. INICIA A BUSCA DE DISPONIBILIDADE NO FIREBASE
        val cabineNumero = intent.getStringExtra("EXTRA_NUMERO_CABINE") ?: "00"
        val dataReservaStr = dateFormatDatabase.format(dataHoraInicial.time)

        buscarReservasExistentes(cabineNumero, dataReservaStr)

        // 6. Define a Ação do Botão
        btnConfirmar.setOnClickListener {
            salvarReserva()
        }
    }


    /**
     * Consulta o Firebase para obter todas as reservas ATIVAS para a cabine e data.
     */
    private fun buscarReservasExistentes(cabineNumero: String, dataReservaStr: String) {
        db.collection("reservas")
            .whereEqualTo("cabineNumero", cabineNumero)
            .whereEqualTo("dataReserva", dataReservaStr)
            .whereEqualTo("status", StatusReserva.ATIVA.name)
            .get()
            .addOnSuccessListener { result ->
                val reservasOcupadas = result.toObjects(Reserva::class.java)

                // 1. Gera a lista completa de slots (todos disponíveis, aplicando a checagem de tempo)
                val todosSlots = gerarTodosSlotsDeTempo(dataHoraInicial)

                // 2. Marca os slots que estão ocupados pelas reservas do Firebase
                marcarSlotsOcupados(todosSlots, reservasOcupadas)

                // 3. Configura o RecyclerView com a lista final de disponibilidade
                configurarSlotsUI(todosSlots)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar disponibilidade. ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                // Se falhar, exibe a lista como se estivesse toda disponível (mas com a checagem de tempo)
                configurarSlotsUI(gerarTodosSlotsDeTempo(dataHoraInicial))
            }
    }

    /**
     * Processa as reservas do Firebase e marca os slots correspondentes como indisponíveis.
     */
    private fun marcarSlotsOcupados(todosSlots: MutableList<TimeSlot>, reservasOcupadas: List<Reserva>) {

        val baseCalendar = dataHoraInicial.clone() as Calendar

        for (reserva in reservasOcupadas) {
            try {
                // REVERTIDO: Lendo as horas diretamente, sem .replace("\"", "")
                val horaInicio = reserva.horaInicio ?: continue
                val horaFim = reserva.horaFim ?: continue

                val inicioParts = horaInicio.split(":")
                val fimParts = horaFim.split(":")

                if (inicioParts.size != 2 || fimParts.size != 2) continue

                // Preparamos o Milissegundo de INÍCIO da RESERVA (com a data correta)
                val inicioOcupadoCalendar = baseCalendar.clone() as Calendar
                inicioOcupadoCalendar.set(Calendar.HOUR_OF_DAY, inicioParts[0].toInt())
                inicioOcupadoCalendar.set(Calendar.MINUTE, inicioParts[1].toInt())
                inicioOcupadoCalendar.set(Calendar.SECOND, 0)
                inicioOcupadoCalendar.set(Calendar.MILLISECOND, 0)
                val inicioOcupadoMillis = inicioOcupadoCalendar.timeInMillis

                // Preparamos o Milissegundo de FIM da RESERVA (com a data correta)
                val fimOcupadoCalendar = baseCalendar.clone() as Calendar
                fimOcupadoCalendar.set(Calendar.HOUR_OF_DAY, fimParts[0].toInt())
                fimOcupadoCalendar.set(Calendar.MINUTE, fimParts[1].toInt())
                fimOcupadoCalendar.set(Calendar.SECOND, 0)
                fimOcupadoCalendar.set(Calendar.MILLISECOND, 0)
                val fimOcupadoMillis = fimOcupadoCalendar.timeInMillis

                // Comparamos com cada slot
                for (index in todosSlots.indices) {
                    val slot = todosSlots[index]
                    if (!slot.isAvailable) continue

                    val slotStartTimeMillis = slot.startTime.timeInMillis

                    // Lógica de Ocupação:
                    if (slotStartTimeMillis >= inicioOcupadoMillis && slotStartTimeMillis < fimOcupadoMillis) {
                        todosSlots[index] = slot.copy(isAvailable = false)
                    }
                }
            } catch (e: Exception) {
                continue
            }
        }
    }


    /**
     * Gera todos os slots de tempo possíveis, aplicando a checagem de horário para bloquear
     * slots no passado se a reserva for para HOJE.
     */
    private fun gerarTodosSlotsDeTempo(dataHoraBase: Calendar): MutableList<TimeSlot> {
        val slots = mutableListOf<TimeSlot>()

        val HORA_INICIO_SELECAO = 8 // Assume que a biblioteca abre às 8h
        val HORA_FIM_SELECAO = HORARIO_FIM_DIA // 22h

        var slotCalendar = dataHoraBase.clone() as Calendar
        slotCalendar.set(Calendar.MINUTE, 0)
        slotCalendar.set(Calendar.SECOND, 0)
        slotCalendar.set(Calendar.MILLISECOND, 0)

        // 1. CHECAGEM DE DATA: Verifica se a reserva é para HOJE
        val agora = Calendar.getInstance()
        val isToday = (dataHoraBase.get(Calendar.YEAR) == agora.get(Calendar.YEAR) &&
                dataHoraBase.get(Calendar.DAY_OF_YEAR) == agora.get(Calendar.DAY_OF_YEAR))

        // 2. DEFINE A HORA DE CORTE (apenas se for hoje)
        var horaMinimaPermitida: Calendar? = null

        if (isToday) {
            // Se for hoje, arredonda a hora atual para a próxima hora cheia.
            // Ex: 19:32 -> 20:00.
            val proximaHoraCheia = agora.get(Calendar.HOUR_OF_DAY) + 1

            horaMinimaPermitida = dataHoraBase.clone() as Calendar
            horaMinimaPermitida.set(Calendar.HOUR_OF_DAY, proximaHoraCheia)
            horaMinimaPermitida.set(Calendar.MINUTE, 0)
            horaMinimaPermitida.set(Calendar.SECOND, 0)
            horaMinimaPermitida.set(Calendar.MILLISECOND, 0)
        }

        // 3. GERAÇÃO E CHECAGEM DOS SLOTS
        for (hora in HORA_INICIO_SELECAO until HORA_FIM_SELECAO) {

            // Define o início do slot (ex: 19:00)
            val startTime = slotCalendar.clone() as Calendar
            startTime.set(Calendar.HOUR_OF_DAY, hora)
            startTime.set(Calendar.MINUTE, 0)

            // Define o fim do slot (ex: 20:00)
            val endTime = startTime.clone() as Calendar
            endTime.add(Calendar.HOUR_OF_DAY, 1)

            var isAvailable = true

            // Aplica o bloqueio de slots passados
            if (isToday && horaMinimaPermitida != null) {
                // Se o início do slot (ex: 19:00) for ANTES da hora mínima permitida (ex: 20:00), bloqueia.
                if (startTime.before(horaMinimaPermitida)) {
                    isAvailable = false
                }
            }

            slots.add(TimeSlot(startTime, endTime, isAvailable = isAvailable))
        }

        return slots
    }

    /**
     * Configura o RecyclerView e o resumo inicial.
     */
    private fun configurarSlotsUI(slots: MutableList<TimeSlot>) {
        listaSlots = slots

        // Tenta pré-selecionar o primeiro slot DISPONÍVEL, se houver
        val primeiroSlotDisponivel = listaSlots.firstOrNull { it.isAvailable }
        if (primeiroSlotDisponivel != null) {
            primeiroSlotDisponivel.isSelected = true
        }

        val adapter = TimeSlotAdapter(listaSlots) { startTime, endTime ->
            atualizarResumoSelecao(startTime, endTime)
        }

        recyclerTimeSlots.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerTimeSlots.adapter = adapter

        if (primeiroSlotDisponivel != null) {
            atualizarResumoSelecao(primeiroSlotDisponivel.startTime, primeiroSlotDisponivel.endTime)
        } else {
            // Se não houver slots disponíveis (ex: todos no passado ou ocupados)
            atualizarResumoSelecao(null, null)
        }
    }

    private fun atualizarResumoSelecao(startTime: Calendar?, endTime: Calendar?) {
        selectedStartTime = startTime
        selectedEndTime = endTime

        if (startTime == null || endTime == null) {
            tvResumoPeriodo.visibility = View.GONE
            btnConfirmar.isEnabled = false
            return
        }

        val duracaoMillis = endTime.timeInMillis - startTime.timeInMillis
        val duracaoHoras = duracaoMillis / (1000 * 60 * 60)

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val horaInicioStr = timeFormat.format(startTime.time)
        val horaFimStr = timeFormat.format(endTime.time)

        val resumo = "Reserva: $horaInicioStr até $horaFimStr ($duracaoHoras horas)"
        tvResumoPeriodo.text = resumo
        tvResumoPeriodo.visibility = View.VISIBLE

        btnConfirmar.isEnabled = true
    }

    private fun salvarReserva() {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "Erro: Usuário não autenticado. Tente fazer login.", Toast.LENGTH_LONG).show()
            return
        }

        if (selectedStartTime == null || selectedEndTime == null) {
            Toast.makeText(this, "Erro: Seleção de período inválida. Tente novamente.", Toast.LENGTH_SHORT).show()
            return
        }

        // Formatação dos Dados
        val cabineNumero = intent.getStringExtra("EXTRA_NUMERO_CABINE")

        // Usa o formato corrigido: YYYY-MM-DD
        val dataReservaStr = dateFormatDatabase.format(dataHoraInicial.time)

        val horaInicioStr = timeFormat.format(selectedStartTime!!.time)
        val horaFimStr = timeFormat.format(selectedEndTime!!.time)

        // REMOVIDO: Limpeza de aspas não é mais necessária
        // val cabineNumeroLimpo = cabineNumero?.replace("\"", "")

        val novaReserva = Reserva(
            cabineNumero = cabineNumero, // REVERTIDO: Usando cabineNumero diretamente
            usuarioId = userId,
            dataReserva = dataReservaStr,
            horaInicio = horaInicioStr,
            horaFim = horaFimStr,
            status = StatusReserva.ATIVA.name,
            timestampCriacao = Timestamp.now()
        )

        // Salvar no Firestore
        db.collection("reservas")
            .add(novaReserva)
            .addOnSuccessListener {
                Toast.makeText(this, "Reserva da Cabine $cabineNumero confirmada com sucesso!", Toast.LENGTH_LONG).show() // REVERTIDO: cabineNumero direto

                // Navegação para a tela de Sucesso
                val intentSucesso = Intent(this, ReservaSucessoActivity::class.java).apply {
                    putExtra("EXTRA_NUMERO_CABINE", cabineNumero) // REVERTIDO: cabineNumero direto
                    putExtra("EXTRA_DATA", dataHoraInicial.timeInMillis)
                    putExtra("EXTRA_INICIO_HORA", selectedStartTime!!.timeInMillis)
                    putExtra("EXTRA_FIM_HORA", selectedEndTime!!.timeInMillis)
                }
                startActivity(intentSucesso)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao salvar reserva. Tente novamente.", Toast.LENGTH_LONG).show()
            }
    }
}