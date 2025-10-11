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

class CabinesIndividuaisActivity : AppCompatActivity() {

    private lateinit var dataHoraSelecionada: Calendar

    private lateinit var dateSelectorTextView: TextView

    private lateinit var btnReservarCabine: Button

    private lateinit var btnMinhasReservas: Button

    private lateinit var listaCabines: MutableList<Cabine>
    private lateinit var cabinesAdapter: CabinesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cabines_individuais)

        dataHoraSelecionada = Calendar.getInstance()

        // 1. OBTÉM AS REFERÊNCIAS
        dateSelectorTextView = findViewById(R.id.date_selector)
        // ** MOVEMOS ESTA LINHA PARA CÁ **
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

        // ** LISTENER DO BOTÃO RESERVAR (AGORA PODE SER CHAMADO) **
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
    }


    // Função para criar dados de teste (sem alterações)
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
            btnReservarCabine.visibility = View.VISIBLE // Mostra o botão
        } else {
            btnReservarCabine.visibility = View.GONE    // Esconde o botão
        }
    }

    private fun mostrarDatePicker() {
        val ano = dataHoraSelecionada.get(Calendar.YEAR)
        val mes = dataHoraSelecionada.get(Calendar.MONTH)
        val dia = dataHoraSelecionada.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                // Quando a data é selecionada, atualiza a variável e chama o seletor de hora
                dataHoraSelecionada.set(year, monthOfYear, dayOfMonth)
                mostrarTimePicker() // <--- Chama o seletor de hora
            },
            ano, mes, dia
        )
        datePickerDialog.show()
    }

    private fun mostrarTimePicker() {
        val hora = dataHoraSelecionada.get(Calendar.HOUR_OF_DAY)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, _ -> // O terceiro argumento (minute) será ignorado!
                // Atualiza a hora para a hora cheia selecionada e o minuto para 00
                dataHoraSelecionada.set(Calendar.HOUR_OF_DAY, hourOfDay)
                dataHoraSelecionada.set(Calendar.MINUTE, 0) // <--- FORÇA MINUTO ZERO (HORA CHEIA)

                // Atualiza o texto exibido na tela
                atualizarTextoSeletorData(dataHoraSelecionada)

                // Opcional: Recarrega a grade com o novo horário (ficará para a lógica futura)
                // recarregarGradeCabines()
            },
            hora,
            0, // Minuto inicial (sempre 0)
            true // true = 24h format, false = AM/PM format
        )
        timePickerDialog.show()
    }

    private fun atualizarTextoSeletorData(calendar: Calendar) {
        // Formato para a data: Ex: Segunda-feira, 14 de Outubro de 2025
        val dateFormat = java.text.SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", java.util.Locale("pt", "BR"))
        val dataFormatada = dateFormat.format(calendar.time)

        // Formato para a hora: Ex: 10:00
        val timeFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        val horaFormatada = timeFormat.format(calendar.time)

        // Combina e define o texto
        val textoFinal = "$dataFormatada às $horaFormatada"
        dateSelectorTextView.text = textoFinal
    }

    private fun reservarCabineSelecionada() {
        val selectedPosition = cabinesAdapter.getSelectedPosition()

        // Verifica se há uma cabine selecionada
        if (selectedPosition != -1) {
            val cabineSelecionada = listaCabines[selectedPosition]

            // Inicia a nova Activity de Confirmação, passando os dados
            val intent = Intent(this, CabineSelecaoPeriodoActivity::class.java).apply {
                putExtra("EXTRA_NUMERO_CABINE", cabineSelecionada.numero)
                // Usamos o tempo em milissegundos para passar a data/hora
                putExtra("EXTRA_DATA_HORA", dataHoraSelecionada.timeInMillis)
            }
            startActivity(intent)

            // Opcional: Aqui você faria uma chamada API para reservar.
            // Para simulação, vamos apenas ir para a tela.

        } else {
            // Caso de segurança (não deve acontecer, pois o botão estaria invisível)
            Toast.makeText(this, "Selecione uma cabine antes de reservar.", Toast.LENGTH_SHORT).show()
        }
    }
}