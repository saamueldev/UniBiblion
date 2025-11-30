package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Adm_Dados_Perfil : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private lateinit var profileImage: ImageView
    private lateinit var textUserName: TextView
    private lateinit var labelEmail: TextView
    private lateinit var labelNomeCompleto: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_de_perfil_dados)

        profileImage = findViewById(R.id.profile_image)
        textUserName = findViewById(R.id.text_user_name)
        labelEmail = findViewById(R.id.label_email)
        labelNomeCompleto = findViewById(R.id.label_nome_completo)

        val iconNotification = findViewById<ImageView>(R.id.icon_notification)
        val iconMenu = findViewById<ImageView>(R.id.icon_menu)
        val buttonEditarNome = findViewById<Button>(R.id.button_editar_nome)
        val buttonEditarEmail = findViewById<Button>(R.id.button_editar_email)
        val buttonTrocarSenha = findViewById<Button>(R.id.button_trocar_senha)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        iconNotification.setOnClickListener {
            startActivity(Intent(this, Tela_Notificacoes::class.java))
        }
        iconMenu.setOnClickListener { showPopupMenu(it) }

        profileImage.setOnClickListener {
            startActivity(Intent(this, Tela_De_Perfil_Foto::class.java))
        }

        buttonEditarNome.setOnClickListener {
            startActivity(Intent(this, Tela_De_Perfil_Nome::class.java))
        }
        buttonEditarEmail.setOnClickListener {
            startActivity(Intent(this, Tela_De_Perfil_Email::class.java))
        }
        buttonTrocarSenha.setOnClickListener {
            startActivity(Intent(this, Tela_Trocar_Senha_Via_Perfil::class.java))
        }

        setupBottomNavigation(bottomNavigation)
    }

    override fun onResume() {
        super.onResume()
        carregarDadosDoUsuario()
    }

    private fun carregarDadosDoUsuario() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val userId = user.uid

        firestore.collection("usuarios").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nome = document.getString("nome") ?: "Nome não encontrado"
                    val email = document.getString("email") ?: user.email
                    val imageUrl = document.getString("profileImageUrl")

                    textUserName.text = nome
                    labelEmail.text = email
                    labelNomeCompleto.text = nome

                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(imageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .placeholder(R.drawable.ic_profile)
                            .error(R.drawable.ic_profile)
                            .circleCrop()
                            .into(profileImage)
                    } else {
                        profileImage.setImageResource(R.drawable.ic_profile)
                    }
                } else {
                    Log.w("PerfilDados", "Documento do usuário não encontrado no Firestore.")
                    textUserName.text = "Usuário"
                    labelEmail.text = user.email ?: "E-mail não encontrado"
                    labelNomeCompleto.text = "Nome não encontrado" // << VALOR PADRÃO
                    profileImage.setImageResource(R.drawable.ic_profile)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("PerfilDados", "Erro ao buscar dados do Firestore", exception)
                Toast.makeText(this, "Erro ao carregar dados.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.menu_perfil_opcoes, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_editar_perfil -> { true }
                R.id.action_acessibilidade -> {
                    startActivity(Intent(this, Tela_Acessibilidade::class.java))
                    true
                }
                R.id.action_configuracoes_gerais -> {
                    startActivity(Intent(this, Tela_Config_geral::class.java))
                    true
                }
                else -> false
            }
        }
        popup.show()
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