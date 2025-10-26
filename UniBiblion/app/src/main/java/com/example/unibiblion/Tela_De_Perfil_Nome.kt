package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class Tela_De_Perfil_Nome : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_de_perfil_nome)

        val editNovoNome = findViewById<EditText>(R.id.edit_novo_nome)
        val buttonSalvar = findViewById<Button>(R.id.button_salvar_nome)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        buttonSalvar.setOnClickListener {
            val novoNome = editNovoNome.text.toString().trim()

            if (novoNome.isNotEmpty()) {
                Toast.makeText(this, "Nome salvo: $novoNome", Toast.LENGTH_SHORT).show()

                finish()
            } else {
                Toast.makeText(this, "O nome não pode ser vazio.", Toast.LENGTH_SHORT).show()
            }
        }
        bottomNavigation.selectedItemId = R.id.nav_perfil

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_livraria -> {
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
}