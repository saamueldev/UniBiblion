package com.example.unibiblion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Certifique-se de que o caminho abaixo corresponde ao pacote da sua BottomNavigationView
// import com.google.android.material.bottomnavigation.BottomNavigationView

class Tela_Acervo_Livros : AppCompatActivity() {

    private lateinit var recyclerViewLivros: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // O nome do arquivo XML do seu layout principal
        setContentView(R.layout.activity_tela_acervo_livros)

        // 1. Inicializa a RecyclerView (usando o ID que você forneceu no XML)
        recyclerViewLivros = findViewById(R.id.recyclerViewLivros)

        // 2. Cria a lista de dados de exemplo (use seus próprios Drawables!)
        val listaDeLivros = criarListaDeExemplo()

        // 3. Configura o LayoutManager (DEFINE COMO OS ITENS VÃO APARECER: Vertical, Horizontal, Grid)
        // Para uma lista vertical padrão, use LinearLayoutManager
        recyclerViewLivros.layoutManager = LinearLayoutManager(this)

        // 4. Cria e ATRIBUI o Adapter à RecyclerView
        val adapter = LivroAdapter(listaDeLivros)
        recyclerViewLivros.adapter = adapter

        // Configurações da SearchView e BottomNavigationView (opcional, para referência)
        // val searchView = findViewById<SearchView>(R.id.searchViewLivros)
        // val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        // ...
    }

    // Função auxiliar para criar dados de demonstração
    private fun criarListaDeExemplo(): List<Livro> {
        return listOf(
            // ATENÇÃO: Substitua R.drawable.sommervile pelos seus próprios recursos de imagem
            Livro(1, "Engenharia de Software", "Ian Sommerville", R.drawable.sommervile),
            Livro(2, "O Poder do Hábito", "Charles Duhigg", R.drawable.sommervile), // Use sua imagem correta aqui
            Livro(3, "Introdução ao Kotlin", "Google", R.drawable.sommervile), // Use sua imagem correta aqui
            // Adicione mais livros...
        )
    }
}