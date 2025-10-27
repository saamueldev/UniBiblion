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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
// [O restante do seu código da Activity...]

data class Curso(val nome: String)

class CursosAdapter(private val listaDeCursos: List<Curso>) :
    RecyclerView.Adapter<CursosAdapter.CursoViewHolder>() {
    // [Seu CursosAdapter... (Mantido como estava)]
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
    // [Seus atributos 'lateinit var'...]

    private lateinit var recyclerViewLivros: RecyclerView
    private lateinit var searchViewLivros: SearchView
    private lateinit var btnFiltro: ImageButton

    private lateinit var cardViewFiltro: CardView
    private lateinit var recyclerViewCursos: RecyclerView
    private lateinit var btnAplicarFiltro: Button

    // Lista de livros (variável de estado para a tela)
    private lateinit var listaDeLivros: List<Livro>
    private lateinit var livroAdapter: LivroAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_livros_curso)

        // [Seu código de inicialização de Views e Insets...]
        recyclerViewLivros = findViewById(R.id.recyclerViewLivros)
        searchViewLivros = findViewById(R.id.searchViewLivros)
        btnFiltro = findViewById(R.id.btnAdicionar)

        cardViewFiltro = findViewById(R.id.cardViewFiltro)
        recyclerViewCursos = findViewById(R.id.recyclerViewCursos)
        btnAplicarFiltro = findViewById(R.id.btnAplicarFiltro)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- COMEÇO DA CORREÇÃO/ADIÇÃO DOS LIVROS ---

        // 1. Obtém os dados de livros
        listaDeLivros = obterDadosDosLivros()

        // 2. Cria o Adapter dos Livros
        livroAdapter = LivroAdapter(listaDeLivros)

        // 3. Define o LayoutManager (GridLayoutManager para grade de 2 colunas)
        recyclerViewLivros.layoutManager = GridLayoutManager(this, 2)

        // 4. ATRIBUI o Adapter à RecyclerView
        recyclerViewLivros.adapter = livroAdapter

        // --- FIM DA CORREÇÃO/ADIÇÃO DOS LIVROS ---

        searchViewLivros.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Lógica para filtrar livros deve ser implementada aqui, usando o livroAdapter
                return true
            }
        })

        // [Seu código do CursosAdapter e botões...]
        val listaDeCursos = obterDadosDosCursos()
        recyclerViewCursos.layoutManager = LinearLayoutManager(this)
        recyclerViewCursos.adapter = CursosAdapter(listaDeCursos)

        btnFiltro.setOnClickListener {

            if (cardViewFiltro.visibility == View.GONE) {
                cardViewFiltro.visibility = View.VISIBLE
            } else {
                cardViewFiltro.visibility = View.GONE
            }
        }

        btnAplicarFiltro.setOnClickListener {
            Toast.makeText(this, "Filtros aplicados (Lógica a ser implementada)", Toast.LENGTH_SHORT).show()
            cardViewFiltro.visibility = View.GONE
        }
    }

    private fun obterDadosDosLivros(): List<Livro> {
        // ATENÇÃO: SUBSTITUA R.drawable.sommervile por um recurso de imagem real do seu projeto!
        // Se a imagem não existir, o aplicativo irá CRASHAR!
        return listOf(
            Livro(1, "Livro 1: Design UX/UI", "Autor A", R.drawable.sommervile),
            Livro(2, "Livro 2: Padrões de Projeto", "Autor B", R.drawable.sommervile),
            Livro(3, "Livro 3: Clean Code", "Autor C", R.drawable.sommervile),
            Livro(4, "Livro 4: Redes TCP/IP", "Autor D", R.drawable.sommervile),
            Livro(5, "Livro 5: Testes de Software", "Autor E", R.drawable.sommervile)
        )
    }

    private fun obterDadosDosCursos(): List<Curso> {
        return listOf(
            Curso("Ciência da Computação"),
            Curso("Engenharia Civil"),
            Curso("Direito"),
            Curso("Medicina"),
            Curso("Arquitetura"),
            Curso("Psicologia"),
            Curso("Administração")
        )
    }
}