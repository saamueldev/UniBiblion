package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.SearchView // Importe o SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class Adm_Tela_Acervo_Livros : AppCompatActivity() {

    // 1. Declaração dos componentes da UI
    private lateinit var recyclerViewAdm: RecyclerView
    private lateinit var searchViewAdm: SearchView // Adicionado
    private lateinit var btnAdicionar: ImageButton
    private var livroAdapter: LivroAdapter? = null
    private val db = FirebaseFirestore.getInstance()

    // Variável para guardar o texto da busca
    private var filtroTexto: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_adm_tela_acervo_livros)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 2. Inicialização dos componentes usando os IDs do layout
        btnAdicionar = findViewById(R.id.btnAdicionar)
        recyclerViewAdm = findViewById(R.id.recyclerViewLivros)
        searchViewAdm = findViewById(R.id.searchViewLivros) // Adicionado

        // Ação do botão de adicionar continua a mesma
        btnAdicionar.setOnClickListener {
            val intent = Intent(this, Adm_Tela_Cadastro_Livro::class.java)
            startActivity(intent)
        }

        // 3. Chamada para as funções de configuração
        setupRecyclerView()
        setupSearchListener() // Adicionado
    }

    private fun setupRecyclerView() {
        // A query inicial não muda
        val query: Query = db.collection("livros").orderBy("titulo", Query.Direction.ASCENDING)

        val options = FirestoreRecyclerOptions.Builder<Livro>()
            .setQuery(query, Livro::class.java)
            .build()

        // Passamos 'null' como listener pois o ADM não tem ação de clique no item
        livroAdapter = LivroAdapter(options, null)

        recyclerViewAdm.layoutManager = LinearLayoutManager(this)
        recyclerViewAdm.adapter = livroAdapter
    }

    // 4. Função para configurar o listener da barra de pesquisa (copiada da tela de usuário)
    private fun setupSearchListener() {
        searchViewAdm.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Não faz nada ao submeter, a busca é em tempo real
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Guarda o novo texto e aplica o filtro
                filtroTexto = newText ?: ""
                aplicarFiltros()
                return true
            }
        })
    }

    // 5. Função que chama o método de filtro do adapter
    private fun aplicarFiltros() {
        // Para o ADM, os filtros de estado e curso são sempre "Todos"
        livroAdapter?.aplicarFiltrosCombinados(filtroTexto, "Todos", "Todos")
    }

    // Gerenciamento do ciclo de vida do adapter (essencial)
    override fun onStart() {
        super.onStart()
        livroAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        livroAdapter?.stopListening()
    }
}
