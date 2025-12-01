package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView // Importação necessária
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// Implementa a interface para "ouvir" os cliques do adapter
class Adm_Tela_Acervo_Livros : AppCompatActivity(), LivroAdapter.OnItemClickListener {

    private lateinit var recyclerViewAdm: RecyclerView
    private lateinit var searchViewAdm: SearchView
    private lateinit var btnAdicionar: android.widget.ImageButton
    private lateinit var bottomNavigationView: BottomNavigationView // 👈 DECLARAÇÃO
    private var livroAdapter: LivroAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adm_tela_acervo_livros)

        // Inicialização dos componentes usando os IDs do layout
        btnAdicionar = findViewById(R.id.btnAdicionar)
        recyclerViewAdm = findViewById(R.id.recyclerViewLivros)
        searchViewAdm = findViewById(R.id.searchViewLivros)
        bottomNavigationView = findViewById(R.id.bottom_navigation) // 👈 INICIALIZAÇÃO (VERIFIQUE O ID NO XML!)

        btnAdicionar.setOnClickListener {
            val intent = Intent(this, Adm_Tela_Cadastro_Livro::class.java)
            startActivity(intent)
        }

        // Chamada para as funções de configuração
        setupRecyclerView()
        setupSearchListener()
        setupBottomNavigation() // 👈 CHAMADA PARA A NOVA FUNÇÃO
    }

    private fun setupBottomNavigation() {
        // Define o item atual como selecionado (Livraria)
        bottomNavigationView.selectedItemId = R.id.nav_livraria

        bottomNavigationView.setOnItemSelectedListener { item ->
            // Define a classe de destino com base no ID do item do menu
            val activityClass = when (item.itemId) {
                R.id.nav_livraria -> null // Já estamos na Home Admin
                R.id.nav_noticias -> Adm_Tela_Mural_Noticias_Eventos::class.java
                R.id.nav_chatbot -> Tela_Adm_Chat_Bot::class.java
                R.id.nav_perfil -> Adm_Tela_De_Perfil::class.java
                else -> null
            }

            if (activityClass != null) {
                val intent = Intent(this, activityClass).apply {
                    // Usando Flags para evitar empilhamento desnecessário
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
                // É bom finalizar a Activity atual se for para uma tela de navegação principal diferente.
                finish()
                return@setOnItemSelectedListener true
            }

            // Se for o item nav_livraria, retorna true para manter o item selecionado e evitar cliques inválidos.
            return@setOnItemSelectedListener item.itemId == R.id.nav_livraria
        }
    }


    private fun setupRecyclerView() {
        val query: Query = Firebase.firestore.collection("livros")
            .orderBy("titulo", Query.Direction.ASCENDING)

        val options = FirestoreRecyclerOptions.Builder<Livro>()
            .setQuery(query, Livro::class.java)
            .build()

        livroAdapter = LivroAdapter(options, this)

        recyclerViewAdm.layoutManager = LinearLayoutManager(this)
        recyclerViewAdm.adapter = livroAdapter
    }

    // Este método é executado quando um item da lista é clicado
    override fun onItemClick(livro: Livro) {
        val intent = Intent(this, Adm_Tela_Detalhes_Livro::class.java).apply {
            putExtra("LIVRO_SELECIONADO", livro)
        }
        startActivity(intent)
    }

    private fun setupSearchListener() {
        searchViewAdm.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                // Presumindo que aplicarFiltrosCombinados existe no seu LivroAdapter
                livroAdapter?.aplicarFiltrosCombinados(newText.orEmpty(), "Todos", "Todos")
                return true
            }
        })
    }

    override fun onStart() {
        super.onStart()
        livroAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        livroAdapter?.stopListening()
    }
}