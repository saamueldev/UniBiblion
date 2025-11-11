// CabineAdminEditActivity.kt

package com.example.unibiblion

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CabineAdminEditActivity : AppCompatActivity() {

    private lateinit var selectedCabineId: String
    private lateinit var selectedDate: Calendar
    private lateinit var timeSlotAdapter: TimeSlotAdminAdapter
    private val currentRestrictedSlots = mutableListOf<TimeSlotAdmin>() // Slots modificados pelo admin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cabine_admin_edit)

        // 1. Receber ID da Cabine
        selectedCabineId = intent.getStringExtra("CABINE_ID") ?: throw IllegalStateException("CABINE_ID obrigatório.")
        val cabineNome = "Cabine $selectedCabineId - Edição"

        findViewById<TextView>(R.id.tv_cabine_nome).text = cabineNome

        // 2. Configurar Data (começa em hoje)
        selectedDate = Calendar.getInstance()
        updateDateDisplay()

        // 3. Configurar RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recycler_horarios_admin)
        val todosHorarios = criarHorariosBase() // Base de 09:00 às 18:00

        // Inicializa o Adapter
        timeSlotAdapter = TimeSlotAdminAdapter(todosHorarios.toMutableList()) { slot, position ->
            // Lógica de manipulação de slot: Adiciona/Remove da lista de mudanças
            if (slot.isIndisponivel) {
                if (slot !in currentRestrictedSlots) currentRestrictedSlots.add(slot)
            } else {
                currentRestrictedSlots.remove(slot)
            }
        }
        recyclerView.adapter = timeSlotAdapter

        // 4. Lógica de Salvar
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_save_changes).setOnClickListener {
            salvarAlteracoes()
        }
    }

    private fun updateDateDisplay() {
        val dateFormat = SimpleDateFormat("EEE, dd 'de' MMM", Locale("pt", "BR"))
        findViewById<TextView>(R.id.tv_date_selector).text = dateFormat.format(selectedDate.time)
    }

    private fun criarHorariosBase(): List<TimeSlotAdmin> {
        val slots = mutableListOf<TimeSlotAdmin>()
        // Simulação de horário comercial 09:00 às 18:00, em blocos de 1 hora
        for (h in 9..17) {
            val start = String.format("%02d:00", h)
            val end = String.format("%02d:00", h + 1)
            slots.add(TimeSlotAdmin("${h}", start, end))
        }
        // NOTE: Em uma app real, você checaria aqui se já existe alguma restrição para esta cabine/data
        return slots
    }

    private fun salvarAlteracoes() {
        // Envia a lista 'currentRestrictedSlots' para o backend ou para o banco local
        if (currentRestrictedSlots.isEmpty()) {
            Toast.makeText(this, "Nenhuma alteração de indisponibilidade para a cabine $selectedCabineId na data ${updateDateDisplay()}.", Toast.LENGTH_LONG).show()
        } else {
            val count = currentRestrictedSlots.size
            Toast.makeText(this, "Salvo: $count slots de tempo marcados como indisponíveis para a Cabine $selectedCabineId.", Toast.LENGTH_LONG).show()
        }
        // Retorna para a tela principal
        finish()
    }
}