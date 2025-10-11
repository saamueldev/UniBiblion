package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Tela_Login_Resgister : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // CORREÇÃO: Usando o nome exato do seu arquivo XML
        setContentView(R.layout.activity_tela_login_resgister)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Botão "Entrar" (sign_in) -> Abre Tela_Login
        val signInButton: Button = findViewById(R.id.button_sign_in)
        signInButton.setOnClickListener {
            val intent = Intent(this, Tela_Login::class.java)
            startActivity(intent)
        }

        // 2. Botão "Cadastrar" (sign_up) -> Abre Tela_Register
        val signUpButton: Button = findViewById(R.id.button_sign_up)
        signUpButton.setOnClickListener {
            val intent = Intent(this, Tela_Register::class.java)
            startActivity(intent)
        }
    }
}