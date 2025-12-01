package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Tela_De_Perfil_Email : AppCompatActivity() {

    // 1. Instâncias do Firebase
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private lateinit var editNovoEmail: EditText
    private lateinit var buttonSalvar: Button
    private var emailAtual: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_de_perfil_email)

        editNovoEmail = findViewById(R.id.edit_novo_email)
        buttonSalvar = findViewById(R.id.button_salvar_email)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation_bar)

        carregarEmailAtual()

        buttonSalvar.setOnClickListener {
            val novoEmail = editNovoEmail.text.toString().trim()
            validarEAtualizarEmail(novoEmail)
        }

        setupBottomNavigation(bottomNavigation)
    }

    private fun carregarEmailAtual() {
        emailAtual = auth.currentUser?.email
        editNovoEmail.setText(emailAtual)
    }

    private fun validarEAtualizarEmail(novoEmail: String) {
        if (novoEmail.equals(emailAtual, ignoreCase = true)) {
            Toast.makeText(this, "O novo e-mail não pode ser igual ao atual.", Toast.LENGTH_LONG).show()
            return
        }

        if (novoEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(novoEmail).matches()) {
            Toast.makeText(this, "Por favor, insira um e-mail válido.", Toast.LENGTH_SHORT).show()
            return
        }

        atualizarEmailNoFirebase(novoEmail)
    }

    private fun atualizarEmailNoFirebase(novoEmail: String) {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Erro: Usuário não autenticado.", Toast.LENGTH_SHORT).show()
            return
        }

        buttonSalvar.isEnabled = false

        user.verifyBeforeUpdateEmail(novoEmail)
            .addOnSuccessListener {
                atualizarEmailNoFirestore(novoEmail, user.uid)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Falha ao enviar verificação: ${exception.message}", Toast.LENGTH_LONG).show()
                buttonSalvar.isEnabled = true
            }
    }

    private fun atualizarEmailNoFirestore(novoEmail: String, userId: String) {
        val userDocRef = firestore.collection("usuarios").document(userId)
        userDocRef.update("email", novoEmail)
            .addOnSuccessListener {
                Toast.makeText(this, "Um e-mail de verificação foi enviado para $novoEmail.", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ocorreu um erro ao atualizar o banco de dados. Tente novamente.", Toast.LENGTH_LONG).show()
                buttonSalvar.isEnabled = true
            }
    }

    private fun setupBottomNavigation(bottomNavigation: BottomNavigationView) {
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
                    startActivity(Intent(this, Tela_De_Perfil_Dados::class.java))
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
