package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

class Tela_Login : AppCompatActivity() {

    private val TAG = "Tela_Login"
    private lateinit var emailEditText: EditText
    private lateinit var senhaEditText: EditText
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        emailEditText = findViewById(R.id.edit_text_email)
        senhaEditText = findViewById(R.id.edit_text_password)
        db = Firebase.firestore

        val loginButton: Button = findViewById(R.id.button_login)
        loginButton.setOnClickListener {
            handleLogin()
        }

        val forgotPasswordLink: TextView = findViewById(R.id.text_view_forgot_password)
        forgotPasswordLink.setOnClickListener {
            val intent = Intent(this, Tela_Esquecer_Senha::class.java)
            startActivity(intent)
        }
    }

    private fun handleLogin() {
        val email = emailEditText.text.toString().trim()
        val senha = senhaEditText.text.toString().trim()

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            return
        }

        authenticateUser(email, senha)
    }

    private fun authenticateUser(email: String, senha: String) {
        db.collection("usuarios")
            .whereEqualTo("email", email)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    // 🚨 E-mail não encontrado -> Toast
                    Toast.makeText(this, "Dados incorretos", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val document = querySnapshot.documents[0]
                val storedPassword = document.getString("senha")?.trim()

                val isAdmin = document.getBoolean("admin") == true

                Log.d(TAG, "Valor lido para 'admin': ${document.getBoolean("admin")}")
                Log.d(TAG, "Resultado da verificação isAdmin: $isAdmin")

                if (senha == storedPassword) {
                    Toast.makeText(this, "Login efetuado com sucesso!", Toast.LENGTH_SHORT).show()

                    if (isAdmin) {
                        Log.i(TAG, "Login de Administrador.")
                        val intent = Intent(this, Adm_Tela_Central_Livraria::class.java)
                        startActivity(intent)
                    } else {
                        Log.i(TAG, "Login de Usuário Comum.")
                        val intent = Intent(this, Tela_Central_Livraria::class.java)
                        startActivity(intent)
                    }
                    finish()

                } else {
                    // 🚨 Senha incorreta -> Toast
                    Toast.makeText(this, "Dados incorretos", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                // Trata falha na comunicação com o Firestore
                Log.e(TAG, "Erro ao tentar login: ${e.message}", e)
                Toast.makeText(this, "Erro de conexão. Tente novamente.", Toast.LENGTH_LONG).show()
            }
    }
}