package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class Adm_Tela_Perfil_Usuario : AppCompatActivity() {

    // 1. Declarar a instância do Firestore e uma variável para o ID do usuário
    private val db = FirebaseFirestore.getInstance()
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // O layout usado parece ser 'activity_tela_de_perfil', mantive ele.
        setContentView(R.layout.activity_tela_de_perfil)

        // 2. Captura o ID do usuário passado pela Intent da tela anterior
        userId = intent.getStringExtra("USER_ID")

        if (userId == null) {
            Toast.makeText(this, "Erro: ID do usuário não encontrado.", Toast.LENGTH_LONG).show()
            finish() // Fecha a activity se não houver ID
            return
        }

        fetchAndDisplayUserData()
        setupHeaderClicks() // Mantém a mesma navegação superior
        setupBottomNavigation() // Mantém a mesma navegação inferior
    }

    private fun fetchAndDisplayUserData() {
        db.collection("usuarios").document(userId!!)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val usuario = document.toObject(Usuario::class.java)

                    findViewById<TextView>(R.id.text_name)?.text = usuario?.nome

                    val profileImageView = findViewById<ImageView>(R.id.profile_image)
                    Glide.with(this)
                        .load(usuario?.fotoUrl) // 'fotoUrl' é o nome do campo na sua classe Usuario.kt
                        .placeholder(R.drawable.ic_profile) // Imagem padrão
                        .error(R.drawable.ic_profile) // Imagem em caso de erro
                        .circleCrop() // Deixa a imagem redonda
                        .into(profileImageView)

                } else {
                    Toast.makeText(this, "Usuário não encontrado.", Toast.LENGTH_SHORT).show()
                    Log.d("Firestore", "Nenhum documento encontrado com o ID: $userId")
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Falha ao buscar dados do usuário.", Toast.LENGTH_SHORT).show()
                Log.e("Firestore", "Erro ao buscar usuário", exception)
            }
    }


    private fun setupHeaderClicks() {
        findViewById<ImageView>(R.id.icon_bell).setOnClickListener {
            startActivity(Intent(this, Adm_Tela_Notificacoes::class.java))
        }
        val menuIcon = findViewById<ImageView>(R.id.icon_menu)
        menuIcon.setOnClickListener { view ->
            showPopupMenu(view)
        }
        findViewById<ImageView>(R.id.profile_image).setOnClickListener {
        }
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        // Verifica se o menu a ser inflado é o correto para esta tela
        popup.menuInflater.inflate(R.menu.adm_menu_perfil, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_editar_perfil -> {
                    val intent = Intent(this, Tela_De_Perfil_Dados::class.java).apply {
                        putExtra("USER_ID", userId)
                    }
                    startActivity(intent)
                    true
                }
                R.id.action_acessar_perfil -> {
                    Toast.makeText(this, "Você já está na tela de perfil do usuário.", Toast.LENGTH_SHORT).show()
                    true
                }
                // ... outros itens do menu
                else -> false
            }
        }
        popup.show()
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_bar)
        // É uma boa prática não ter um item selecionado se esta tela é acessada de fora do fluxo principal
        // bottomNavigationView.selectedItemId = R.id.nav_perfil

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_livraria -> {
                    startActivity(Intent(this, Adm_Tela_Central_Livraria::class.java))
                    true
                }
                R.id.nav_noticias -> {
                    startActivity(Intent(this, Adm_Tela_Mural_Noticias_Eventos::class.java))
                    true
                }
                R.id.nav_chatbot -> {
                    startActivity(Intent(this, Tela_Adm_Chat_Bot::class.java))
                    true
                }
                R.id.nav_perfil -> {
                    startActivity(Intent(this, Adm_Tela_De_Perfil::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
