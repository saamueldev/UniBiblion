package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class Tela_De_Perfil_Foto : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_de_perfil_foto)

        // Inicialização dos Views
        val buttonEscolherFoto = findViewById<Button>(R.id.button_escolher_foto)
        val buttonSalvar = findViewById<Button>(R.id.button_salvar)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        buttonEscolherFoto.setOnClickListener {
            Toast.makeText(this, "Abrir Galeria/Câmera", Toast.LENGTH_SHORT).show()
        }

        buttonSalvar.setOnClickListener {
            Toast.makeText(this, "Salvando a nova foto...", Toast.LENGTH_SHORT).show()

            finish()
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
}