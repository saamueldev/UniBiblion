package com.example.unibiblion

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.Serializable
import java.util.Calendar

class Tela_Informacoes1 : AppCompatActivity() {

    private lateinit var editTextDate2: EditText
    private lateinit var editTextTime: EditText
    private lateinit var buttonConfirmar: Button
    private lateinit var bottomNavigationView: BottomNavigationView

    // Variável para guardar o objeto Livro inteiro
    private var livroSelecionado: Livro? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_informacoes1)

        // RECEBE O OBJETO LIVRO INTEIRO
        livroSelecionado = getSerializable(intent, "LIVRO_SELECIONADO", Livro::class.java)

        // Verificação de segurança
        if (livroSelecionado == null) {
            Toast.makeText(this, "Erro ao carregar dados do livro. Tente novamente.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        editTextDate2 = findViewById(R.id.editTextDate2)
        editTextTime = findViewById(R.id.editTextTime)
        buttonConfirmar = findViewById(R.id.buttonConfirmar)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        editTextDate2.addTextChangedListener(DateMask(editTextDate2))
        editTextTime.addTextChangedListener(TimeMask(editTextTime))

        buttonConfirmar.setOnClickListener {
            validarEContinuar()
        }

        bottomNavigationView.setOnItemSelectedListener { true }
    }

    private fun validarEContinuar() {
        val dataStr = editTextDate2.text.toString()
        val horaStr = editTextTime.text.toString()

        if (dataStr.length < 10 || horaStr.length < 5) {
            Toast.makeText(this, "Por favor, preencha a data e a hora completas.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // A validação de data e hora permanece a mesma
            val partesData = dataStr.split("/").map { it.toInt() }
            val diaInserido = partesData[0]
            val mesInserido = partesData[1]
            val anoInserido = partesData[2]

            val partesHora = horaStr.split(":").map { it.toInt() }
            val horaInserida = partesHora[0]
            val minutoInserido = partesHora[1]

            if (horaInserida !in 0..23 || minutoInserido !in 0..59 || mesInserido !in 1..12 || diaInserido !in 1..31) {
                Toast.makeText(this, "Formato de data ou hora inválido.", Toast.LENGTH_SHORT).show()
                return
            }

            val agora = Calendar.getInstance()
            val anoAtual = agora.get(Calendar.YEAR)
            val mesAtual = agora.get(Calendar.MONTH) + 1
            val diaAtual = agora.get(Calendar.DAY_OF_MONTH)

            val isHoje = anoInserido == anoAtual && mesInserido == mesAtual && diaInserido == diaAtual
            val isFuturo = anoInserido > anoAtual ||
                    (anoInserido == anoAtual && mesInserido > mesAtual) ||
                    (anoInserido == anoAtual && mesInserido == mesAtual && diaInserido > diaAtual)

            if (!isHoje && !isFuturo) {
                Toast.makeText(this, "A data de retirada não pode ser um dia anterior a hoje.", Toast.LENGTH_LONG).show()
                return
            }

            if (isHoje) {
                val horaAtual = agora.get(Calendar.HOUR_OF_DAY)
                val minutoAtual = agora.get(Calendar.MINUTE)
                if (horaInserida < horaAtual || (horaInserida == horaAtual && minutoInserido < minutoAtual)) {
                    Toast.makeText(this, "Horário inválido. Deve ser um horário válido e posterior ao atual.", Toast.LENGTH_LONG).show()
                    return
                }
            }

            // REPASSA O OBJETO LIVRO INTEIRO PARA A PRÓXIMA TELA
            val intent = Intent(this, Tela_Informacoes2::class.java).apply {
                putExtra("DATA_AGENDAMENTO", dataStr)
                putExtra("HORA_AGENDAMENTO", horaStr)
                putExtra("LIVRO_SELECIONADO", livroSelecionado)
            }
            startActivity(intent)

        } catch (e: Exception) {
            Toast.makeText(this, "Formato de data ou hora inválido.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun <T : Serializable?> getSerializable(intent: Intent, key: String, clazz: Class<T>): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(key, clazz)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(key) as? T
        }
    }
}
