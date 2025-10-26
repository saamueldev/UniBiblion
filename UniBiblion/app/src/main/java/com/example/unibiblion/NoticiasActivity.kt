package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class NoticiasActivity : AppCompatActivity() {

    // 1. DECLARAÇÃO: Variável de classe para a Bottom Navigation
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_noticias)

        // 2. OBTÉM AS REFERÊNCIAS
        bottomNavigation = findViewById(R.id.bottom_navigation) // Inicialização da Bottom Nav

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

        recyclerView.adapter = NoticiasAdapter(dadosNoticias.toMutableList(), isAdmin = false)

        // 3. CONFIGURAÇÃO DA BOTTOM NAVIGATION LISTENER (NOVO)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_livraria -> {
                    // Item: ic_book. Navega para a Tela Central Livraria (Home)
                    val intent = Intent(this, Tela_Central_Livraria::class.java)
                    // Flags para limpar a pilha e evitar múltiplas instâncias da Home
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }

                R.id.nav_noticias -> {
                    // Já estamos aqui. Não faz nada (ou retorna true).
                    true
                }

                R.id.nav_chatbot -> {
                    // Item: ic_chat. Navega para o Chatbot
                    val intent = Intent(this, Tela_Chat_Bot::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_perfil -> {
                    // Item: ic_profile. Navega para o Perfil
                    val intent = Intent(this, Tela_De_Perfil::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    // 4. SOLUÇÃO DE ESTADO: Garante que o ícone de Notícias esteja selecionado ao retornar
    override fun onResume() {
        super.onResume()
        // Força a seleção do ícone de Notícias
        if (::bottomNavigation.isInitialized) { // Verifica se foi inicializada (segurança)
            bottomNavigation.menu.findItem(R.id.nav_noticias).isChecked = true
        }
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
