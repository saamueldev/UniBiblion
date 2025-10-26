package com.example.unibiblion

import android.os.Bundle
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Tela_Acervo_Livros : AppCompatActivity() {

    private lateinit var recyclerViewLivros: RecyclerView
    private lateinit var searchViewLivros: SearchView
    private lateinit var btnFiltro: ImageButton
    // private lateinit var livrosAdapter: LivrosAdapter // Você precisará criar este Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_acervo_livros)

        // Inicializa as views
        recyclerViewLivros = findViewById(R.id.recyclerViewLivros)
        searchViewLivros = findViewById(R.id.searchViewLivros)
        btnFiltro = findViewById(R.id.btnAdicionar)

        // Aplica os insets (boa prática com enableEdgeToEdge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- Configuração do RecyclerView ---
        // Exemplo de como configurar com GridLayoutManager de 2 colunas
        recyclerViewLivros.layoutManager = GridLayoutManager(this, 2)

        // **TODO: Crie sua classe de modelo (Livro) e a classe de Adapter (LivrosAdapter)**
        // Exemplo:
        // val listaDeLivros = obterDadosDosLivros() // Função para carregar seus dados
        // livrosAdapter = LivrosAdapter(listaDeLivros)
        // recyclerViewLivros.adapter = livrosAdapter

        // --- Configuração da Barra de Pesquisa ---
        searchViewLivros.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Lógica de pesquisa finalizada (ex: quando o usuário aperta Enter)
                // **TODO: Implementar a lógica de filtro no adapter aqui**
                // livrosAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Lógica de pesquisa em tempo real (a cada letra digitada)
                // **TODO: Implementar a lógica de filtro no adapter aqui**
                // livrosAdapter.filter.filter(newText)
                return true
            }
        })

        // --- Configuração do Botão de Filtro ---
        btnFiltro.setOnClickListener {
            // **TODO: Implementar a lógica para abrir a aba/dialog de filtros**
            Toast.makeText(this, "Abrir aba de filtros", Toast.LENGTH_SHORT).show()
        }
    }
}