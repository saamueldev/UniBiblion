package com.example.unibiblion

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

class CabinesAdminListActivity : AppCompatActivity() {

    private lateinit var dataHoraSelecionada: Calendar
    private lateinit var dateSelectorTextView: TextView
    private lateinit var listaCabines: MutableList<Cabine>
    private lateinit var cabinesAdapter: CabinesAdapter

    private lateinit var db: FirebaseFirestore
    private var cabinesListener: ListenerRegistration? = null

    private lateinit var bottomNavigation: BottomNavigationView

    private val dateFormatDatabase = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val TAG = "AdminCabinesDebug"

    // Assumindo que você tem uma constante para o fim do dia
    private val HORARIO_FIM_DIA = 22

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cabines_individuais)

        Log.d(TAG, "Activity onCreate iniciado.")

        // INICIALIZAÇÃO DA DATA/HORA
        dataHoraSelecionada = Calendar.getInstance().apply {
            val currentMinute = get(Calendar.MINUTE)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // Arredonda para a próxima hora completa
            if (currentMinute > 0) {
                add(Calendar.HOUR_OF_DAY, 1)
            }
        }

        db = FirebaseFirestore.getInstance()
        bottomNavigation = findViewById(R.id.bottom_navigation)

        dateSelectorTextView = findViewById(R.id.date_selector)

        // Esconder botões de usuário, caso o layout seja compartilhado
        findViewById<Button>(R.id.btn_reservar_cabine)?.visibility = View.GONE
        findViewById<Button>(R.id.btn_minhas_reservas)?.visibility = View.GONE

        // 1. Inicia o carregamento e configuração do Grid
        carregarCabinesDoFirebase()

        // 2. Configura o Seletor de Data
        atualizarTextoSeletorData(dataHoraSelecionada)
        dateSelectorTextView.setOnClickListener {
            mostrarDatePicker()
        }

        // 3. Configuração da Bottom Navigation
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

        if (::listaCabines.isInitialized && ::cabinesAdapter.isInitialized) {
            // Recarregar status de ocupação para a data/hora atual (útil se o Admin voltou da edição)
            carregarStatusOcupacao()
        } else {
            carregarCabinesDoFirebase()
        }
    }

    // ==========================================================
    // LÓGICA DE FIREBASE E ATUALIZAÇÃO
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
                    } else {
                        listaCabines.clear()
                        listaCabines.addAll(novaLista)
                    }

                    carregarStatusOcupacao()
                }
            }
    }

    private fun carregarStatusOcupacao() {
        Log.d(TAG, "Iniciando carregarStatusOcupacao() [MODO ADMIN].")

        if (!::listaCabines.isInitialized || listaCabines.isEmpty() || !::cabinesAdapter.isInitialized) {
            Log.w(TAG, "carregarStatusOcupacao abortado: Lista ou Adapter não inicializados/vazios.")
            return
        }

        // DATA E HORA SELECIONADAS PELO ADMIN
        val dataReservaStr = dateFormatDatabase.format(dataHoraSelecionada.time)
        val horaInicioSelecionada = dataHoraSelecionada.get(Calendar.HOUR_OF_DAY)

        // 1. Resetar o estado de todas as cabines
        listaCabines.forEach { it.estado = Cabine.ESTADO_LIVRE }

        // 2. Lógica para buscar RESERVAS e RESTRICÕES

        // Exemplo: Buscar todas as reservas ativas para a data selecionada
        db.collection("reservas")
            .whereEqualTo("dataReserva", dataReservaStr)
            .get()
            .addOnSuccessListener { resultReservas ->
                // Lógica de iteração sobre reservas e marcação de cabines ocupadas (ESTADO_OCUPADO)

                // 3. Após checar reservas, checar Restrições de Admin
                db.collection("restricoes")
                    .whereEqualTo("dataRestricao", dataReservaStr)
                    .get()
                    .addOnSuccessListener { resultRestricoes ->
                        // Lógica de iteração sobre restrições e marcação de cabines como restritas (ESTADO_OCUPADO)
                        // Neste modo, o ESTADO_OCUPADO pode ser usado para ambos.

                        // 4. ATUALIZAÇÃO FINAL
                        cabinesAdapter.notifyDataSetChanged()
                    }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Falha ao carregar status: ${e.localizedMessage}")
                Toast.makeText(this, "Erro ao carregar status: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                cabinesAdapter.notifyDataSetChanged()
            }
    }


    private fun configurarGridView() {
        val gridCabines: GridView = findViewById(R.id.grid_cabines)
        Log.d(TAG, "configurarGridView() iniciado.")

        if (!::cabinesAdapter.isInitialized) {
            cabinesAdapter = CabinesAdapter(this, listaCabines.toList())
            gridCabines.adapter = cabinesAdapter
            Log.d(TAG, "Adapter criado e setado no GridView.")

            // 🌟 CORREÇÃO CRÍTICA AQUI: ENVIAR A DATA SELECIONADA PARA A TELA DE EDIÇÃO
            gridCabines.setOnItemClickListener { parent, view, position, id ->

                val cabineClicada = listaCabines[position]
                Log.d(TAG, "Cabine ${cabineClicada.numero} clicada. Direcionando para edição de restrições.")

                val intent = Intent(this, CabineAdminEditActivity::class.java).apply {
                    putExtra("CABINE_ID", cabineClicada.numero.toString())
                    // 🎯 ADICIONADO: Envia a data e hora selecionadas
                    putExtra("EXTRA_DATA_SELECIONADA", dataHoraSelecionada.timeInMillis)
                }
                startActivity(intent)
            }
        }
    }

    // ==========================================================
    // FUNÇÕES DE SELEÇÃO DE DATA E HORA (COMPLETAS)
    // ==========================================================

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

                // Após a seleção completa, atualiza o texto e recarrega os dados
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
            carregarStatusOcupacao() // Recarrega o status com a nova data/hora
        }
    }
}