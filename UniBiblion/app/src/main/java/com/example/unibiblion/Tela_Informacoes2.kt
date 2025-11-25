package com.example.unibiblion

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Tela_Informacoes2 : AppCompatActivity() {

    // Variável para guardar o livro que veio da tela anterior
    private var livroSelecionado: Livro? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_informacoes2)

        // 1. Encontre os componentes
        val capaImageView: ImageView = findViewById(R.id.CapaLivro)
        val tituloTextView: TextView = findViewById(R.id.NomeLivroLabel)
        val autorTextView: TextView = findViewById(R.id.NomeAutorLabel)
        val dataRetiradaTextView: TextView = findViewById(R.id.textView5)
        val horarioRetiradaTextView: TextView = findViewById(R.id.textView6)
        val dataDevolucaoTextView: TextView = findViewById(R.id.textView7)
        val buttonConfirmar: Button = findViewById(R.id.buttonConfirmarInf2)
        val buttonVoltar: Button = findViewById(R.id.buttonVoltarInf2)

        // 2. Receba todos os dados da Intent
        val dataAgendamento = intent.getStringExtra("DATA_AGENDAMENTO")
        val horaAgendamento = intent.getStringExtra("HORA_AGENDAMENTO")
        // RECEBE O OBJETO LIVRO INTEIRO
        livroSelecionado = getSerializable(intent, "LIVRO_SELECIONADO", Livro::class.java)

        // Verificação de segurança
        if (livroSelecionado == null) {
            Toast.makeText(this, "Erro: dados do livro não encontrados.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 3. Preencha os componentes com os dados
        tituloTextView.text = livroSelecionado?.titulo ?: "Título não disponível"
        autorTextView.text = "por ${livroSelecionado?.autor ?: "Autor não disponível"}"
        dataRetiradaTextView.text = dataAgendamento ?: "DD/MM/AAAA"
        horarioRetiradaTextView.text = horaAgendamento ?: "HH:MM"

        // 4. Calcule e exiba a data de devolução
        if (dataAgendamento != null) {
            try {
                val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val data = formato.parse(dataAgendamento)
                if (data != null) {
                    val calendar = Calendar.getInstance()
                    calendar.time = data
                    calendar.add(Calendar.MONTH, 1)
                    val dataDevolucaoStr = formato.format(calendar.time)
                    dataDevolucaoTextView.text = dataDevolucaoStr
                }
            } catch (e: Exception) {
                dataDevolucaoTextView.text = "DD/MM/AAAA"
            }
        }

        // 5. Carregue a imagem da capa
        if (!livroSelecionado?.capaUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(livroSelecionado?.capaUrl)
                .placeholder(R.drawable.sommervile)
                .error(R.drawable.sommervile)
                .into(capaImageView)
        } else {
            capaImageView.setImageResource(R.drawable.sommervile)
        }

        // 6. Lógica dos Botões (CORRIGIDA COM ATUALIZAÇÃO NO FIREBASE)
        buttonConfirmar.setOnClickListener {
            decrementarEstoqueEFinalizar()
        }

        buttonVoltar.setOnClickListener {
            voltarParaTelaDesejado()
        }
    }

    /**
     * Função que decrementa o estoque do livro no Firestore e, em caso de sucesso,
     * mostra o Toast e navega para a tela de detalhes.
     */
    private fun decrementarEstoqueEFinalizar() {
        val livroId = livroSelecionado?.id
        if (livroId.isNullOrEmpty()) {
            Toast.makeText(this, "Erro: ID do livro não encontrado.", Toast.LENGTH_SHORT).show()
            return
        }

        // Desabilita o botão para evitar cliques múltiplos durante a operação
        findViewById<Button>(R.id.buttonConfirmarInf2).isEnabled = false

        val db = Firebase.firestore
        val livroRef = db.collection("livros").document(livroId)

        // Usa FieldValue.increment(-1) para decrementar o valor no servidor de forma segura
        livroRef.update("qEstoque", FieldValue.increment(-1))
            .addOnSuccessListener {
                Log.d("Firestore", "Estoque do livro $livroId decrementado com sucesso.")
                // SUCESSO: Mostra o Toast e volta para a tela de detalhes
                Toast.makeText(this, "Aluguel agendado com sucesso!", Toast.LENGTH_LONG).show()

                // Atualiza o estoque no objeto local para consistência ao voltar
                livroSelecionado?.qEstoque = (livroSelecionado?.qEstoque ?: 1) - 1

                voltarParaTelaDesejado()
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Erro ao decrementar estoque", e)
                // FALHA: Mostra um erro para o usuário e reabilita o botão
                Toast.makeText(this, "Falha ao agendar o aluguel. Tente novamente.", Toast.LENGTH_LONG).show()
                findViewById<Button>(R.id.buttonConfirmarInf2).isEnabled = true
            }
    }

    // Função centralizada para voltar à tela de detalhes, enviando os dados de volta
    private fun voltarParaTelaDesejado() {
        val intent = Intent(this, Tela_Livro_Desejado::class.java).apply {
            // PASSO CRUCIAL: DEVOLVE O OBJETO LIVRO ATUALIZADO PARA EVITAR O CRASH
            putExtra("LIVRO_SELECIONADO", livroSelecionado)
            // Limpa as telas de agendamento (Informacoes1 e Informacoes2) da pilha
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish() // Finaliza a Tela_Informacoes2
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
