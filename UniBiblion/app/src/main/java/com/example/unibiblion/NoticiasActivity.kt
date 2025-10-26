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

        // Código Edge-to-Edge (mantido)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- PREPARAÇÃO DO RECYCLERVIEW ---

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_noticias)
        val dadosNoticias = criarDadosDeExemplo()
        recyclerView.layoutManager = LinearLayoutManager(this)

        // CORREÇÃO CRÍTICA: Adiciona o segundo argumento 'isAdmin = false'
        // Isso resolve o erro de construtor e garante que o usuário comum não veja o lápis.
        recyclerView.adapter = NoticiasAdapter(dadosNoticias.toMutableList(), isAdmin = false)

        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_noticias

    }

    private fun criarDadosDeExemplo(): List<Noticia> {
        val corpoLongoExemplo = "Este é o corpo completo do artigo. Aqui você encontra todos os detalhes, parágrafos e informações que não cabem no preview. O ScrollView garantirá que o usuário possa ler tudo. O UniBiblion busca manter você sempre informado sobre os acontecimentos do campus e do mundo acadêmico. Este texto é longo o suficiente para provar que a rolagem da tela de detalhe está funcionando corretamente."

        return listOf(
            Noticia(
                titulo = "Notícia de Destaque 1",
                preview = "Conteúdo de alto impacto. Este é o resumo.",
                corpo = corpoLongoExemplo,
                urlImagem = "url1",
                tipoLayout = Noticia.TIPO_IMAGEM_GRANDE
            ),
            Noticia(
                titulo = "Notícia Lateral 1",
                preview = "Conteúdo padrão e resumido.",
                corpo = corpoLongoExemplo,
                urlImagem = "url2",
                tipoLayout = Noticia.TIPO_IMAGEM_LATERAL
            ),
            Noticia(
                titulo = "Destaque 2: Evento Importante",
                preview = "Detalhes da conferência anual. Resumo aqui.",
                corpo = corpoLongoExemplo,
                urlImagem = "url3",
                tipoLayout = Noticia.TIPO_IMAGEM_GRANDE
            )
        )
    }
}