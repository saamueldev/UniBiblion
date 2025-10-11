package com.example.unibiblion

import android.os.Bundle
import android.widget.Button // Importar o Button
import android.widget.Toast // Importar o Toast (para mensagens temporárias)
import androidx.activity.enableEdgeToEdge // Apenas esta linha é necessária
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
// REMOVIDA: import androidx.core.view.WindowCompat.enableEdgeToEdge  (Esta linha causava o erro!)
import androidx.core.view.WindowInsetsCompat

// Nome da Activity corrigido
class Tela_Login_Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_login_resgister) // Assumindo que este é o nome do seu layout XML

        // Configuração de Insets (para barras de sistema)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Encontrar o botão "Entrar" (Sign In)
        val signInButton: Button = findViewById(R.id.button_sign_in)
        signInButton.setOnClickListener {
            // Ação ao clicar em "Entrar"
            Toast.makeText(this, "Redirecionando para Login...", Toast.LENGTH_SHORT).show()
        }

        // 2. Encontrar o botão "Cadastrar" (Sign Up)
        val signUpButton: Button = findViewById(R.id.button_sign_up)
        signUpButton.setOnClickListener {
            // Ação ao clicar em "Cadastrar"
            Toast.makeText(this, "Redirecionando para Cadastro...", Toast.LENGTH_SHORT).show()
        }
    }
}