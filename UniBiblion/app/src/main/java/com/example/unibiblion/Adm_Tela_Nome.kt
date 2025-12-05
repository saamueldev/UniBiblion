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

class Adm_Tela_Nome : AppCompatActivity() {

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

    private fun salvarNovoNome(novoNome: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Erro: Usuário não autenticado.", Toast.LENGTH_SHORT).show()
            return
        }

        buttonSalvar.isEnabled = false

        val userDocRef = firestore.collection("usuarios").document(userId)
        val updates = hashMapOf<String, Any>(
            "nome" to novoNome
        )

        userDocRef.update(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Nome atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                // Falha!
                Toast.makeText(this, "Erro ao atualizar o nome: ${e.message}", Toast.LENGTH_LONG).show()
                buttonSalvar.isEnabled = true
            }
    }

    private fun setupBottomNavigation(bottomNavigation: BottomNavigationView) {
        bottomNavigation.selectedItemId = R.id.nav_perfil
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_livraria -> {
                    startActivity(Intent(this, Adm_Tela_Central_Livraria::class.java))
                    finish()
                    true
                }
                R.id.nav_noticias -> {
                    startActivity(Intent(this, Adm_Tela_Mural_Noticias_Eventos::class.java))
                    finish()
                    true
                }
                R.id.nav_perfil -> {
                    startActivity(Intent(this, Adm_Tela_De_Perfil::class.java))
                    finish()
                    true
                }
                R.id.nav_chatbot -> {
                    startActivity(Intent(this, Tela_Adm_Chat_Bot::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
