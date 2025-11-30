package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Timestamp // Importa a classe Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Tela_Renovacao_Livros : AppCompatActivity(), LivroAlugadoAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomNavigation: BottomNavigationView
    private var adapter: LivroAlugadoAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_renovacao_livros)

        recyclerView = findViewById(R.id.recyclerViewLivroParaRenovacao)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        
        setupRecyclerView()
        setupBottomNavigation()
    }

    private fun setupRecyclerView() {
        // A consulta agora ordena um campo do tipo Timestamp, o que funciona corretamente.
        val query: Query = Firebase.firestore.collection("livrosalugados")
            .orderBy("dataDevolucao", Query.Direction.ASCENDING)

        val options = FirestoreRecyclerOptions.Builder<LivroAlugado>()
            .setQuery(query, LivroAlugado::class.java)
            .build()

        adapter = LivroAlugadoAdapter(options, this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    override fun onItemClick(documentId: String, livro: LivroAlugado) {
        if (livro.renovado) {
            Toast.makeText(this, "Este livro já foi renovado uma vez.", Toast.LENGTH_LONG).show()
        } else {
            // Garante que a data não é nula antes de tentar renovar
            livro.dataDevolucao?.let { dataTimestamp ->
                mostrarDialogoDeRenovacao(documentId, dataTimestamp)
            } ?: Toast.makeText(this, "Data de devolução inválida.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarDialogoDeRenovacao(documentId: String, dataDevolucaoTimestamp: Timestamp) {
        val formatoData = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Converte o Timestamp para um objeto Calendar
        val dataInicial = Calendar.getInstance()
        dataInicial.time = dataDevolucaoTimestamp.toDate()

        // Calcula a nova data (data original + 1 mês)
        val novaData = dataInicial.clone() as Calendar
        novaData.add(Calendar.MONTH, 1)

        // Prepara as strings para exibição no diálogo
        val dataInicialStr = formatoData.format(dataInicial.time)
        val novaDataStr = formatoData.format(novaData.time)

        // *** CORREÇÃO AQUI: Ajusta o texto da mensagem para o formato solicitado ***
        val mensagem = "Data de Entrega Inicial: $dataInicialStr\n\n" +
                "Só poderá postergar até $novaDataStr"

        AlertDialog.Builder(this)
            .setTitle("Renovar Empréstimo?")
            .setMessage(mensagem)
            .setPositiveButton("Confirmar") { _, _ ->
                // Passa o objeto Timestamp da nova data para a função de atualização
                renovarDataDeEntrega(documentId, Timestamp(novaData.time))
            }
            .setNegativeButton("Voltar", null)
            .show()
    }

    private fun renovarDataDeEntrega(documentId: String, novaDataTimestamp: Timestamp) {
        val updates = mapOf(
            "dataDevolucao" to novaDataTimestamp, // Atualiza com um objeto Timestamp
            "renovado" to true
        )

        Firebase.firestore.collection("livrosalugados").document(documentId)
            .update(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Renovação confirmada!", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao renovar: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter?.stopListening()
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
}
