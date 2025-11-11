package com.example.unibiblion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView

class Adm_Tela_De_Permissoes : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.adm_tela_permissoes)

        val btnSalvar = findViewById<Button>(R.id.buttonSalvar)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val checkCadastrarLivro = findViewById<CheckBox>(R.id.check_cadastrar_livro)

        bottomNav.menu.findItem(R.id.nav_perfil)?.isChecked = true

        btnSalvar.setOnClickListener {
            val permissaoLivro = checkCadastrarLivro.isChecked
            val mensagem = if (permissaoLivro) {
                "Permissões salvas! (Cadastrar Livro Ativo)"
            } else {
                "Permissões salvas! (Cadastrar Livraro Inativo)"
            }
            Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_livraria -> {
                    startActivity(Intent(this, Adm_Tela_Central_Livraria::class.java))
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
                    startActivity(Intent(this, Adm_Tela_De_Perfil::class.java))
                    true
                }
                else -> false
            }
        }
    }
}