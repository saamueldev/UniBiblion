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

class Tela_De_Perfil_Nome : AppCompatActivity() {

    // 1. Instâncias do Firebase
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private lateinit var editNovoNome: EditText
    private lateinit var buttonSalvar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_de_perfil_nome)

        editNovoNome = findViewById(R.id.edit_novo_nome)
        buttonSalvar = findViewById(R.id.button_salvar_nome)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // 2. Lógica de clique atualizada
        buttonSalvar.setOnClickListener {
            val novoNome = editNovoNome.text.toString().trim()

            if (novoNome.isNotEmpty()) {
                // Inicia o processo de salvar no Firestore
                salvarNovoNome(novoNome)
            } else {
                Toast.makeText(this, "O nome não pode ser vazio.", Toast.LENGTH_SHORT).show()
            }
        }

        setupBottomNavigation(bottomNavigation)
    }

    // 3. Nova função para interagir com o Firestore
    private fun salvarNovoNome(novoNome: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Erro: Usuário não autenticado.", Toast.LENGTH_SHORT).show()
            return
        }

        // Desativa o botão para evitar cliques duplos durante o salvamento
        buttonSalvar.isEnabled = false

        val userDocRef = firestore.collection("usuarios").document(userId)
        val updates = hashMapOf<String, Any>(
            "nome" to novoNome
        )

        userDocRef.update(updates)
            .addOnSuccessListener {
                // Sucesso!
                Toast.makeText(this, "Nome atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                finish() // Volta para a tela anterior
            }
            .addOnFailureListener { e ->
                // Falha!
                Toast.makeText(this, "Erro ao atualizar o nome: ${e.message}", Toast.LENGTH_LONG).show()
                // Reativa o botão em caso de falha
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
                    // Importante: Voltar para Tela_De_Perfil_Dados para ver a mudança
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
