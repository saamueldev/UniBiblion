package com.example.unibiblion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.PopupMenu
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView

class Adm_Tela_Perfil_Usuario : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_de_perfil)

        populateProfileData()
        setupHeaderClicks()
        setupBottomNavigation()
    }

    private fun populateProfileData() {
        val nomeMock = "Jonh Henrique"
        val bioMock = "O que um leitor quer, um leitor tem."

        findViewById<TextView>(R.id.text_name)?.let {
            it.text = nomeMock
        }

    }

    private fun setupHeaderClicks() {
        findViewById<ImageView>(R.id.icon_bell).setOnClickListener {
            startActivity(Intent(this, Tela_Notificacoes::class.java))
        }
        val menuIcon = findViewById<ImageView>(R.id.icon_menu)
        menuIcon.setOnClickListener { view ->
            showPopupMenu(view)
        }
        findViewById<ImageView>(R.id.profile_image).setOnClickListener {
            startActivity(Intent(this, Tela_De_Perfil_Dados::class.java))
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
                R.id.action_acessar_perfil -> {
                    Toast.makeText(this, "Abrindo visualização do Perfil", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.action_gerenciar_notificacoes -> {
                    startActivity(Intent(this, Tela_Notificacoes::class.java))
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
                R.id.action_permissoes -> {
                    startActivity(Intent(this, Adm_Tela_De_Permissoes::class.java))
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_bar)
        bottomNavigationView.selectedItemId = R.id.nav_perfil

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_livraria -> {
                    startActivity(Intent(this, Tela_Central_Livraria::class.java))
                    true
                }
                R.id.nav_noticias -> {
                    startActivity(Intent(this, NoticiasActivity::class.java))
                    true
                }
                R.id.nav_chatbot -> {
                    startActivity(Intent(this, Tela_Chat_Bot::class.java))
                    true
                }
                R.id.nav_perfil -> {
                    true
                }
                else -> false
            }
        }
    }
}