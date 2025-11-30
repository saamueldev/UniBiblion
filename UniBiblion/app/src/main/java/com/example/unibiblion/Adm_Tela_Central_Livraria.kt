package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Activity Central de Administração da Livraria (Tela Home do Admin).
 * Contém botões de gerenciamento e a navegação principal (BottomNavigationView).
 */
class Adm_Tela_Central_Livraria : AppCompatActivity() {

    // Declaração do componente de navegação inferior
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_adm_tela_central_livraria)

        // 1. Inicialização da Bottom Navigation
        bottomNavigation = findViewById(R.id.bottom_navigation)

        // 2. Inicialização e Configuração dos Cliques dos Botões de Administração

        // Mapeia: Gerenciar Catálogo -> Adm_Tela_Acervo_Livros
        val buttonGerenciarCatalogo: Button = findViewById(R.id.buttonGerenciarCatalogo)
        buttonGerenciarCatalogo.setOnClickListener {
            val intent = Intent(this, Adm_Tela_Acervo_Livros::class.java)
            startActivity(intent)
        }

        // Mapeia: Gerenciar Cabines -> CabinesAdminActivity (Conforme solicitado)
        val buttonGerenciarCabines: Button = findViewById(R.id.buttonGerenciarCabines)
        buttonGerenciarCabines.setOnClickListener {
            val intent = Intent(this, CabinesAdminListActivity::class.java)
            startActivity(intent)
        }

        // Mapeia: Relatórios -> Adm_Tela_Relatorios
        val buttonRelatorios: Button = findViewById(R.id.buttonRelatorios)
        buttonRelatorios.setOnClickListener {
            val intent = Intent(this, Adm_Tela_Relatorios::class.java)
            startActivity(intent)
        }

        // Mapeia: Gerenciamento Geral -> AdminDashboardActivity
        val buttonGerenciamentoGeral: Button = findViewById(R.id.buttonGerenciamentoGeral)
        buttonGerenciamentoGeral.setOnClickListener {
            val intent = Intent(this, AdminDashboardActivity::class.java)
            startActivity(intent)
        }

        // 3. Configuração da Bottom Navigation Listener (4 Itens: Livraria, Notícias, Chatbot, Perfil)
        bottomNavigation.setOnItemSelectedListener { item ->
            // Define a classe de destino com base no ID do item do menu
            val activityClass = when (item.itemId) {
                R.id.nav_livraria -> null // Já estamos na Home Admin
                R.id.nav_noticias -> Adm_Tela_Mural_Noticias_Eventos::class.java
                R.id.nav_chatbot -> Tela_Chat_Bot::class.java
                R.id.nav_perfil -> Adm_Tela_De_Perfil::class.java
                else -> null
            }

            if (activityClass != null) {
                val intent = Intent(this, activityClass).apply {
                    // Usando FLAG_ACTIVITY_CLEAR_TOP | NEW_TASK para evitar empilhamento desnecessário
                    // e garantir que a Activity de destino seja a raiz daquela navegação.
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
                return@setOnItemSelectedListener true
            }
            return@setOnItemSelectedListener false
        }

        // 4. Configuração Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * Garante que o item "Livraria" (a tela atual) esteja sempre marcado ao retornar a esta Activity.
     */
    override fun onResume() {
        super.onResume()
        bottomNavigation.menu.findItem(R.id.nav_livraria).isChecked = true
    }
}
