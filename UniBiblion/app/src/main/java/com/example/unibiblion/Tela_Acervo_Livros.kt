package com.example.unibiblion

import android.content.Intent // Importe o Intent
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
import java.io.Serializable // Importe Serializable para passar o objeto

// 1. Implemente a interface do adapter
class Tela_Acervo_Livros : AppCompatActivity(), LivroAdapter.OnItemClickListener {

    // ... (seu código existente: lateinit var, onCreate, etc.)

    // Mantenha todo o seu código existente até o setupRecyclerView
    private lateinit var recyclerViewLivros: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var btnFiltro: ImageButton
    private lateinit var bottomNav: BottomNavigationView
    private var livroAdapter: LivroAdapter? = null
    private val db = FirebaseFirestore.getInstance()
    private var filtroTexto: String = ""
    private var filtroEstado: String = "Todos"
    private var filtroCurso: String = "Todos"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_acervo_livros)
        recyclerViewLivros = findViewById(R.id.recyclerViewLivros)
        searchView = findViewById(R.id.searchViewLivros)
        btnFiltro = findViewById(R.id.btnAdicionar)
        bottomNav = findViewById(R.id.bottom_navigation)
        recyclerViewLivros.layoutManager = LinearLayoutManager(this)
        setupRecyclerView()
        setupSearchListener()
        setupFilterButton()
    }

    private fun setupRecyclerView() {
        val query: Query = db.collection("livros").orderBy("titulo", Query.Direction.ASCENDING)
        val options = FirestoreRecyclerOptions.Builder<Livro>()
            .setQuery(query, Livro::class.java)
            .setLifecycleOwner(this)
            .build()
        // 2. Passe 'this' (a activity) como o listener ao criar o adapter
        livroAdapter = LivroAdapter(options, this)
        recyclerViewLivros.adapter = livroAdapter
    }

    // 3. Sobrescreva o método da interface para tratar o clique
    override fun onItemClick(livro: Livro) {
        val intent = Intent(this, Tela_Livro_Desejado::class.java)

        // 4. Coloque o objeto livro inteiro no Intent
        // Para isso, precisaremos fazer uma pequena alteração no Livro.kt
        intent.putExtra("LIVRO_SELECIONADO", livro)

        startActivity(intent)
    }

    // ... (resto do seu código: setupSearchListener, setupFilterButton, etc. permanece igual)
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

                    else -> { // Ações individuais
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
