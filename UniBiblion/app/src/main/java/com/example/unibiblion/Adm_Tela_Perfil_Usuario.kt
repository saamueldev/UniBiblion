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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query



class Adm_Tela_Perfil_Usuario : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    // ID do usuário que o ADM está inspecionando. Recebido via Intent.
    private var userId: String? = null

    private lateinit var profileImageView: ImageView
    private lateinit var userNameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContentView(R.layout.activity_adm_tela_perfil_usuario)

            // 1. Recebe o ID do usuário que o administrador deseja visualizar
            userId = intent.getStringExtra("USER_ID")

            if (userId == null) {
                Toast.makeText(this, "Erro: ID do usuário não encontrado para visualização.", Toast.LENGTH_LONG).show()
                finish()
                return
            }

            profileImageView = findViewById(R.id.profile_image)
            userNameTextView = findViewById(R.id.text_name)

            setupRecyclerViews() // ✨ Agora configura como grade de 4 colunas
            populateProfileData()
            loadBookSections()
            setupHeaderClicks()
            setupBottomNavigation()

        } catch (e: Exception) {
            Log.e("AdmPerfilCrash", "Erro FATAL em onCreate: ${e.message}", e)
            Toast.makeText(this, "Erro fatal ao abrir tela. Verifique o Logcat.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    /**
     * Configura o GridLayoutManager para as listas de livros,
     * permitindo 4 itens por linha.
     */
    private fun setupRecyclerViews() {
        // Define que 4 livros devem caber em cada linha (spanCount)
        val spanCount = 4

        // --- Livros Alugados ---
        findViewById<RecyclerView>(R.id.recycler_view_alugados).apply {
            // Usa GridLayoutManager para exibir os itens em grade (4 colunas)
            layoutManager = GridLayoutManager(this@Adm_Tela_Perfil_Usuario, spanCount)
        }

        // --- Livros Favoritos ---
        findViewById<RecyclerView>(R.id.recycler_view_favoritos).apply {
            layoutManager = GridLayoutManager(this@Adm_Tela_Perfil_Usuario, spanCount)
        }

        // --- Histórico de Aluguéis ---
        findViewById<RecyclerView>(R.id.recycler_view_historico).apply {
            layoutManager = GridLayoutManager(this@Adm_Tela_Perfil_Usuario, spanCount)
        }
    }

    /**
     * Busca os dados do USUÁRIO INSPECIONADO no Firestore.
     */
    private fun populateProfileData() {
        // userId é checado como não nulo no onCreate
        db.collection("usuarios").document(userId!!).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val nome = document.getString("nome") ?: "Usuário Desconhecido"
                    val fotoUrl = document.getString("profileImageUrl") ?: document.getString("fotoUrl")

                    userNameTextView.text = nome

                    Glide.with(this)
                        .load(fotoUrl)
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .circleCrop()
                        .into(profileImageView)
                } else {
                    Log.d("AdmPerfil", "Usuário não encontrado para ID: $userId")
                    userNameTextView.text = "Usuário (ID: $userId) Não Encontrado"
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AdmPerfil", "Erro ao buscar dados do usuário: ", exception)
                userNameTextView.text = "Erro ao carregar dados"
                Toast.makeText(this, "Erro de conexão com o Firestore.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadBookSections() {
        val currentUserId = userId!!

        // --- Livros Alugados (Query Real) ---
        val rentedBooksRecyclerView = findViewById<RecyclerView>(R.id.recycler_view_alugados)
        rentedBooksRecyclerView.adapter = BookAdapter(emptyList()) {}

        db.collection("livrosalugados")
            .whereEqualTo("usuarioId", currentUserId)
            .orderBy("dataAluguel", Query.Direction.DESCENDING)
            .limit(16) // Aumentado o limite para preencher a grade
            .get()
            .addOnSuccessListener { documents ->
                val rentedBooksList = documents.toObjects(Livro::class.java)

                rentedBooksRecyclerView.adapter = BookAdapter(rentedBooksList) { livro ->
                    Toast.makeText(this, "Alugado: ${livro.titulo}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AdmPerfil", "Erro ao buscar livros alugados: ", exception)
            }

        // --- Livros Favoritos (Query de Exemplo) ---
        fetchSampleBooks(R.id.recycler_view_favoritos, "Favoritos")

        fetchSampleBooks(R.id.recycler_view_historico, "Histórico")
    }

    private fun fetchSampleBooks(recyclerViewId: Int, sectionName: String) {
        val recyclerView = findViewById<RecyclerView>(recyclerViewId)
        recyclerView.adapter = BookAdapter(emptyList()) {} // Inicialização segura

        db.collection("livros").orderBy("titulo").limit(12).get() // Aumentado o limite
            .addOnSuccessListener { documents ->
                val booksList = documents.toObjects(Livro::class.java)
                recyclerView.adapter = BookAdapter(booksList) { livro ->
                    Toast.makeText(this, "$sectionName: ${livro.titulo}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AdmPerfil", "FALHA AO BUSCAR $sectionName: ", exception)
                Toast.makeText(this, "Falha ao carregar $sectionName.", Toast.LENGTH_LONG).show()
            }
    }


    // --- MÉTODOS DE NAVEGAÇÃO E MENU ---

    private fun setupHeaderClicks() {
        findViewById<ImageView>(R.id.icon_bell).setOnClickListener {
            startActivity(Intent(this, Adm_Tela_Notificacoes::class.java))
        }

        findViewById<ImageView>(R.id.icon_menu).setOnClickListener { view ->
            showPopupMenu(view)
        }

        profileImageView.setOnClickListener {
            startActivity(Intent(this, Tela_De_Perfil_Dados::class.java).apply {
                putExtra("USER_ID", userId)
            })
        }
    }

    private fun showPopupMenu(view: View) {
        PopupMenu(this, view).apply {
            menuInflater.inflate(R.menu.adm_menu_perfil, menu)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_editar_perfil -> {
                        startActivity(Intent(this@Adm_Tela_Perfil_Usuario, Tela_De_Perfil_Dados::class.java))
                        true
                    }
                    R.id.action_acessar_perfil -> {
                        startActivity(Intent(this@Adm_Tela_Perfil_Usuario, Adm_Tela_Procurar_Usuario::class.java))
                        true
                    }
                    R.id.action_gerenciar_notificacoes -> {
                        startActivity(Intent(this@Adm_Tela_Perfil_Usuario, Adm_Criar_Notificacao::class.java))
                        true
                    }
                    R.id.action_configuracoes_gerais -> {
                        startActivity(Intent(this@Adm_Tela_Perfil_Usuario, Tela_Config_geral::class.java))
                        true
                    }
                    R.id.action_acessibilidade -> {
                        startActivity(Intent(this@Adm_Tela_Perfil_Usuario, Tela_Acessibilidade::class.java))
                        true
                    }
                    else -> false
                }
            }
            show()
        }
    }


    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_perfil

        bottomNavigationView.setOnItemSelectedListener { item ->
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
                R.id.nav_chatbot -> {
                    startActivity(Intent(this, Tela_Adm_Chat_Bot::class.java))
                    finish()
                    true
                }
                R.id.nav_perfil -> {
                    startActivity(Intent(this, Adm_Tela_De_Perfil::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}