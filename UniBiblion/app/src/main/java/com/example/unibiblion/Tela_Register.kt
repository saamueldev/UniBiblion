package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class Tela_Register : AppCompatActivity() {

    private val TAG = "Tela_Register"

    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var confirmPasswordErrorTextView: TextView
    private lateinit var db: FirebaseFirestore

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

        val nameEditText: EditText = findViewById(R.id.edit_text_name)
        val emailEditText: EditText = findViewById(R.id.edit_text_email)
        passwordEditText = findViewById(R.id.edit_text_password)
        confirmPasswordEditText = findViewById(R.id.edit_text_confirm_password)
        confirmPasswordErrorTextView = findViewById(R.id.text_view_confirm_password_error)

        db = Firebase.firestore

        confirmPasswordEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateConfirmPassword()
            } else {
                clearConfirmPasswordError()
            }
        }

        val registerButton: Button = findViewById(R.id.button_avancar)

        registerButton.setOnClickListener {
            handleRegister(nameEditText, emailEditText, passwordEditText, confirmPasswordEditText)
        }
    }

    private fun validateConfirmPassword(): Boolean {
        val senha = passwordEditText.text.toString()
        val confirmarSenha = confirmPasswordEditText.text.toString()

        if (senha != confirmarSenha) {
            confirmPasswordEditText.setBackgroundResource(DRAWABLE_BORDER_ERROR)
            confirmPasswordErrorTextView.visibility = View.VISIBLE
            return false
        } else {
            clearConfirmPasswordError()
            return true
        }
    }

    private fun clearConfirmPasswordError() {
        confirmPasswordEditText.setBackgroundResource(DRAWABLE_BORDER_NORMAL)
        confirmPasswordErrorTextView.visibility = View.GONE
    }

    private fun isPasswordValid(password: String): Boolean {
        if (password.length < 6 || password.length > 16) return false
        val hasLetters = password.any { it.isLetter() }
        val hasDigits = password.any { it.isDigit() }
        return hasLetters && hasDigits
    }

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

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isPasswordValid(senha)) {
            val message = "A senha deve ter entre 6 e 16 dígitos e conter letras e números."
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            return
        }

        if (!validateConfirmPassword()) {
            Toast.makeText(this, "As senhas não coincidem.", Toast.LENGTH_SHORT).show()
            return
        }

        checkEmailUniqueness(nome, email, senha)
    }

    private fun checkEmailUniqueness(nome: String, email: String, senha: String) {
        db.collection("usuarios")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    saveUserToFirestore(nome, email, senha)
                } else {
                    Toast.makeText(this, "Este email já está cadastrado.", Toast.LENGTH_LONG).show()
                    Log.w(TAG, "Tentativa de cadastro com email duplicado: $email")
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao verificar email. Tente novamente.", Toast.LENGTH_LONG).show()
                Log.e(TAG, "Erro ao consultar unicidade do email", e)
            }
    }

    private fun saveUserToFirestore(nome: String, email: String, senha: String) {
        val user = hashMapOf(
            "nome" to nome,
            "email" to email,
            "senha" to senha
        )

        db.collection("usuarios")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Documento do usuário salvo com ID aleatório: ${documentReference.id}")
                Toast.makeText(this, "Cadastro realizado com sucesso! Faça login.", Toast.LENGTH_LONG).show()

                val intent = Intent(this, Tela_Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Erro ao salvar documento no Firestore", e)
                Toast.makeText(this, "Erro ao cadastrar. Verifique a conexão e as regras do Firebase. Erro: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}