package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Tela_Login : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var senhaEditText: EditText

    // Dados de Login Fictícios para Simulação
    private val ADMIN_EMAIL = "adm@gmail.com"
    private val ADMIN_SENHA = "adm123"
    private val ALUNO_EMAIL = "aluno@gmail.com"
    private val ALUNO_SENHA = "aluno123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- 1. Conexão dos campos de E-mail e Senha ---
        // Certifique-se que esses IDs (edit_text_email, edit_text_password) existem no seu XML
        emailEditText = findViewById(R.id.edit_text_email)
        senhaEditText = findViewById(R.id.edit_text_password)

        // --- 2. Conexão e Lógica do botão de LOGIN ---
        val loginButton: Button = findViewById(R.id.button_login)

        loginButton.setOnClickListener {
            handleLogin()
        }

        // --- 3. Conexão do link "Esqueci minha senha" ---
        val forgotPasswordLink: TextView = findViewById(R.id.text_view_forgot_password)

        forgotPasswordLink.setOnClickListener {
            val intent = Intent(this, Tela_Esquecer_Senha::class.java)
            startActivity(intent)
        }
    }

    /**
     * Função que contém a lógica de autenticação (Admin vs. Aluno) e redirecionamento
     */
    private fun handleLogin() {
        val email = emailEditText.text.toString().trim()
        val senha = senhaEditText.text.toString()

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            return
        }

        // LÓGICA DE LOGIN: Verifica as credenciais e redireciona
        if (email == ADMIN_EMAIL && senha == ADMIN_SENHA) {
            // Se for Admin, abre a tela de Cadastro de Livro
            Toast.makeText(this, "Login de Administrador bem-sucedido!", Toast.LENGTH_LONG).show()
            val intent = Intent(this, Adm_Tela_Central_Livraria::class.java)
            startActivity(intent)
            finish()

        } else if (email == ALUNO_EMAIL && senha == ALUNO_SENHA) {
            // Se for Aluno, abre a tela Central da Livraria
            Toast.makeText(this, "Login de Aluno bem-sucedido!", Toast.LENGTH_LONG).show()
            val intent = Intent(this, Tela_Central_Livraria::class.java)
            startActivity(intent)
            finish()

        } else {
            // Credenciais inválidas
            Toast.makeText(this, "E-mail ou Senha incorretos.", Toast.LENGTH_SHORT).show()
        }
    }
}