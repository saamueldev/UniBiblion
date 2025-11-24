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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Adm_Tela_De_Perfil : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var profileImage: ImageView
    private lateinit var textName: TextView
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adm_tela_de_perfil)

        setupUI()
        setupListeners()

        loadAdminProfile()
    }


    private fun loadAdminProfile() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.w("AdmAuth", "Nenhum administrador logado.")
            Toast.makeText(this, "Administrador não autenticado.", Toast.LENGTH_LONG).show()
            return
        }

        val userId = currentUser.uid
        db.collection("usuarios").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val name = document.getString("nome") ?: "Nome não encontrado"
                    val profileImageUrl = document.getString("profileImageUrl")

                    textName.text = name

                    if (!profileImageUrl.isNullOrEmpty()) {
                        Log.d("AdmGlide", "URL da imagem encontrada: $profileImageUrl")
                        Glide.with(this)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.ic_profile)
                            .error(R.drawable.ic_profile)
                            .circleCrop()
                            .into(profileImage)
                    } else {
                        Log.w("AdmGlide", "URL da foto de perfil está vazia ou nula.")
                        profileImage.setImageResource(R.drawable.ic_profile)
                    }
                } else {
                    Log.w("AdmFirestore", "Documento do admin não foi encontrado.")
                    textName.text = "Perfil não encontrado"
                    profileImage.setImageResource(R.drawable.ic_profile)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AdmFirestore", "Erro ao buscar perfil: ", exception)
                Toast.makeText(this, "Falha ao carregar perfil do admin.", Toast.LENGTH_SHORT).show()
                textName.text = "Erro ao carregar"
                profileImage.setImageResource(R.drawable.ic_profile)
            }
    }


    private fun setupUI() {
        profileImage = findViewById(R.id.profile_image)
        textName = findViewById(R.id.text_name)
        bottomNavigationView = findViewById(R.id.bottom_navigation_bar)
    }

    private fun setupListeners() {
        setupHeaderAndMenu()
        setupBottomNavigation()
    }

    private fun setupHeaderAndMenu() {
        findViewById<ImageView>(R.id.icon_bell).setOnClickListener {
            startActivity(Intent(this, Tela_Notificacoes::class.java))
        }

        findViewById<ImageView>(R.id.icon_menu).setOnClickListener { view ->
            showPopupMenu(view)
        }

        findViewById<ImageView>(R.id.profile_image).setOnClickListener {
            startActivity(Intent(this, Tela_De_Perfil_Dados::class.java))
        }
    }

    private fun showPopupMenu(view: View) {
        PopupMenu(this, view).apply {
            menuInflater.inflate(R.menu.adm_menu_perfil, menu)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_editar_perfil -> {
                        startActivity(Intent(this@Adm_Tela_De_Perfil, Tela_De_Perfil_Dados::class.java))
                        true
                    }
                    R.id.action_acessar_perfil -> {
                        startActivity(Intent(this@Adm_Tela_De_Perfil, Adm_Tela_Procurar_Usuario::class.java))
                        true
                    }
                    else -> false
                }
            }
            show()
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.selectedItemId = R.id.nav_perfil

        bottomNavigationView.setOnItemSelectedListener { item ->
            val intent = when (item.itemId) {
                R.id.nav_livraria -> Intent(this, Adm_Tela_Central_Livraria::class.java)
                R.id.nav_noticias -> Intent(this, NoticiasActivity::class.java)
                R.id.nav_chatbot -> Intent(this, Tela_Chat_Bot::class.java)
                R.id.nav_perfil -> null
                else -> null
            }

            if (intent != null) {
                startActivity(intent)
                finish()
            }

            return@setOnItemSelectedListener intent != null || item.itemId == R.id.nav_perfil
        }
    }
}
