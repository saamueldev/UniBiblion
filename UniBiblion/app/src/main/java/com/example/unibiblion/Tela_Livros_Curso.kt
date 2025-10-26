package com.example.unibiblion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
data class Curso(val nome: String)

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_livros_curso)

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

        recyclerViewLivros.layoutManager = GridLayoutManager(this, 2)

        searchViewLivros.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                return true
            }
        })

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
            cardViewFiltro.visibility = View.GONE // Oculta a aba após aplicar
        }
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