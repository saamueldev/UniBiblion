package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView // Necessário para o link TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Tela_Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Conexão do botão de LOGIN (Entrar)
        // O ID do XML é: button_login
        val loginButton: Button = findViewById(R.id.button_login)

        loginButton.setOnClickListener {
            // SIMULANDO RETORNO: Fecha a Tela_Login e volta para a Tela_Login_Register (que a chamou)
            finish()
        }

        // 2. Conexão do link "Esqueci minha senha"
        // O ID do XML é: text_view_forgot_password
        val forgotPasswordLink: TextView = findViewById(R.id.text_view_forgot_password)

        forgotPasswordLink.setOnClickListener {
            // Abre o primeiro passo do fluxo de recuperação de senha
            val intent = Intent(this, Tela_Esquecer_Senha::class.java)
            startActivity(intent)
        }
    }
}