package com.example.unibiblion

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReservaSucessoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserva_sucesso)

        supportActionBar?.hide()

        val tvDetalhes: TextView = findViewById(R.id.tv_detalhes_reserva_sucesso)
        val btnVoltarHome: Button = findViewById(R.id.btn_voltar_home)

        // 1. Receber os dados FINAIS
        val numeroCabine = intent.getStringExtra("EXTRA_NUMERO_CABINE") ?: "Cabine Desconhecida"
        val dataMillis = intent.getLongExtra("EXTRA_DATA", 0L)
        val inicioMillis = intent.getLongExtra("EXTRA_INICIO_HORA", 0L)
        val fimMillis = intent.getLongExtra("EXTRA_FIM_HORA", 0L)

        // 2. Formatar os dados
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val calendarData = Calendar.getInstance().apply { timeInMillis = dataMillis }
        val calendarInicio = Calendar.getInstance().apply { timeInMillis = inicioMillis }
        val calendarFim = Calendar.getInstance().apply { timeInMillis = fimMillis }

        val dataStr = dateFormat.format(calendarData.time)
        val inicioStr = timeFormat.format(calendarInicio.time)
        val fimStr = timeFormat.format(calendarFim.time)

        // 3. Exibir a mensagem detalhada
        val mensagem = "A $numeroCabine foi reservada com sucesso para o dia $dataStr, no período das $inicioStr às $fimStr."
        tvDetalhes.text = mensagem

        // 4. Ação do Botão: Voltar
        btnVoltarHome.setOnClickListener {
            // Volta para a tela principal (CabinesIndividuaisActivity)
            finish()
        }
    }
}