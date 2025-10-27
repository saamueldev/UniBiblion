// Em: app/src/main/java/com/example/unibiblion/Tela_Acervo_Livros.kt
package com.example.unibiblion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Tela_Acervo_Livros : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var livroAdapter: LivroAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_acervo_livros)

        // 1. Encontre o RecyclerView no layout
        recyclerView = findViewById(R.id.recyclerViewLivros)

        // 2. Defina o LayoutManager
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 3. Crie os dados de exemplo
        val listaDeLivros = obterDadosDeExemplo()

        // 4. Crie e configure o Adapter
        livroAdapter = LivroAdapter(listaDeLivros)
        recyclerView.adapter = livroAdapter
    }

    // Função para gerar dados de exemplo. Substitua pela sua lógica real.
    private fun obterDadosDeExemplo(): List<Livro> {
        // CORRIGIDO: A lista agora usa a nova data class Livro(Int, String, String, Int)
        // ATENÇÃO: Substitua 'R.drawable.ic_launcher_background' por imagens reais do seu projeto.
        return listOf(
            Livro(1, "O Senhor dos Anéis", "J.R.R. Tolkien", R.drawable.ic_launcher_background),
            Livro(2, "O Guia do Mochileiro das Galáxias", "Douglas Adams", R.drawable.ic_launcher_background),
            Livro(3, "1984", "George Orwell", R.drawable.ic_launcher_background),
            Livro(4, "Dom Casmurro", "Machado de Assis", R.drawable.ic_launcher_background),
            Livro(5, "A Revolução dos Bichos", "George Orwell", R.drawable.ic_launcher_background),
            Livro(6, "O Pequeno Príncipe", "Antoine de Saint-Exupéry", R.drawable.ic_launcher_background)
        )
    }
}
