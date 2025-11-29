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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Timestamp // IMPORTANTE: Importar a classe Timestamp
import com.google.firebase.auth.FirebaseAuth // Para obter o usuário logado
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Tela_Informacoes2 : AppCompatActivity() {

    private var livroSelecionado: Livro? = null
    private lateinit var dataRetiradaTextView: TextView
    private lateinit var horarioRetiradaTextView: TextView
    private lateinit var dataDevolucaoTextView: TextView
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_informacoes2)

        // 1. Encontre os componentes
        val capaImageView: ImageView = findViewById(R.id.CapaLivro)
        val tituloTextView: TextView = findViewById(R.id.NomeLivroLabel)
        val autorTextView: TextView = findViewById(R.id.NomeAutorLabel)
        dataRetiradaTextView = findViewById(R.id.textView5)
        horarioRetiradaTextView = findViewById(R.id.textView6)
        dataDevolucaoTextView = findViewById(R.id.textView7)
        val buttonConfirmar: Button = findViewById(R.id.buttonConfirmarInf2)
        val buttonVoltar: Button = findViewById(R.id.buttonVoltarInf2)
        bottomNavigation = findViewById(R.id.bottom_navigation)

        // 2. Receba todos os dados da Intent
        val dataAgendamento = intent.getStringExtra("DATA_AGENDAMENTO")
        val horaAgendamento = intent.getStringExtra("HORA_AGENDAMENTO")
        livroSelecionado = getSerializable(intent, "LIVRO_SELECIONADO", Livro::class.java)

        if (livroSelecionado == null) {
            Toast.makeText(this, "Erro: dados do livro não encontrados.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 3. Preencha os componentes com os dados
        tituloTextView.text = livroSelecionado?.titulo
        autorTextView.text = "por ${livroSelecionado?.autor}"
        dataRetiradaTextView.text = dataAgendamento
        horarioRetiradaTextView.text = horaAgendamento

        // 4. Calcule e exiba a data de devolução
        if (dataAgendamento != null) {
            try {
                val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val data = formato.parse(dataAgendamento)
                if (data != null) {
                    val calendar = Calendar.getInstance()
                    calendar.time = data
                    calendar.add(Calendar.MONTH, 1) // Adiciona 1 mês
                    val dataDevolucaoStr = formato.format(calendar.time)
                    dataDevolucaoTextView.text = dataDevolucaoStr
                }
            } catch (e: Exception) {
                dataDevolucaoTextView.text = "Erro ao calcular"
            }
        }

        // 5. Carregue a imagem da capa
        if (!livroSelecionado?.capaUrl.isNullOrEmpty()) {
            Glide.with(this).load(livroSelecionado?.capaUrl).placeholder(R.drawable.sommervile).into(capaImageView)
        } else {
            capaImageView.setImageResource(R.drawable.sommervile)
        }

        // 6. Lógica dos Botões
        buttonConfirmar.setOnClickListener {
            decrementarEstoque()
        }

        buttonVoltar.setOnClickListener {
            voltarParaTelaDesejado()
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_livraria -> {
                    val intent = Intent(this, Tela_Central_Livraria::class.java)
                    startActivity(intent)
                    finish()
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

    override fun onResume() {
        super.onResume()
        bottomNavigation.menu.findItem(R.id.nav_livraria).isChecked = true
    }

    private fun decrementarEstoque() {
        val livroId = livroSelecionado?.id
        if (livroId.isNullOrEmpty()) {
            Toast.makeText(this, "Erro: ID do livro não encontrado.", Toast.LENGTH_SHORT).show()
            return
        }

        findViewById<Button>(R.id.buttonConfirmarInf2).isEnabled = false

        val db = Firebase.firestore
        val livroRef = db.collection("livros").document(livroId)

        livroRef.update("qEstoque", FieldValue.increment(-1))
            .addOnSuccessListener {
                Log.d("Firestore", "Estoque do livro $livroId decrementado com sucesso.")
                salvarRegistroDeAluguel()
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Erro ao decrementar estoque", e)
                Toast.makeText(this, "Falha ao agendar. Tente novamente.", Toast.LENGTH_LONG).show()
                findViewById<Button>(R.id.buttonConfirmarInf2).isEnabled = true
            }
    }

    private fun salvarRegistroDeAluguel() {
        // *** CORREÇÃO CRÍTICA: Converte a data de String para Timestamp ***
        val formatoData = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dataDevolucaoStr = dataDevolucaoTextView.text.toString()
        val dataDevolucaoTimestamp: Timestamp

        try {
            val data = formatoData.parse(dataDevolucaoStr)
            if (data == null) {
                Toast.makeText(this, "Erro interno ao processar a data.", Toast.LENGTH_SHORT).show()
                return
            }
            dataDevolucaoTimestamp = Timestamp(data)
        } catch (e: Exception) {
            Toast.makeText(this, "Erro interno de data.", Toast.LENGTH_SHORT).show()
            return
        }

        val dadosAluguel = hashMapOf(
            "titulo" to (livroSelecionado?.titulo ?: "Título indisponível"),
            "capaURL" to (livroSelecionado?.capaUrl ?: ""),
            "dataRetirada" to dataRetiradaTextView.text.toString(),
            "horarioRetirada" to horarioRetiradaTextView.text.toString(),
            "dataDevolucao" to dataDevolucaoTimestamp, // Salva no formato Timestamp
            "renovado" to false, // Adiciona a flag de renovação
            "usuarioId" to (FirebaseAuth.getInstance().currentUser?.uid ?: ""), // ID do usuário
            "livroId" to (livroSelecionado?.id ?: ""), // ID do livro
            "timestampCriacao" to com.google.firebase.Timestamp.now() // Timestamp da criação do aluguel
        )

        Firebase.firestore.collection("livrosalugados")
            .add(dadosAluguel)
            .addOnSuccessListener {
                Log.d("Firestore", "Documento de aluguel adicionado com ID: ${it.id}")
                Toast.makeText(this, "Aluguel agendado com sucesso!", Toast.LENGTH_LONG).show()
                livroSelecionado?.qEstoque = (livroSelecionado?.qEstoque ?: 1) - 1
                voltarParaTelaDesejado()
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Erro ao adicionar documento em 'livrosalugados'", e)
                Toast.makeText(this, "Falha ao registrar o aluguel. Contate o suporte.", Toast.LENGTH_LONG).show()
                findViewById<Button>(R.id.buttonConfirmarInf2).isEnabled = true
            }
    }

    private fun voltarParaTelaDesejado() {
        val intent = Intent(this, Tela_Livro_Desejado::class.java).apply {
            putExtra("LIVRO_SELECIONADO", livroSelecionado)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
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
