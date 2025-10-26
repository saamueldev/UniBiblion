package com.example.unibiblion

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast // Import necessário para a mensagem de erro
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import java.util.Calendar
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView // 1. IMPORT NECESSÁRIO
import java.text.SimpleDateFormat
import java.util.Locale

class CabinesIndividuaisActivity : AppCompatActivity() {

    private lateinit var dataHoraSelecionada: Calendar
    private lateinit var dateSelectorTextView: TextView
    private lateinit var btnReservarCabine: Button
    private lateinit var btnMinhasReservas: Button
    private lateinit var listaCabines: MutableList<Cabine>
    private lateinit var cabinesAdapter: CabinesAdapter

    // 2. DECLARAÇÃO: Variável de classe para a Bottom Navigation
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cabines_individuais)

        dataHoraSelecionada = Calendar.getInstance()

        // 3. INICIALIZAÇÃO DA BOTTOM NAVIGATION
        bottomNavigation = findViewById(R.id.bottom_navigation)

        // 1. OBTÉM AS REFERÊNCIAS
        dateSelectorTextView = findViewById(R.id.date_selector)
        btnReservarCabine = findViewById(R.id.btn_reservar_cabine)
        btnMinhasReservas = findViewById(R.id.btn_minhas_reservas)

        // Inicializa dados e Grid
        listaCabines = criarDadosDeExemplo()
        val gridCabines: GridView = findViewById(R.id.grid_cabines)

        // 2. CONFIGURA LISTENERS/ADAPTERS USANDO AS REFERÊNCIAS

        // Listener do Seletor de Data
        atualizarTextoSeletorData(dataHoraSelecionada)
        dateSelectorTextView.setOnClickListener {
            mostrarDatePicker()
        }

        // LISTENER DO BOTÃO RESERVAR
        btnReservarCabine.setOnClickListener {
            reservarCabineSelecionada()
        }

        btnMinhasReservas.setOnClickListener {
            // Crie e inicie o Intent para a MinhasReservasActivity
            val intent = Intent(this, MinhasReservasActivity::class.java)
            startActivity(intent)
        }

        // Adapter e GridView
        cabinesAdapter = CabinesAdapter(this, listaCabines)
        gridCabines.adapter = cabinesAdapter

        // 4. Configurar o clique do usuário com a restrição
        gridCabines.setOnItemClickListener { parent, view, position, id ->
            val cabineClicada = listaCabines[position]

            if (cabineClicada.estado == Cabine.ESTADO_LIVRE) {
                cabinesAdapter.selectSingleCabine(position)
                atualizarVisibilidadeBotaoReservar()
            } else {
                Toast.makeText(this, "A Cabine ${cabineClicada.numero} está ocupada.", Toast.LENGTH_SHORT).show()
            }
        }

        // 5. CONFIGURAÇÃO DA BOTTOM NAVIGATION LISTENER (NOVO)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_livraria -> {
                    // Item: ic_book (Livraria). Navega para a Home da Livraria.
                    val intent = Intent(this, Tela_Central_Livraria::class.java)
                    // Flags para limpar a pilha e evitar múltiplas instâncias da Home
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }

                R.id.nav_noticias -> {
                    // Item: ic_newspaper (Notícias)
                    val intent = Intent(this, NoticiasActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_chatbot -> {
                    // Item: ic_chat (Chatbot)
                    val intent = Intent(this, Tela_Chat_Bot::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_perfil -> {
                    // Item: ic_profile (Perfil)
                    val intent = Intent(this, Tela_De_Perfil::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    // 6. SOLUÇÃO DE ESTADO: Garante que o ícone da Livraria esteja selecionado ao retomar a tela
    override fun onResume() {
        super.onResume()
        // Força a seleção do ícone Livraria (o fluxo desta tela)
        bottomNavigation.menu.findItem(R.id.nav_livraria).isChecked = true
    }

    // Funções mantidas do seu código original (sem alterações)
    private fun criarDadosDeExemplo(): MutableList<Cabine> {
        val cabines = mutableListOf<Cabine>()
        for (i in 1..25) {
            val numeroStr = String.format("%02d", i)
            val estado = if (i % 3 == 0) Cabine.ESTADO_OCUPADO else Cabine.ESTADO_LIVRE
            cabines.add(Cabine(numeroStr, estado))
        }
        return cabines
    }

    private fun atualizarVisibilidadeBotaoReservar() {
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

            val intent = Intent(this, CabineSelecaoPeriodoActivity::class.java).apply {
                putExtra("EXTRA_NUMERO_CABINE", cabineSelecionada.numero)
                putExtra("EXTRA_DATA_HORA", dataHoraSelecionada.timeInMillis)
            }
            startActivity(intent)
        } else {
            Toast.makeText(this, "Selecione uma cabine antes de reservar.", Toast.LENGTH_SHORT).show()
        }
    }
}
