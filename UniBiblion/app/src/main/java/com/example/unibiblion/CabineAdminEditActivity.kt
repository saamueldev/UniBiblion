package com.example.unibiblion

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CabineAdminEditActivity : AppCompatActivity() {

    private lateinit var selectedCabineId: String
    private lateinit var selectedDate: Calendar
    private lateinit var timeSlotAdapter: TimeSlotAdminAdapter
    private lateinit var recyclerView: RecyclerView

    // FIREBASE
    private lateinit var db: FirebaseFirestore

    private val HORA_INICIO_FUNCIONAMENTO = 8
    private val HORA_FIM_FUNCIONAMENTO = 22
    private val dateFormatDatabase = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cabine_admin_edit)

        db = FirebaseFirestore.getInstance()

        // 1. Receber ID da Cabine
        selectedCabineId = intent.getStringExtra("CABINE_ID") ?: throw IllegalStateException("CABINE_ID obrigatório.")
        val cabineNome = "Cabine $selectedCabineId - Edição"
        findViewById<TextView>(R.id.tv_cabine_nome).text = cabineNome

        // 2. Configurar Data: Receber o valor da Intent ou usar o dia atual como fallback
        val dataMillis = intent.getLongExtra("EXTRA_DATA_SELECIONADA", 0L)

        selectedDate = Calendar.getInstance()
        if (dataMillis != 0L) {
            selectedDate.timeInMillis = dataMillis
        }

        updateDateDisplay()

        // 🎯 Lógica do Seletor de Data
        findViewById<TextView>(R.id.tv_date_selector).setOnClickListener {
            mostrarDatePicker()
        }

        // 3. Configurar RecyclerView
        recyclerView = findViewById(R.id.recycler_horarios_admin)
        val todosHorariosBase = criarHorariosBase()

        // 🎯 Inicializa o Adapter
        timeSlotAdapter = TimeSlotAdminAdapter(todosHorariosBase.toMutableList()) { slot, position ->
            // Listener de clique do Admin:
        }
        recyclerView.adapter = timeSlotAdapter

        // 4. Carrega os dados da data inicial (agora a data correta da Intent)
        carregarRestricoesDoFirebase()

        // 5. Lógica de Salvar
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_save_changes).setOnClickListener {
            salvarAlteracoes()
        }
    }

    // --- MÉTODOS DE DADOS E UI ---

    private fun updateDateDisplay() {
        val dateFormat = SimpleDateFormat("EEE, dd 'de' MMM", Locale("pt", "BR"))
        findViewById<TextView>(R.id.tv_date_selector).text = dateFormat.format(selectedDate.time)
    }

    private fun criarHorariosBase(): List<TimeSlotAdmin> {
        val slots = mutableListOf<TimeSlotAdmin>()

        for (h in HORA_INICIO_FUNCIONAMENTO until HORA_FIM_FUNCIONAMENTO) {
            val start = String.format("%02d:00", h)
            val end = String.format("%02d:00", h + 1)

            slots.add(TimeSlotAdmin(id = "${h}", startHour = start, endHour = end))
        }

        return slots
    }

    private fun mostrarDatePicker() {
        val ano = selectedDate.get(Calendar.YEAR)
        val mes = selectedDate.get(Calendar.MONTH)
        val dia = selectedDate.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                selectedDate.set(year, monthOfYear, dayOfMonth)
                updateDateDisplay()
                carregarRestricoesDoFirebase()
            },
            ano, mes, dia
        ).show()
    }

    // --- MÉTODOS FIREBASE ---

    /**
     * Busca no Firebase as reservas ativas e as restrições existentes para a cabine e data selecionada.
     */
    private fun carregarRestricoesDoFirebase() {
        val dataStr = dateFormatDatabase.format(selectedDate.time)
        val todosHorarios = criarHorariosBase().toMutableList()

        // --- 1. BUSCA RESERVAS ATIVAS DE USUÁRIOS ---
        db.collection("reservas")
            .whereEqualTo("cabineNumero", selectedCabineId)
            .whereEqualTo("dataReserva", dataStr)
            .whereEqualTo("status", StatusReserva.ATIVA.name)
            .get()
            .addOnSuccessListener { resultReservas ->
                val reservasAtivas = resultReservas.toObjects(Reserva::class.java)

                // Marca os slots com reserva de usuário
                for (reserva in reservasAtivas) {
                    try {
                        val horaInicioReserva = reserva.horaInicio?.split(":")?.get(0)?.toInt() ?: continue

                        // Encontra o slot de tempo correspondente
                        val slot = todosHorarios.find { it.id.toInt() == horaInicioReserva }

                        if (slot != null) {
                            // 🎯 Marca como reservado e indisponível para clique do Admin
                            slot.isReservadoPeloUsuario = true
                            slot.isIndisponivel = true
                        }
                    } catch (e: Exception) { /* Ignorar erros de formato */ }
                }

                // --- 2. BUSCA RESTRIÇÕES DE ADMIN ---
                db.collection("restricoes")
                    .whereEqualTo("cabineId", selectedCabineId)
                    .whereEqualTo("dataRestricao", dataStr)
                    .get()
                    .addOnSuccessListener { resultRestricoes ->
                        val restricoesExistentes = resultRestricoes.toObjects(Restricao::class.java)

                        // Aplica as restrições de Admin (bloqueios)
                        for (restricao in restricoesExistentes) {
                            val horaSlot = restricao.horaInicio?.split(":")?.get(0)?.toInt() ?: continue
                            val slot = todosHorarios.find { it.id.toInt() == horaSlot }

                            if (slot != null && !slot.isReservadoPeloUsuario) {
                                // Só marca como bloqueado/indisponível SE já não estiver reservado pelo usuário
                                slot.isIndisponivel = true
                                slot.firestoreDocId = restricao.id
                            }
                        }

                        timeSlotAdapter.updateSlots(todosHorarios)

                    }.addOnFailureListener { e ->
                        Toast.makeText(this, "Erro ao carregar restrições: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                        timeSlotAdapter.updateSlots(todosHorarios)
                    }

            }.addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar reservas: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                timeSlotAdapter.updateSlots(todosHorarios)
            }
    }

    /**
     * Executa um batch de escrita para adicionar novas restrições e deletar antigas.
     * Slots com reservas de usuário são ignorados.
     */
    private fun salvarAlteracoes() {
        val dataStr = dateFormatDatabase.format(selectedDate.time)
        val batch = db.batch()

        // Itera sobre a lista ATUAL de slots do Adapter (a lista mais atualizada)
        for (slot in timeSlotAdapter.slots) {

            // 🎯 Ignora slots que já estão reservados por um usuário.
            if (slot.isReservadoPeloUsuario) continue

            // 1. Determina a referência do documento
            val docRef = if (slot.firestoreDocId != null) {
                db.collection("restricoes").document(slot.firestoreDocId!!)
            } else {
                db.collection("restricoes").document()
            }

            if (slot.isIndisponivel) {
                // Usuário marcou o slot como indisponível/bloqueado
                if (slot.firestoreDocId == null) {
                    // ** AÇÃO 1: Adicionar NOVA restrição **
                    val generatedId = docRef.id
                    val novaRestricao = Restricao(
                        id = generatedId,
                        cabineId = selectedCabineId,
                        dataRestricao = dataStr,
                        horaInicio = slot.startHour,
                        horaFim = slot.endHour
                    )
                    batch.set(docRef, novaRestricao)
                }
            } else {
                // Usuário desmarcou o slot (está disponível)
                if (slot.firestoreDocId != null) {
                    // ** AÇÃO 2: REMOVER restrição existente **
                    batch.delete(docRef)
                }
            }
        }

        executarBatch(batch)
    }

    private fun executarBatch(batch: WriteBatch) {
        batch.commit()
            .addOnSuccessListener {
                Toast.makeText(this, "Alterações salvas com sucesso!", Toast.LENGTH_LONG).show()
                carregarRestricoesDoFirebase()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao salvar alterações: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }
}