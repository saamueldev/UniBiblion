package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class Tela_Central_Livraria : AppCompatActivity() {

    // 1. DECLARAÇÃO: Variável de classe para a Bottom Navigation
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_central_livraria)

        // 2. INICIALIZAÇÃO DA BOTTOM NAVIGATION
        bottomNavigation = findViewById(R.id.bottom_navigation)

        // Inicializa os botões do corpo da tela
        val buttonAcervo: Button = findViewById(R.id.buttonGerenciarCatalogo)
        val buttonLivros: Button = findViewById(R.id.buttonGerenciarCabines)
        val buttonSala: Button = findViewById(R.id.buttonGerenciamentoGeral)
        val buttonRenovar: Button = findViewById(R.id.buttonRenovarLivros)

        // 3. CONFIGURAÇÃO DA NAVEGAÇÃO INTERNA (BOTÕES)

        // Mapeia para Catálogo/Acervo
        buttonAcervo.setOnClickListener {
            val intent = Intent(this, Tela_Acervo_Livros::class.java)
            startActivity(intent)
        }

        // Mapeia para Alugar livros / Livros Curso
        buttonLivros.setOnClickListener {
            val intent = Intent(this, Tela_Livros_Curso::class.java)
            startActivity(intent)
        }

        // ALTERAÇÃO AQUI: Mapeamento para a Activity correta (CabinesIndividuaisActivity)
        buttonSala.setOnClickListener {
            val intent = Intent(this, CabinesIndividuaisActivity::class.java)
            startActivity(intent)
        }

        // Mapeia para Renovar livros
        buttonRenovar.setOnClickListener {
            val intent = Intent(this, Tela_Renovacao_Livros::class.java)
            startActivity(intent)
        }


        // 4. CONFIGURAÇÃO DA BOTTOM NAVIGATION LISTENER
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_livraria -> {
                    // Já estamos aqui (Home). Não navega para si mesma.
                    true
                }

                R.id.nav_noticias -> {
                    val intent = Intent(this, NoticiasActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_chatbot -> {
                    val intent = Intent(this, Tela_Chat_Bot::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_perfil -> {
                    val intent = Intent(this, Tela_De_Perfil::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    // 5. SOLUÇÃO DE ESTADO: Garante que o ícone da Livraria esteja selecionado ao retornar
    override fun onResume() {
        super.onResume()
        bottomNavigation.menu.findItem(R.id.nav_livraria).isChecked = true
    }
}
