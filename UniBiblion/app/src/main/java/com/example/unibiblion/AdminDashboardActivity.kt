package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminDashboardActivity : AppCompatActivity() {

    // 1. DECLARAÇÃO: Declara a BottomNavigationView como variável da classe
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        // Configuração da Toolbar (necessária para o botão Voltar)
        setSupportActionBar(findViewById(R.id.toolbar_admin))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 2. INICIALIZAÇÃO: Inicializa a variável aqui
        bottomNavigation = findViewById(R.id.bottom_navigation)

        // 3. Configurar o CardView para Gerenciar Reviews
        val cardManageReviews: CardView = findViewById(R.id.card_manage_reviews)
        cardManageReviews.setOnClickListener {
            // Navegação para a tela de reviews do administrador
            val intent = Intent(this, AdminReviewsActivity::class.java)
            startActivity(intent)
        }

        // 4. Configurar a Bottom Navigation View
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_livraria -> {
                    // Se o admin clicar na própria Livraria/Home, leva para a tela central
                    val intent = Intent(this, Adm_Tela_Central_Livraria::class.java)
                    // Usamos estas flags para limpar a pilha de volta para a Home Central
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }

                R.id.nav_noticias -> {
                    val intent = Intent(this, Adm_Tela_Mural_Noticias_Eventos::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_chatbot -> {
                    val intent = Intent(this, Tela_Chat_Bot::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_perfil -> {
                    val intent = Intent(this, Adm_Tela_De_Perfil::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    // 5. SOLUÇÃO: Mover a lógica de seleção para onResume()
    override fun onResume() {
        super.onResume()
        // Força a seleção do ícone de Livraria (Home Admin) sempre que a tela é retomada.
        bottomNavigation.menu.findItem(R.id.nav_livraria).isChecked = true
    }

    // Método que garante que o botão Voltar (seta na Toolbar) funcione
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
