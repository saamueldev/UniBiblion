package com.example.unibiblion

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import java.util.Calendar
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ListenerRegistration
import java.text.SimpleDateFormat
import java.util.Locale

class CabinesIndividuaisActivity : AppCompatActivity() {

    private lateinit var dataHoraSelecionada: Calendar
    private lateinit var dateSelectorTextView: TextView
    private lateinit var btnReservarCabine: Button
    private lateinit var btnMinhasReservas: Button
    private lateinit var listaCabines: MutableList<Cabine>
    private lateinit var cabinesAdapter: CabinesAdapter

    private lateinit var db: FirebaseFirestore
    private var cabinesListener: ListenerRegistration? = null

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cabines_individuais)

        dataHoraSelecionada = Calendar.getInstance()

        db = FirebaseFirestore.getInstance()
        bottomNavigation = findViewById(R.id.bottom_navigation)

        dateSelectorTextView = findViewById(R.id.date_selector)
        btnReservarCabine = findViewById(R.id.btn_reservar_cabine)
        btnMinhasReservas = findViewById(R.id.btn_minhas_reservas)

        carregarCabinesDoFirebase()

        atualizarTextoSeletorData(dataHoraSelecionada)
        dateSelectorTextView.setOnClickListener {
            mostrarDatePicker()
        }

        btnReservarCabine.setOnClickListener {
            reservarCabineSelecionada()
        }

        btnMinhasReservas.setOnClickListener {
            val intent = Intent(this, MinhasReservasActivity::class.java)
            startActivity(intent)
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_livraria -> {
                    val intent = Intent(this, Tela_Central_Livraria::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_noticias -> {
                    val intent = Intent(this, NoticiasActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_chatbot -> {
                    val intent = Intent(this, Tela_Chat_Bot::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_perfil -> {
                    val intent = Intent(this, Tela_De_Perfil::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cabinesListener?.remove()
    }

    override fun onResume() {
        super.onResume()
        bottomNavigation.menu.findItem(R.id.nav_livraria).isChecked = true
    }

    // ==========================================================
    // LÓGICA DE BUSCA EM TEMPO REAL DO FIREBASE
    // ==========================================================

    private fun carregarCabinesDoFirebase() {

        cabinesListener?.remove()

        cabinesListener = db.collection("cabines")
            .orderBy("numero", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->

                if (e != null) {
                    Toast.makeText(this, "Erro ao observar cabines: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    if (!::listaCabines.isInitialized) {
                        listaCabines = mutableListOf()
                        configurarGridView()
                    }
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    listaCabines = snapshots.toObjects(Cabine::class.java).toMutableList()
                    configurarGridView()
                }
            }
    }

    /**
     * Configura o GridView e o Adapter.
     */
    private fun configurarGridView() {
        val gridCabines: GridView = findViewById(R.id.grid_cabines)

        if (!::cabinesAdapter.isInitialized) {
            cabinesAdapter = CabinesAdapter(this, listaCabines)
            gridCabines.adapter = cabinesAdapter

            // 🎯 CORREÇÃO: VERIFICAÇÃO DE ESTADO NO CLIQUE
            gridCabines.setOnItemClickListener { parent, view, position, id ->

                val cabineClicada = listaCabines[position] // Obtém o objeto Cabine

                if (cabineClicada.estado == Cabine.ESTADO_OCUPADO) {

                    // 1. Se a cabine OCUPADA for a que estava selecionada, desmarcamos.
                    if (cabinesAdapter.getSelectedPosition() == position) {
                        cabinesAdapter.selectSingleCabine(position) // Desmarca a seleção
                    } else {
                        // 2. Se for uma cabine ocupada e não estava selecionada, bloqueamos a seleção
                        Toast.makeText(this, "A Cabine ${cabineClicada.numero} está ocupada e não pode ser selecionada.", Toast.LENGTH_SHORT).show()
                    }

                    atualizarVisibilidadeBotaoReservar()
                    return@setOnItemClickListener // Impede qualquer ação de seleção
                }

                // Se a cabine estiver LIVRE, permite o toggle normal de seleção/deseleção
                cabinesAdapter.selectSingleCabine(position)
                atualizarVisibilidadeBotaoReservar()
            }
        } else {
            // Atualiza a lista interna do Adapter (solução para tempo real)
            cabinesAdapter.updateCabines(listaCabines)
        }
    }

    // ==========================================================
    // FUNÇÕES DE UI E NAVEGAÇÃO
    // ==========================================================

    private fun atualizarVisibilidadeBotaoReservar() {
        if (!::listaCabines.isInitialized) return

        val algumaCabineSelecionada = cabinesAdapter.getSelectedPosition() != -1
        if (algumaCabineSelecionada) {
            btnReservarCabine.visibility = View.VISIBLE
        } else {
            btnReservarCabine.visibility = View.GONE
        }
    }

    private fun mostrarDatePicker() {
        val ano = dataHoraSelecionada.get(Calendar.YEAR)
        val mes = dataHoraSelecionada.get(Calendar.MONTH)
        val dia = dataHoraSelecionada.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                dataHoraSelecionada.set(year, monthOfYear, dayOfMonth)
                mostrarTimePicker()
            },
            ano, mes, dia
        )
        datePickerDialog.show()
    }

    private fun mostrarTimePicker() {
        val hora = dataHoraSelecionada.get(Calendar.HOUR_OF_DAY)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, _ ->
                dataHoraSelecionada.set(Calendar.HOUR_OF_DAY, hourOfDay)
                dataHoraSelecionada.set(Calendar.MINUTE, 0)

                atualizarTextoSeletorData(dataHoraSelecionada)
            },
            hora,
            0,
            true
        )
        timePickerDialog.show()
    }

    private fun atualizarTextoSeletorData(calendar: Calendar) {
        val dateFormat = SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", Locale("pt", "BR"))
        val dataFormatada = dateFormat.format(calendar.time)

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val horaFormatada = timeFormat.format(calendar.time)

        val textoFinal = "$dataFormatada às $horaFormatada"
        dateSelectorTextView.text = textoFinal
    }

    private fun reservarCabineSelecionada() {
        val selectedPosition = cabinesAdapter.getSelectedPosition()

        if (selectedPosition != -1) {
            val cabineSelecionada = listaCabines[selectedPosition]

            val numeroCabine = cabineSelecionada.numero ?: run {
                Toast.makeText(this, "Erro: Número da cabine não encontrado.", Toast.LENGTH_SHORT).show()
                return
            }

            // Esta verificação já existia, mas é crucial: se, por algum motivo,
            // o clique foi permitido, o botão final ainda checa.
            if (cabineSelecionada.estado != Cabine.ESTADO_LIVRE) {
                Toast.makeText(this, "A Cabine $numeroCabine não está disponível para reserva.", Toast.LENGTH_SHORT).show()
                return
            }

            val intent = Intent(this, CabineSelecaoPeriodoActivity::class.java).apply {
                putExtra("EXTRA_NUMERO_CABINE", numeroCabine)
                putExtra("EXTRA_DATA_HORA", dataHoraSelecionada.timeInMillis)
            }
            startActivity(intent)
        } else {
            Toast.makeText(this, "Selecione uma cabine antes de reservar.", Toast.LENGTH_SHORT).show()
        }
    }
}