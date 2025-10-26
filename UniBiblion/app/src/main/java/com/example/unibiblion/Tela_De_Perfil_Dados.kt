package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView

class Tela_De_Perfil_Dados : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_de_perfil_dados)

        val iconNotification = findViewById<ImageView>(R.id.icon_notification)
        val iconMenu = findViewById<ImageView>(R.id.icon_menu)
        val profileImageContainer = findViewById<CardView>(R.id.profile_image_container)
        val buttonEditarNome = findViewById<Button>(R.id.button_editar_nome)
        val buttonEditarEmail = findViewById<Button>(R.id.button_editar_email)
        val buttonTrocarSenha = findViewById<Button>(R.id.button_trocar_senha)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        iconNotification.setOnClickListener {
            startActivity(Intent(this, Tela_Notificacoes::class.java))
        }

        iconMenu.setOnClickListener {
            showPopupMenu(it)
        }

        profileImageContainer.setOnClickListener {
            startActivity(Intent(this, Tela_De_Perfil_Foto::class.java))
        }

        buttonEditarNome.setOnClickListener {
            startActivity(Intent(this, Tela_De_Perfil_Nome::class.java))
        }

        buttonEditarEmail.setOnClickListener {
            startActivity(Intent(this, Tela_De_Perfil_Email::class.java))
        }

        buttonTrocarSenha.setOnClickListener {
            startActivity(Intent(this, Tela_Trocar_Senha_Via_Perfil::class.java))
        }

        bottomNavigation.selectedItemId = R.id.nav_perfil

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_livraria-> {
                    startActivity(Intent(this, Tela_Central_Livraria::class.java))
                    finish()
                    true
                }
                R.id.nav_noticias -> {
                    startActivity(Intent(this, NoticiasActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_perfil -> {
                    startActivity(Intent(this, Tela_De_Perfil::class.java))
                    finish()
                    true
                }
                R.id.nav_chatbot -> {
                    startActivity(Intent(this, Tela_Chat_Bot::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.menu_perfil_opcoes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_editar_perfil -> {
                    startActivity(Intent(this, Tela_De_Perfil_Dados::class.java))
                    true
                }
                R.id.action_acessibilidade -> {
                    startActivity(Intent(this, Tela_Acessibilidade::class.java))
                    true
                }
                R.id.action_configuracoes_gerais -> {
                    startActivity(Intent(this, Tela_Config_geral::class.java))
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
}