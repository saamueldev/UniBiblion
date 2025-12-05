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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class Tela_De_Perfil : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    private lateinit var profileImageView: ImageView
    private lateinit var userNameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // 🚨 CORREÇÃO PRINCIPAL: Verifica a autenticação imediatamente
        if (auth.currentUser == null) {
            // Se não estiver logado, redireciona para a Tela_Login e finaliza a Activity
            val intent = Intent(this, Tela_Login::class.java)
            // Define flags para limpar a pilha de atividades, garantindo que o usuário não volte
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return // Sai do onCreate para evitar carregar a UI sem autenticação
        }
        // -------------------------------------------------------------

        setContentView(R.layout.activity_tela_de_perfil)

        profileImageView = findViewById(R.id.profile_image)
        userNameTextView = findViewById(R.id.text_name)

        setupHeaderClicks()
        setupBottomNavigation()
        populateProfileData()
        loadBookSections()
    }

    private fun populateProfileData() {
        // Usuário agora é garantido como não-nulo pelo onCreate
        val user = auth.currentUser

        // Embora user não deva ser null aqui, manter uma checagem de segurança pode ser bom.
        if (user != null) {
            db.collection("usuarios").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nome = document.getString("nome") ?: "Usuário"
                        // Usando o operador Elvis para testar duas chaves comuns de URL de foto
                        val fotoUrl = document.getString("profileImageUrl") ?: document.getString("fotoUrl")

                        userNameTextView.text = nome

                        Glide.with(this)
                            .load(fotoUrl)
                            .placeholder(R.drawable.ic_profile)
                            .error(R.drawable.ic_profile)
                            .circleCrop()
                            .into(profileImageView)
                    } else {
                        Log.d("Tela_De_Perfil", "Nenhum documento de usuário encontrado.")
                        userNameTextView.text = user.displayName ?: "Usuário"
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Tela_De_Perfil", "Erro ao buscar dados do usuário: ", exception)
                    userNameTextView.text = user.displayName ?: "Usuário"
                }
        } else {
            // Este bloco agora é redundante, mas se for alcançado, significa um erro de estado.
            Log.e("Tela_De_Perfil", "Erro de estado: Usuário nulo após checagem no onCreate.")
            startActivity(Intent(this, Tela_Login::class.java))
            finish()
        }
    }

    private fun loadBookSections() {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            // Este log será atingido se a checagem do onCreate falhar ou se o estado mudar após o onCreate
            Log.w("Tela_De_Perfil", "ID do usuário não encontrado para carregar livros.")
            return
        }

        val rentedBooksRecyclerView = findViewById<RecyclerView>(R.id.rented_books_recycler_view)
        rentedBooksRecyclerView.layoutManager = GridLayoutManager(this, 4)
        // Certifique-se de que BookAdapter é a classe correta e aceita List<Livro>
        rentedBooksRecyclerView.adapter = BookAdapter(emptyList()) {}

        db.collection("livrosalugados")
            .whereEqualTo("usuarioId", currentUserId)
            .orderBy("dataDevolucao")
            .limit(8)
            .get()
            .addOnSuccessListener { documents ->
                val rentedBooksList = mutableListOf<Livro>()
                if (documents.isEmpty) {
                    Log.d("Tela_De_Perfil", "Nenhum livro alugado encontrado para o usuário: $currentUserId")
                } else {
                    for (document in documents) {
                        val livro = Livro(
                            id = document.id,
                            titulo = document.getString("titulo") ?: "Título indisponível",
                            capaUrl = document.getString("capaURL") ?: ""
                        )
                        rentedBooksList.add(livro)
                    }
                }
                rentedBooksRecyclerView.adapter = BookAdapter(rentedBooksList) { livro ->
                    Toast.makeText(this, "Alugado: ${livro.titulo}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Tela_De_Perfil", "Erro ao buscar livros alugados: ", exception)
            }

        fetchBooksWithQuery(
            db.collection("livros").orderBy("autor").limit(4),
            R.id.favorites_recycler_view
        )
        fetchBooksWithQuery(
            db.collection("livros").orderBy("titulo", Query.Direction.DESCENDING).limit(4),
            R.id.history_recycler_view
        )
    }

    private fun fetchBooksWithQuery(query: Query, recyclerViewId: Int) {
        val recyclerView = findViewById<RecyclerView>(recyclerViewId)
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        recyclerView.adapter = BookAdapter(emptyList()) {}

        query.get()
            .addOnSuccessListener { documents ->
                // Certifique-se de que 'Livro' é uma data class compatível com toObjects
                val booksList = documents.toObjects(Livro::class.java)
                recyclerView.adapter = BookAdapter(booksList) { livro ->
                    Toast.makeText(this, "Clicado: ${livro.titulo}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Tela_De_Perfil", "FALHA AO BUSCAR LIVROS: ", exception)
                Toast.makeText(this, "Falha ao carregar livros.", Toast.LENGTH_LONG).show()
            }
    }


    private fun setupHeaderClicks() {
        findViewById<ImageView>(R.id.icon_bell).setOnClickListener {
            startActivity(Intent(this, Tela_Notificacoes::class.java))
        }

        findViewById<ImageView>(R.id.icon_menu).setOnClickListener { view ->
            showPopupMenu(view)
        }

        profileImageView.setOnClickListener {
            startActivity(Intent(this, Tela_De_Perfil_Dados::class.java))
        }
    }

    private fun showPopupMenu(view: View) {
        val context = this
        val popup = PopupMenu(context, view)
        // Certifique-se de que o menu_perfil_opcoes.xml existe e contém os IDs corretos
        popup.menuInflater.inflate(R.menu.menu_perfil_opcoes, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_acessibilidade -> {
                    startActivity(Intent(context, Tela_Acessibilidade::class.java))
                    true
                }
                R.id.action_editar_perfil -> {
                    startActivity(Intent(context, Tela_De_Perfil_Dados::class.java))
                    true
                }
                R.id.action_configuracoes_gerais -> {
                    // Esta opção não deve fazer logout, mas sim abrir as configurações.
                    // Removi a chamada auth.signOut() para este item de menu.
                    val intent = Intent(context, Tela_Config_geral::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }


    private fun setupBottomNavigation() {
        // Certifique-se de que o ID da BottomNavigationView é 'bottom_navigation'
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_perfil

        bottomNavigationView.setOnItemSelectedListener { item ->
            // Note: Não estamos chamando finish() aqui, o que é um padrão aceitável
            // para navegação Bottom Bar, mas pode levar a uma pilha de Activities maior.
            // Se você quer o comportamento do primeiro código (chamar finish()),
            // basta adicionar finish() após startActivity() em cada caso.
            when (item.itemId) {
                R.id.nav_livraria -> {
                    startActivity(Intent(this, Tela_Central_Livraria::class.java))
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
                    // Já está na tela, não faz nada
                    true
                }
                else -> false
            }
        }
    }
}