package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Tela_Register : AppCompatActivity() {

    // Variáveis para os campos necessários na lógica de foco
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var confirmPasswordErrorTextView: TextView

    // IDs de Drawables (ASUMIDOS: Você deve ter estes Drawables em res/drawable)
    private val DRAWABLE_BORDER_NORMAL = R.drawable.rounded_edittext_background_white
    private val DRAWABLE_BORDER_ERROR = R.drawable.rounded_edittext_background_red

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Conexão dos campos de entrada e Label de Erro
        val nameEditText: EditText = findViewById(R.id.edit_text_name)
        val emailEditText: EditText = findViewById(R.id.edit_text_email)
        passwordEditText = findViewById(R.id.edit_text_password)
        confirmPasswordEditText = findViewById(R.id.edit_text_confirm_password)
        confirmPasswordErrorTextView = findViewById(R.id.text_view_confirm_password_error)


        // 2. Implementação da validação "ao sair do campo" (onFocusChange) - RF01.02
        confirmPasswordEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                // Quando o campo de confirmação perde o foco, valide
                validateConfirmPassword()
            } else {
                // Quando o campo recebe foco, limpe o erro
                clearConfirmPasswordError()
            }
        }

        // 3. Conecta o botão de Cadastro/Avançar
        val registerButton: Button = findViewById(R.id.button_avancar)

        registerButton.setOnClickListener {
            handleRegister(nameEditText, emailEditText, passwordEditText, confirmPasswordEditText)
        }
    }

    /**
     * Verifica se a senha de confirmação é igual à senha original (RF01.02)
     */
    private fun validateConfirmPassword(): Boolean {
        val senha = passwordEditText.text.toString()
        val confirmarSenha = confirmPasswordEditText.text.toString()

        if (senha != confirmarSenha) {
            // Exibir erro (borda vermelha e label)
            confirmPasswordEditText.setBackgroundResource(DRAWABLE_BORDER_ERROR)
            confirmPasswordErrorTextView.visibility = View.VISIBLE
            return false
        } else {
            // Limpar erro
            clearConfirmPasswordError()
            return true
        }
    }

    /**
     * Limpa o estado de erro do campo de confirmação de senha.
     */
    private fun clearConfirmPasswordError() {
        confirmPasswordEditText.setBackgroundResource(DRAWABLE_BORDER_NORMAL)
        confirmPasswordErrorTextView.visibility = View.GONE
    }

    /**
     * Verifica se a senha atende aos requisitos: 6-16 dígitos, alfanumérica.
     */
    private fun isPasswordValid(password: String): Boolean {
        // 1. Comprimento
        if (password.length < 6 || password.length > 16) return false

        // 2. Caracteres Alfanuméricos
        val hasLetters = password.any { it.isLetter() }
        val hasDigits = password.any { it.isDigit() }
        return hasLetters && hasDigits
    }

    /**
     * Função que contém a lógica principal de validação e navegação do registro.
     */
    private fun handleRegister(
        nameField: EditText,
        emailField: EditText,
        passField: EditText,
        confirmPassField: EditText
    ) {
        val nome = nameField.text.toString().trim()
        val email = emailField.text.toString().trim()
        val senha = passField.text.toString()
        val confirmarSenha = confirmPassField.text.toString()

        // 1. Validação de campos vazios (RF01.02)
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Validação dos Requisitos da Senha
        if (!isPasswordValid(senha)) {
            val message = "A senha deve ter entre 6 e 16 dígitos e conter letras e números."
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            return
        }

        // 3. Validação de Senhas Iguais (Garantia no clique do botão)
        if (!validateConfirmPassword()) {
            Toast.makeText(this, "As senhas não coincidem.", Toast.LENGTH_SHORT).show()
            return
        }

        // 4. Lógica de Validação bem-sucedida:

        Toast.makeText(this, "Cadastro realizado com sucesso! Faça login.", Toast.LENGTH_LONG).show()
        val intent = Intent(this, Tela_Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}