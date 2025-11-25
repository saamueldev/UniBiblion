package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView // Importação correta do androidx
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// Implementa a interface para "ouvir" os cliques do adapter
class Adm_Tela_Acervo_Livros : AppCompatActivity(), LivroAdapter.OnItemClickListener {

    private lateinit var recyclerViewAdm: RecyclerView
    private lateinit var searchViewAdm: SearchView // Variável do tipo androidx
    private lateinit var btnAdicionar: android.widget.ImageButton
    private var livroAdapter: LivroAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adm_tela_acervo_livros)

        // Inicialização dos componentes usando os IDs do layout
        btnAdicionar = findViewById(R.id.btnAdicionar)
        recyclerViewAdm = findViewById(R.id.recyclerViewLivros)
        searchViewAdm = findViewById(R.id.searchViewLivros) // Agora o cast funciona!

        btnAdicionar.setOnClickListener {
            val intent = Intent(this, Adm_Tela_Cadastro_Livro::class.java)
            startActivity(intent)
        }

        // Chamada para as funções de configuração
        setupRecyclerView()
        setupSearchListener()
    }

    private fun setupRecyclerView() {
        val query: Query = Firebase.firestore.collection("livros")
            .orderBy("titulo", Query.Direction.ASCENDING)

        val options = FirestoreRecyclerOptions.Builder<Livro>()
            .setQuery(query, Livro::class.java)
            .build()

        // Passa 'this' como o listener, pois a classe implementa a interface
        livroAdapter = LivroAdapter(options, this)

        recyclerViewAdm.layoutManager = LinearLayoutManager(this)
        recyclerViewAdm.adapter = livroAdapter
    }

    // Este método é executado quando um item da lista é clicado
    override fun onItemClick(livro: Livro) {
        val intent = Intent(this, Adm_Tela_Detalhes_Livro::class.java).apply {
            // Envia o objeto Livro completo para a tela de detalhes do admin
            putExtra("LIVRO_SELECIONADO", livro)
        }
        startActivity(intent)
    }

    private fun setupSearchListener() {
        searchViewAdm.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
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
