package com.example.unibiblion

import android.content.Intent // Import necessário
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView // Import necessário
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Tela_Renovacao_Livros : AppCompatActivity(), LivroAlugadoAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private var adapter: LivroAlugadoAdapter? = null
    private lateinit var bottomNavigation: BottomNavigationView // 1. Declaração da Bottom Navigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_renovacao_livros)

        recyclerView = findViewById(R.id.recyclerViewLivroParaRenovacao)
        setupRecyclerView()

        // 2. Inicialização da Bottom Navigation
        bottomNavigation = findViewById(R.id.bottom_navigation)

        // 3. Configuração do Listener para a navegação
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_livraria -> {
                    // Navega para a tela central da livraria
                    val intent = Intent(this, Tela_Central_Livraria::class.java)
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

    // 4. Garante que o ícone da Livraria esteja selecionado ao entrar ou retornar para esta tela
    override fun onResume() {
        super.onResume()
        bottomNavigation.menu.findItem(R.id.nav_livraria).isChecked = true
    }

    private fun setupRecyclerView() {
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
            livro.dataDevolucao?.let { dataTimestamp ->
                mostrarDialogoDeRenovacao(documentId, dataTimestamp)
            } ?: Toast.makeText(this, "Data de devolução inválida.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarDialogoDeRenovacao(documentId: String, dataDevolucaoTimestamp: Timestamp) {
        val formatoData = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dataInicial = Calendar.getInstance().apply { time = dataDevolucaoTimestamp.toDate() }
        val novaData = (dataInicial.clone() as Calendar).apply { add(Calendar.MONTH, 1) }

        val mensagem = "Data de Entrega Inicial: ${formatoData.format(dataInicial.time)}\n\n" +
                "Só poderá postergar até ${formatoData.format(novaData.time)}"

        AlertDialog.Builder(this)
            .setTitle("Renovar Empréstimo?")
            .setMessage(mensagem)
            .setPositiveButton("Confirmar") { _, _ ->
                renovarDataDeEntrega(documentId, Timestamp(novaData.time))
            }
            .setNegativeButton("Voltar", null)
            .show()
    }

    private fun renovarDataDeEntrega(documentId: String, novaDataTimestamp: Timestamp) {
        val updates = mapOf(
            "dataDevolucao" to novaDataTimestamp,
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
}
