package com.example.unibiblion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

// A classe CursosAdapter permanece a mesma, pois usa dados locais.
class CursosAdapter(private val listaDeCursos: List<Curso>) :
    RecyclerView.Adapter<CursosAdapter.CursoViewHolder>() {
    inner class CursoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CursoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return CursoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CursoViewHolder, position: Int) {
        holder.textView.text = listaDeCursos[position].nome
    }

    override fun getItemCount() = listaDeCursos.size
}


class Tela_Livros_Curso : AppCompatActivity() {

    private lateinit var recyclerViewLivros: RecyclerView
    private lateinit var searchViewLivros: SearchView
    private lateinit var btnFiltro: ImageButton
    private lateinit var cardViewFiltro: CardView
    private lateinit var recyclerViewCursos: RecyclerView
    private lateinit var btnAplicarFiltro: Button

    // O adapter agora é inicializado como nulo e gerenciado no ciclo de vida
    private var livroAdapter: LivroAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_livros_curso)

        // Inicialização das Views
        recyclerViewLivros = findViewById(R.id.recyclerViewLivros)
        searchViewLivros = findViewById(R.id.searchViewLivros)
        btnFiltro = findViewById(R.id.btnAdicionar)
        cardViewFiltro = findViewById(R.id.cardViewFiltro)
        recyclerViewCursos = findViewById(R.id.recyclerViewCursos)
        btnAplicarFiltro = findViewById(R.id.btnAplicarFiltro)

        // Lida com as barras de sistema (insets)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- LÓGICA COM FIREBASE ---

        // 1. Configurar a consulta ao Firestore para buscar os livros
        val db = FirebaseFirestore.getInstance()
        val query: Query = db.collection("livros").orderBy("titulo", Query.Direction.ASCENDING)

        // 2. Configurar as opções do FirestoreRecyclerAdapter
        val options = FirestoreRecyclerOptions.Builder<Livro>()
            .setQuery(query, Livro::class.java) // Conecta a consulta e o modelo de dados
            .build()

        // 3. Inicializar o LivroAdapter com as opções do Firestore
        livroAdapter = LivroAdapter(options)

        // 4. Configurar a RecyclerView para os livros
        recyclerViewLivros.layoutManager = GridLayoutManager(this, 2)
        recyclerViewLivros.adapter = livroAdapter

        // --- FIM DA LÓGICA COM FIREBASE ---

        // Configuração do SearchView
        searchViewLivros.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // A lógica de filtro com FirestoreRecyclerAdapter é diferente e mais complexa.
                // Requer a criação de uma nova query e a atualização do adapter.
                // Manteremos a funcionalidade básica por enquanto.
                return true
            }
        })

        // Configuração da RecyclerView de Cursos (usa dados locais)
        val listaDeCursos = obterDadosDosCursos()
        recyclerViewCursos.layoutManager = LinearLayoutManager(this)
        recyclerViewCursos.adapter = CursosAdapter(listaDeCursos)

        // Lógica dos botões de filtro
        btnFiltro.setOnClickListener {
            cardViewFiltro.visibility = if (cardViewFiltro.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        btnAplicarFiltro.setOnClickListener {
            Toast.makeText(this, "Filtros aplicados (Lógica a ser implementada)", Toast.LENGTH_SHORT).show()
            cardViewFiltro.visibility = View.GONE
        }
    }

    // --- CICLO DE VIDA DO ADAPTER DO FIREBASE ---

    override fun onStart() {
        super.onStart()
        // Inicia a escuta por atualizações do Firestore quando a tela fica visível
        livroAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        // Para a escuta para economizar recursos quando a tela não está visível
        livroAdapter?.stopListening()
    }

    // --- FUNÇÕES DE DADOS LOCAIS (APENAS PARA CURSOS) ---

    private fun obterDadosDosCursos(): List<Curso> {
        return listOf(
            Curso(1, "Ciência da Computação"),
            Curso(2, "Engenharia Civil"),
            Curso(3, "Direito"),
            Curso(4, "Medicina"),
            Curso(5, "Arquitetura"),
            Curso(6, "Psicologia"),
            Curso(7, "Administração")
        )
    }
}
