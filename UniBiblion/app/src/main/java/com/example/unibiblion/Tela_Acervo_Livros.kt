package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.Serializable

class Tela_Acervo_Livros : AppCompatActivity(), LivroAdapter.OnItemClickListener {

    private lateinit var recyclerViewLivros: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var btnFiltro: ImageButton
    private lateinit var bottomNav: BottomNavigationView // Variável para a barra de navegação
    private var livroAdapter: LivroAdapter? = null
    private val db = FirebaseFirestore.getInstance()
    private var filtroTexto: String = ""
    private var filtroEstado: String = "Todos"
    private var filtroCurso: String = "Todos"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_acervo_livros)

        // Inicialização das Views
        recyclerViewLivros = findViewById(R.id.recyclerViewLivros)
        searchView = findViewById(R.id.searchViewLivros)
        btnFiltro = findViewById(R.id.btnAdicionar)
        bottomNav = findViewById(R.id.bottom_navigation) // Encontra a barra de navegação no layout

        recyclerViewLivros.layoutManager = LinearLayoutManager(this)

        // Configuração dos componentes da tela
        setupRecyclerView()
        setupSearchListener()
        setupFilterButton()
        setupBottomNavigation() // Configura a lógica da barra de navegação
    }

    /**
     * Configura o listener da BottomNavigationView para tratar os cliques nos itens do menu.
     */
    private fun setupBottomNavigation() {
        // Define o item "Livraria" como selecionado para feedback visual ao usuário
        bottomNav.selectedItemId = R.id.nav_livraria

        bottomNav.setOnItemSelectedListener { item ->
            var targetActivity: Class<*>? = null

            // Impede a navegação se o usuário já estiver na tela de destino
            if (item.itemId == R.id.nav_livraria && this is Tela_Central_Livraria) {
                return@setOnItemSelectedListener false // Retorna false para não fazer nada
            }

            when (item.itemId) {
                // CORREÇÃO: Direciona para a tela central da livraria
                R.id.nav_livraria -> {
                    targetActivity = Tela_Central_Livraria::class.java
                }
                R.id.nav_noticias -> {
                    targetActivity = NoticiasActivity::class.java
                }
                R.id.nav_chatbot -> {
                    targetActivity = Tela_Chat_Bot::class.java
                }
                R.id.nav_perfil -> {
                    targetActivity = Tela_De_Perfil::class.java
                }
            }

            if (targetActivity != null) {
                val intent = Intent(this, targetActivity).apply {
                    // Limpa a pilha de atividades acima da nova tela
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }

            true // Indica que o evento foi tratado
        }
    }

    override fun onItemClick(livro: Livro) {
        val intent = Intent(this, Tela_Livro_Desejado::class.java)
        intent.putExtra("LIVRO_SELECIONADO", livro)
        startActivity(intent)
    }

    private fun setupRecyclerView() {
        val query: Query = db.collection("livros").orderBy("titulo", Query.Direction.ASCENDING)
        val options = FirestoreRecyclerOptions.Builder<Livro>()
            .setQuery(query, Livro::class.java)
            .setLifecycleOwner(this)
            .build()
        livroAdapter = LivroAdapter(options, this)
        recyclerViewLivros.adapter = livroAdapter
    }

    private fun setupSearchListener() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filtroTexto = newText ?: ""
                aplicarFiltros()
                return true
            }
        })
    }

    private fun setupFilterButton() {
        btnFiltro.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menuInflater.inflate(R.menu.filtro_menu, popup.menu)
            popup.menu.findItem(getIdEstado(filtroEstado)).isChecked = true
            popup.menu.findItem(getIdCurso(filtroCurso)).isChecked = true
            popup.setOnMenuItemClickListener { item ->
                when (item.groupId) {
                    R.id.menu_filtro_estado -> {
                        filtroEstado = getEstadoFromId(item.itemId)
                        item.isChecked = true
                    }
                    R.id.menu_filtro_curso -> {
                        filtroCurso = getCursoFromId(item.itemId)
                        item.isChecked = true
                    }
                    else -> {
                        if (item.itemId == R.id.menu_limpar_filtros) {
                            filtroEstado = "Todos"
                            filtroCurso = "Todos"
                        }
                    }
                }
                aplicarFiltros()
                true
            }
            popup.show()
        }
    }

    private fun getEstadoFromId(itemId: Int): String = when (itemId) {
        R.id.filtro_estado_novo -> "Novo"
        R.id.filtro_estado_usado -> "Usado"
        else -> "Todos"
    }

    private fun getIdEstado(estado: String): Int = when (estado) {
        "Novo" -> R.id.filtro_estado_novo
        "Usado" -> R.id.filtro_estado_usado
        else -> R.id.filtro_estado_todos
    }

    private fun getCursoFromId(itemId: Int): String = when (itemId) {
        R.id.filtro_curso_medicina -> "Medicina"
        R.id.filtro_curso_direito -> "Direito"
        R.id.filtro_curso_administracao -> "Administração"
        R.id.filtro_curso_computacao -> "Ciência da Computação"
        R.id.filtro_curso_outros -> "Outros"
        else -> "Todos"
    }

    private fun getIdCurso(curso: String): Int = when (curso) {
        "Medicina" -> R.id.filtro_curso_medicina
        "Direito" -> R.id.filtro_curso_direito
        "Administração" -> R.id.filtro_curso_administracao
        "Ciência da Computação" -> R.id.filtro_curso_computacao
        "Outros" -> R.id.filtro_curso_outros
        else -> R.id.filtro_curso_todos
    }

    private fun aplicarFiltros() {
        livroAdapter?.aplicarFiltrosCombinados(filtroTexto, filtroEstado, filtroCurso)
    }
}
