package com.example.unibiblion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.PopupMenu
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject

class Tela_De_Perfil : AppCompatActivity() {

    // Referências ao Firestore e Firebase Auth
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Views que serão atualizadas
    private lateinit var profileImageView: ImageView
    private lateinit var nameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_de_perfil)

        // Inicializar as views
        profileImageView = findViewById(R.id.profile_image)
        nameTextView = findViewById(R.id.text_name)

        setupHeaderClicks()
        setupBottomNavigation()

        // Carrega apenas os dados do usuário (nome e foto)
        loadUserProfile()
        // Carrega as listas de livros diretamente da coleção "livros"
        loadBookSections()
    }

    // Função mantida para carregar NOME e FOTO do usuário
    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("usuarios").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("nome") ?: "Nome não encontrado"
                        val profileImageUrl = document.getString("url_foto_perfil")

                        nameTextView.text = name
                        if (!profileImageUrl.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(profileImageUrl)
                                .placeholder(R.drawable.ic_profile)
                                .error(R.drawable.ic_profile)
                                .circleCrop()
                                .into(profileImageView)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Tela_De_Perfil", "Erro ao buscar perfil: ", exception)
                }
        }
    }

    // FUNÇÃO para carregar livros sem depender do usuário
    private fun loadBookSections() {
        // Para "Favoritos", pegamos 4 livros ordenados pelo título
        fetchBooksWithQuery(
            db.collection("livros").orderBy("title").limit(4),
            R.id.favorites_recycler_view
        )

        // Para "Histórico", pegamos 4 livros ordenados pelo autor
        fetchBooksWithQuery(
            db.collection("livros").orderBy("author").limit(4),
            R.id.history_recycler_view
        )

        // Para "Alugados", pegamos 4 livros em ordem decrescente de título
        fetchBooksWithQuery(
            db.collection("livros").orderBy("title", Query.Direction.DESCENDING).limit(4),
            R.id.rented_books_recycler_view
        )
    }

    // Função genérica que executa uma query e popula um RecyclerView
    private fun fetchBooksWithQuery(query: Query, recyclerViewId: Int) {
        val recyclerView = findViewById<RecyclerView>(recyclerViewId)
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        // **MELHORIA: Atribui um adaptador vazio imediatamente**
        recyclerView.adapter = BookAdapter(emptyList()) {}

        query.get()
            .addOnSuccessListener { documents ->
                val booksList = mutableListOf<Book>()
                for (document in documents) {
                    document.toObject<Book>()?.let { book ->
                        booksList.add(book)
                    }
                }
                // **Atualiza o adaptador existente com os novos dados**
                recyclerView.adapter = BookAdapter(booksList) { book ->
                    Toast.makeText(this, "Livro clicado: ${book.title}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                // AQUI ESTÁ O PROBLEMA: A busca está falhando.
                // Esta mensagem deve aparecer no seu Logcat.
                Log.e("Tela_De_Perfil", "FALHA AO BUSCAR LIVROS: A query precisa de um índice. Verifique o Logcat para o link de criação.", exception)
                Toast.makeText(this, "Falha ao carregar livros. Verifique os índices do Firestore.", Toast.LENGTH_LONG).show()
            }
    }

    // --- O resto do código (setupHeaderClicks, showPopupMenu, setupBottomNavigation) permanece o mesmo ---
    private fun setupHeaderClicks() {
        findViewById<ImageView>(R.id.icon_bell).setOnClickListener {
            startActivity(Intent(this, Tela_Notificacoes::class.java))
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
        popup.menuInflater.inflate(R.menu.menu_perfil_opcoes, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_editar_perfil -> {
                    startActivity(Intent(this, Tela_De_Perfil_Dados::class.java))
                    true
                }
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
    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_bar)
        bottomNavigationView.selectedItemId = R.id.nav_perfil
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_livraria -> {
                    startActivity(Intent(this, Tela_Central_Livraria ::class.java))
                    true
                }
                R.id.nav_noticias -> {
                    startActivity(Intent(this, NoticiasActivity::class.java))
                    true
                }
                R.id.nav_chatbot -> {
                    startActivity(Intent(this, Tela_Chat_Bot::class.java))
                    true
                }
                R.id.nav_perfil -> {
                    true
                }
                else -> false
            }
        }
    }
}
