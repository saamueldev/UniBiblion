package com.example.unibiblion

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class NoticiasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_noticias)

        // Código Edge-to-Edge (agora funcional)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- PREPARAÇÃO DO RECYCLERVIEW (Parte 2) ---

        // 1. Obter o RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_noticias)

        // 2. Criar a lista de dados de exemplo
        val dadosNoticias = criarDadosDeExemplo()

        // 3. Configurar o Layout Manager
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 4. (Temporário) Criar um Adaptador vazio

        recyclerView.adapter = NoticiasAdapter(dadosNoticias)

        // 5. Configurar a BottomNavigationView
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_noticias // OU o ID correto

    }

    // Função para criar dados de teste com variação de layout
    private fun criarDadosDeExemplo(): List<Noticia> {
        return listOf(
            Noticia("Notícia de Destaque 1", "Conteúdo de alto impacto.", "url1", Noticia.TIPO_IMAGEM_GRANDE),
            Noticia("Notícia Lateral 1", "Conteúdo padrão e resumido.", "url2", Noticia.TIPO_IMAGEM_LATERAL),
            Noticia("Destaque 2: Evento Importante", "Detalhes da conferência anual.", "url3", Noticia.TIPO_IMAGEM_GRANDE)
        )
    }
}