package com.example.unibiblion

import android.os.Bundle
import android.widget.ImageButton
import android.widget.SearchView // CORREÇÃO: Usando a importação antiga
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.android.material.bottomnavigation.BottomNavigationView

class Tela_Acervo_Livros : AppCompatActivity() {

    private lateinit var recyclerViewLivros: RecyclerView
    private lateinit var searchView: SearchView // CORREÇÃO: Usando o tipo antigo
    private lateinit var btnFiltro: ImageButton
    private lateinit var bottomNav: BottomNavigationView

    private var livroAdapter: LivroAdapter? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_acervo_livros)

        // 1. Inicializa todas as Views do layout
        recyclerViewLivros = findViewById(R.id.recyclerViewLivros)
        searchView = findViewById(R.id.searchViewLivros)
        btnFiltro = findViewById(R.id.btnAdicionar)
        bottomNav = findViewById(R.id.bottom_navigation)

        // 2. Configura o RecyclerView
        recyclerViewLivros.layoutManager = LinearLayoutManager(this)

        // 3. Configura o RecyclerView com a consulta simples que não causa crash
        setupRecyclerView()

        // 4. Configura o listener da barra de pesquisa
        setupSearchListener()

        // 5. Adiciona funcionalidade ao botão de filtro
        setupFilterButton()
    }

    /**
     * Configura o RecyclerView com uma consulta simples. A filtragem será feita no adapter.
     */
    private fun setupRecyclerView() {
        val query: Query = db.collection("livros")
            .orderBy("titulo", Query.Direction.ASCENDING)

        val options = FirestoreRecyclerOptions.Builder<Livro>()
            .setQuery(query, Livro::class.java)
            .setLifecycleOwner(this) // Gerencia o ciclo de vida do adapter automaticamente
            .build()

        livroAdapter = LivroAdapter(options)
        recyclerViewLivros.adapter = livroAdapter
    }

    /**
     * Configura o listener da SearchView para chamar o filtro do adapter.
     */
    private fun setupSearchListener() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // A filtragem já acontece em tempo real, não precisa de ação no submit
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Chama o filtro implementado no LivroAdapter
                livroAdapter?.filter?.filter(newText)
                return true
            }
        })
    }

    /**
     * Adiciona a funcionalidade de clique ao botão de filtro.
     */
    private fun setupFilterButton() {
        btnFiltro.setOnClickListener {
            // Exibe uma mensagem temporária para confirmar que o clique funcionou.
            Toast.makeText(this, "Botão de filtro clicado!", Toast.LENGTH_SHORT).show()
        }
    }
}
