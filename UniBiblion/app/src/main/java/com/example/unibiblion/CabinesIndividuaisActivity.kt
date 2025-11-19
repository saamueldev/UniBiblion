package com.example.unibiblion

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log // ⬅️ IMPORTANTE: Adicionado para o debug
import android.view.View
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import java.util.Calendar
import java.util.Locale
import java.text.SimpleDateFormat

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

    private val dateFormatDatabase = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val TAG = "CabinesActivityDebug" // ⬅️ TAG para filtro do Logcat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cabines_individuais)

        Log.d(TAG, "Activity onCreate iniciado.")

        dataHoraSelecionada = Calendar.getInstance().apply {
            val currentMinute = get(Calendar.MINUTE)

            // Zera minutos, segundos e milissegundos
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // Se os minutos atuais forem maiores que zero, o slot da hora atual já começou.
            if (currentMinute > 0) {
                // Avança para a próxima hora completa (arredonda para cima).
                // Ex: 18:42 -> adiciona 1h -> 19:00.
                add(Calendar.HOUR_OF_DAY, 1)
            }
        }

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
        Log.d(TAG, "Activity onDestroy. Removendo listener do Firebase.")
        cabinesListener?.remove()
    }

    override fun onResume() {
        super.onResume()
        bottomNavigation.menu.findItem(R.id.nav_livraria).isChecked = true
        Log.d(TAG, "Activity onResume iniciado.")

        // 🎯 Força a atualização do status ao retornar à tela (ponto de re-renderização)
        if (::listaCabines.isInitialized && ::cabinesAdapter.isInitialized) {
            carregarStatusOcupacao()
        } else {
            // Se ainda não inicializou, tenta carregar do Firebase novamente.
            carregarCabinesDoFirebase()
        }
    }

    // ==========================================================
    // LÓGICA DE BUSCA E ATUALIZAÇÃO DO FIREBASE (CORRIGIDA E DEBUGADA)
    // ==========================================================

    private fun carregarCabinesDoFirebase() {

        Log.d(TAG, "Iniciando carregarCabinesDoFirebase().")
        cabinesListener?.remove()

        cabinesListener = db.collection("cabines")
            .orderBy("numero", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->

                if (e != null) {
                    Log.e(TAG, "Erro ao observar cabines: ${e.localizedMessage}")
                    Toast.makeText(this, "Erro ao observar cabines: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    if (!::listaCabines.isInitialized) {
                        listaCabines = mutableListOf()
                        configurarGridView()
                    }
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val novaLista = snapshots.toObjects(Cabine::class.java).toMutableList()
                    Log.d(TAG, "Snapshot do Firebase recebido. Tamanho: ${novaLista.size}")

                    if (!::cabinesAdapter.isInitialized) {
                        listaCabines = novaLista
                        configurarGridView()
                        Log.d(TAG, "Adapter inicializado pela primeira vez. Tamanho da lista: ${listaCabines.size}")
                    } else {
                        // ➡️ CORREÇÃO DE FLUXO APLICADA
                        // Atualiza a lista principal (listaCabines) sem zerar o Adapter
                        listaCabines.clear()
                        listaCabines.addAll(novaLista)
                        Log.d(TAG, "Lista principal atualizada com ${listaCabines.size} cabines do snapshot.")
                    }

                    // 3. Chama a lógica de ocupação.
                    carregarStatusOcupacao()
                }
            }
    }

    private fun carregarStatusOcupacao() {
        Log.d(TAG, "Iniciando carregarStatusOcupacao().")

        if (!::listaCabines.isInitialized || listaCabines.isEmpty() || !::cabinesAdapter.isInitialized) {
            Log.w(TAG, "carregarStatusOcupacao abortado: Lista (${::listaCabines.isInitialized}) ou Adapter (${::cabinesAdapter.isInitialized}) não inicializados/vazios.")
            return
        }

        val dataReservaStr = dateFormatDatabase.format(dataHoraSelecionada.time)

        // Hora que o usuário SELECIONOU no TextView (ex: 19)
        val horaInicioSelecionada = dataHoraSelecionada.get(Calendar.HOUR_OF_DAY)

        Log.d(TAG, "Checando ocupação para data/hora: $dataReservaStr às $horaInicioSelecionada:00")

        // --- 🎯 NOVA LÓGICA DE HORÁRIO DE FUNCIONAMENTO ---
        val horaLimiteInicio = 8 // 08:00
        val horaLimiteFim = 22 // 22:00 (O slot de 22:00 a 23:00 é o último que deve ser bloqueado)

        if (horaInicioSelecionada < horaLimiteInicio || horaInicioSelecionada >= horaLimiteFim) {
            Log.w(TAG, "Horário de reserva ($horaInicioSelecionada:00) fora do funcionamento (08:00-22:00). Bloqueando todas as cabines.")

            // Marca todas as cabines como OCUPADAS e notifica o adapter
            listaCabines.forEach { it.estado = Cabine.ESTADO_OCUPADO }
            cabinesAdapter.updateCabines(listaCabines)
            atualizarVisibilidadeBotaoReservar()
            return // Encerra a função, não precisa checar o Firebase
        }
        // --------------------------------------------------

        // 1. Resetamos o estado de TODAS as cabines
        listaCabines.forEach { it.estado = Cabine.ESTADO_LIVRE }
        Log.d(TAG, "Status de todas as ${listaCabines.size} cabines resetado para LIVRE.")

        db.collection("reservas")
            .whereEqualTo("dataReserva", dataReservaStr)
            .whereEqualTo("status", StatusReserva.ATIVA.name)
            .get()
            .addOnSuccessListener { result ->
                val reservasDoDia = result.toObjects(Reserva::class.java)
                Log.i(TAG, "Reservas ativas encontradas para o dia: ${reservasDoDia.size}")

                var cabinesOcupadas = 0

                // 2. Itera sobre as reservas e MARCA as cabines OCUPADAS
                for (reserva in reservasDoDia) {
                    try {
                        val horaInicioReserva = reserva.horaInicio?.split(":")?.get(0)?.toInt() ?: continue
                        val horaFimReserva = reserva.horaFim?.split(":")?.get(0)?.toInt() ?: continue

                        Log.d(TAG, "   -> Comp: Slot Selecionado ($horaInicioSelecionada) vs Reserva (${horaInicioReserva} a ${horaFimReserva}) na cabine ${reserva.cabineNumero}")

                        if (horaInicioSelecionada >= horaInicioReserva && horaInicioSelecionada < horaFimReserva) {

                            val cabineOcupada = listaCabines.find { it.numero == reserva.cabineNumero }

                            cabineOcupada?.estado = Cabine.ESTADO_OCUPADO
                            if (cabineOcupada != null) {
                                cabinesOcupadas++
                                Log.i(TAG, "   -> Cabine ${reserva.cabineNumero} marcada como OCUPADA.")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Erro no parse da hora da reserva: ${e.localizedMessage}. Reserva: ${reserva.horaInicio} - ${reserva.horaFim}")
                    }
                }
                Log.i(TAG, "Total de cabines marcadas como OCUPADAS: $cabinesOcupadas")

                // 3. ATUALIZAÇÃO DO GRIDVIEW
                cabinesAdapter.updateCabines(listaCabines)
                Log.d(TAG, "Adapter notificado (notifyDataSetChanged).")

                atualizarVisibilidadeBotaoReservar()

            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Falha ao carregar reservas: ${e.localizedMessage}")
                Toast.makeText(this, "Erro ao carregar reservas: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                cabinesAdapter.updateCabines(listaCabines)
            }
    }


    private fun configurarGridView() {
        val gridCabines: GridView = findViewById(R.id.grid_cabines)
        Log.d(TAG, "configurarGridView() iniciado.")

        if (!::cabinesAdapter.isInitialized) {
            cabinesAdapter = CabinesAdapter(this, listaCabines.toList())
            gridCabines.adapter = cabinesAdapter
            Log.d(TAG, "Adapter criado e setado no GridView.")

            gridCabines.setOnItemClickListener { parent, view, position, id ->

                val cabineClicada = listaCabines[position]
                Log.d(TAG, "Cabine ${cabineClicada.numero} clicada. Estado: ${cabineClicada.estado}")

                if (cabineClicada.estado == Cabine.ESTADO_OCUPADO) {
                    if (cabinesAdapter.getSelectedPosition() == position) {
                        cabinesAdapter.selectSingleCabine(position)
                    } else {
                        Toast.makeText(this, "A Cabine ${cabineClicada.numero} está ocupada no horário selecionado e não pode ser reservada.", Toast.LENGTH_SHORT).show()
                    }
                    atualizarVisibilidadeBotaoReservar()
                    return@setOnItemClickListener
                }

                cabinesAdapter.selectSingleCabine(position)
                atualizarVisibilidadeBotaoReservar()
            }
        }
    }

    // ==========================================================
    // FUNÇÕES DE UI E NAVEGAÇÃO
    // ==========================================================

    private fun atualizarVisibilidadeBotaoReservar() {
        if (!::listaCabines.isInitialized || !::cabinesAdapter.isInitialized) return

        val selectedPosition = cabinesAdapter.getSelectedPosition()

        if (selectedPosition != -1) {
            val cabineSelecionada = listaCabines[selectedPosition]

            if (cabineSelecionada.estado == Cabine.ESTADO_OCUPADO) {
                cabinesAdapter.selectSingleCabine(selectedPosition)
                btnReservarCabine.visibility = View.GONE
                return
            }

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
        Log.d(TAG, "Data/hora atualizada: $textoFinal. Chamando carregarStatusOcupacao.")

        if (::listaCabines.isInitialized) {
            carregarStatusOcupacao()
        }
    }

    private fun reservarCabineSelecionada() {
        val selectedPosition = cabinesAdapter.getSelectedPosition()

        if (selectedPosition != -1) {
            val cabineSelecionada = listaCabines[selectedPosition]

            val numeroCabine = cabineSelecionada.numero ?: run {
                Toast.makeText(this, "Erro: Número da cabine não encontrado.", Toast.LENGTH_SHORT).show()
                return
            }

            if (cabineSelecionada.estado != Cabine.ESTADO_LIVRE) {
                Toast.makeText(this, "A Cabine $numeroCabine não está disponível para reserva no horário selecionado.", Toast.LENGTH_SHORT).show()
                cabinesAdapter.selectSingleCabine(selectedPosition)
                atualizarVisibilidadeBotaoReservar()
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