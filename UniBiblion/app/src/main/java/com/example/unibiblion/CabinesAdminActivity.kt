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
import java.text.SimpleDateFormat
import java.util.Locale

// ESTA É A VERSÃO DO ADMINISTRADOR
class CabinesAdminActivity : AppCompatActivity() {

    private lateinit var dataHoraSelecionada: Calendar

    private lateinit var dateSelectorTextView: TextView

    private lateinit var listaCabines: MutableList<Cabine>
    private lateinit var cabinesAdapter: CabinesAdapter

    // NOTA: O botão btnReservarCabine e btnMinhasReservas não são necessários para o Admin,
    // mas vamos mantê-los para evitar erros de referência no layout se você não for removê-los.
    // Vamos apenas escondê-los.
    private lateinit var btnReservarCabine: Button
    private lateinit var btnMinhasReservas: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cabines_individuais)

        // 1. Configurar Título (Opcional, para indicar que é a tela do Admin)
        supportActionBar?.title = "Mapa de Cabines - ADMIN"

        dataHoraSelecionada = Calendar.getInstance()

        // 2. OBTÉM AS REFERÊNCIAS
        dateSelectorTextView = findViewById(R.id.date_selector)
        btnReservarCabine = findViewById(R.id.btn_reservar_cabine)
        btnMinhasReservas = findViewById(R.id.btn_minhas_reservas)
        val gridCabines: GridView = findViewById(R.id.grid_cabines)

        // ** AÇÃO 1: ESCONDER BOTÕES DE USUÁRIO **
        btnReservarCabine.visibility = View.GONE
        btnMinhasReservas.visibility = View.GONE
        // Nota: Você pode esconder o seletor de data/hora também se ele não for relevante aqui
        // dateSelectorTextView.visibility = View.GONE

        // 3. Inicializa dados e Grid
        listaCabines = criarDadosDeExemplo()

        // 4. Configura Adapters
        // Usamos o mesmo Adapter, mas no modo Admin, ele deve apenas exibir as cabines
        cabinesAdapter = CabinesAdapter(this, listaCabines)
        gridCabines.adapter = cabinesAdapter

        // 5. Configurar o clique (Diferente da versão do usuário)
        gridCabines.setOnItemClickListener { parent, view, position, id ->

            val cabineClicada = listaCabines[position]

            // NOVO FLUXO: Ao clicar em QUALQUER cabine (livre ou ocupada),
            // o Admin é levado para a tela de edição.
            navigateToAdminEdit(cabineClicada.numero)
        }

        // 6. Atualiza o texto do seletor (necessário para inicialização)
        atualizarTextoSeletorData(dataHoraSelecionada)

        // Opcional: Desabilitar o clique no seletor de data para o Admin, já que
        // ele não está reservando
        dateSelectorTextView.setOnClickListener {
            Toast.makeText(this, "Seletor desabilitado no modo Admin.", Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * Função para navegar para a tela de Edição do Administrador.
     */
    private fun navigateToAdminEdit(cabineId: String) {
        val intent = Intent(this, CabineAdminEditActivity::class.java).apply {
            putExtra("CABINE_ID", cabineId)
        }
        startActivity(intent)
    }

    // Funções mantidas do seu código original para garantir a inicialização correta:

    private fun criarDadosDeExemplo(): MutableList<Cabine> {
        val cabines = mutableListOf<Cabine>()
        for (i in 1..25) {
            val numeroStr = String.format("%02d", i)
            val estado = if (i % 3 == 0) Cabine.ESTADO_OCUPADO else Cabine.ESTADO_LIVRE
            cabines.add(Cabine(numeroStr, estado))
        }
        return cabines
    }

    private fun atualizarTextoSeletorData(calendar: Calendar) {
        val dateFormat = SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", Locale("pt", "BR"))
        val dataFormatada = dateFormat.format(calendar.time)

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val horaFormatada = timeFormat.format(calendar.time)

        val textoFinal = "$dataFormatada às $horaFormatada"
        dateSelectorTextView.text = textoFinal
    }

    // As funções mostrarDatePicker, mostrarTimePicker, atualizarVisibilidadeBotaoReservar
    // e reservarCabineSelecionada não são mais necessárias nesta Activity,
    // mas não causarão problemas se existirem, desde que não sejam chamadas.
    // Para um código mais limpo, você pode removê-las se não for usá-las.

    // ... (Seu código original continha estas, remova-as se quiser limpar)
    private fun mostrarDatePicker() { /* ... */ }
    private fun mostrarTimePicker() { /* ... */ }
    private fun atualizarVisibilidadeBotaoReservar() { /* ... */ }
    private fun reservarCabineSelecionada() { /* ... */ }
}