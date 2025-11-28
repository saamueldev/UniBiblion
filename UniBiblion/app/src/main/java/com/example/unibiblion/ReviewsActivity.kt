package com.example.unibiblion

import android.content.Intent // Import corrigido para Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView // Import corrigido para BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

// 🔑 A Activity deve implementar a interface do Modal
class ReviewsActivity : AppCompatActivity(), ReviewFilterListener {

    companion object {
        const val EXTRA_LIVRO_ID = "extra_livro_id"
    }

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReviewsAdapter
    private lateinit var searchBar: EditText

    // Variáveis de UI
    private lateinit var btnFilter: ImageButton
    private lateinit var bottomNavigation: BottomNavigationView

    private var allReviewsList: List<Review> = emptyList()
    private var livroId: String? = null

    // Variável de estado
    private var currentFilterOption: FilterOption = FilterOption.RECENT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)

        db = FirebaseFirestore.getInstance()

        livroId = intent.getStringExtra(EXTRA_LIVRO_ID)

        // 1. Associa Views
        recyclerView = findViewById(R.id.recycler_reviews)
        recyclerView.layoutManager = LinearLayoutManager(this)
        searchBar = findViewById(R.id.search_bar)
        btnFilter = findViewById(R.id.btn_filter)
        // Certifique-se que o ID no XML é 'bottom_navigation' ou ajuste aqui.
        bottomNavigation = findViewById(R.id.bottom_navigation)

        // 2. Configura Listeners
        setupBottomNavigation() // Chamada para a função auxiliar
        setupSearchBar()
        setupFilterButton()
        // Removidas as chamadas duplicadas para setupSearchBar() e setupFilterButton()

        // 3. Verifica e Carrega Dados
        if (livroId.isNullOrEmpty()) {
            Toast.makeText(this, "Erro: Livro não identificado para carregar reviews.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        loadReviewsFromFirestore(livroId!!)
    }

    // ------------------------------------
    // --- SETUP LISTENERS (Métodos Auxiliares) ---
    // ------------------------------------

    /**
     * Configura a lógica de navegação da BottomNavigationView.
     */
    private fun setupBottomNavigation() {
        // Define qual item da barra de navegação deve ser exibido como 'selecionado'
        bottomNavigation.selectedItemId = R.id.nav_livraria

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_livraria -> {
                    // Já estamos nesta Activity, apenas retorne true
                    true
                }
                R.id.nav_noticias -> {
                    val intent = Intent(this, NoticiasActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_chatbot -> {
                    val intent = Intent(this, Tela_Chat_Bot::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_perfil -> {
                    val intent = Intent(this, Tela_De_Perfil::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }


    private fun setupSearchBar() {
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Ao digitar, aplicamos a busca e mantemos a ordenação atual
                applyFilterAndOrder(currentFilterOption)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupFilterButton() {
        btnFilter.setOnClickListener {
            val filterModal = ReviewFilterModal()
            // A Activity escuta o resultado do modal
            filterModal.setFilterListener(this)
            filterModal.show(supportFragmentManager, "ReviewFilterModal")
        }
    }

    // ------------------------------------
    // --- CARREGAMENTO DE DADOS ---
    // ------------------------------------

    private fun loadReviewsFromFirestore(id: String) {
        val query: Query = db.collection("reviews")
            .whereEqualTo("livroId", id)
            .orderBy("timestamp", Query.Direction.DESCENDING)

        query.get()
            .addOnSuccessListener { snapshots ->
                val listaReviews = mutableListOf<Review>()
                for (doc in snapshots) {
                    val review = doc.toObject(Review::class.java)
                    review.id = doc.id
                    listaReviews.add(review)
                }

                if (listaReviews.isEmpty()) {
                    Toast.makeText(this, "Nenhuma avaliação encontrada para este livro.", Toast.LENGTH_SHORT).show()
                }

                allReviewsList = listaReviews

                // Inicializa o adapter e aplica a ordenação padrão (RECENT)
                adapter = ReviewsAdapter(allReviewsList.toMutableList())
                recyclerView.adapter = adapter

                // Força a primeira ordenação
                applyFilterAndOrder(currentFilterOption)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Falha ao carregar as avaliações: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    // ------------------------------------
    // --- LÓGICA DE FILTRO E ORDENAÇÃO ---
    // ------------------------------------

    // Implementação da interface: recebe a opção do modal
    override fun onFilterApplied(orderBy: FilterOption) {
        currentFilterOption = orderBy // Atualiza a opção atual
        applyFilterAndOrder(currentFilterOption)
    }

    // FUNÇÃO PRINCIPAL: Combina busca de texto e ordenação
    private fun applyFilterAndOrder(orderBy: FilterOption) {
        // 1. Aplica a busca de texto primeiro na lista completa
        val currentSearchQuery = searchBar.text?.toString()

        val listAfterSearch = if (currentSearchQuery.isNullOrBlank()) {
            allReviewsList
        } else {
            val lowerCaseQuery = currentSearchQuery.toLowerCase()
            allReviewsList.filter { review ->
                review.textoReview.toLowerCase().contains(lowerCaseQuery) ||
                        review.userName.toLowerCase().contains(lowerCaseQuery)
            }
        }

        // 2. Aplica a ordenação na lista já filtrada
        val finalOrderedList = when (orderBy) {
            FilterOption.RECENT ->
                listAfterSearch.sortedByDescending { it.timestamp }

            FilterOption.HIGHEST_RATING ->
                listAfterSearch.sortedByDescending { it.rating }

            FilterOption.LOWEST_RATING ->
                listAfterSearch.sortedBy { it.rating }
        }

        // 3. Atualiza o Adapter
        (recyclerView.adapter as? ReviewsAdapter)?.updateList(finalOrderedList.toMutableList())
    }
}