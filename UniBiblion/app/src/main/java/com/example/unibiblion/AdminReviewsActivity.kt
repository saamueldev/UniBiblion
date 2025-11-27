package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.Normalizer // 🎯 NOVO IMPORT

class AdminReviewsActivity : AppCompatActivity(), OnReviewAdminClickListener, ReviewFilterListener {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminReviewsAdapter
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var searchBar: EditText

    private var allReviewsList: List<Review> = emptyList()
    private var currentFilterOption: FilterOption = FilterOption.RECENT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)

        firestore = FirebaseFirestore.getInstance()
        bottomNavigation = findViewById(R.id.bottom_navigation)
        searchBar = findViewById(R.id.search_bar)

        recyclerView = findViewById(R.id.recycler_reviews)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadInitialReviews()
        setupSearchBar()

        findViewById<android.widget.ImageButton>(R.id.btn_filter).setOnClickListener {
            showFilterModal()
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_livraria -> {
                    val intent = Intent(this, Adm_Tela_Central_Livraria::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_noticias -> {
                    val intent = Intent(this, Adm_Tela_Mural_Noticias_Eventos::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_chatbot -> {
                    val intent = Intent(this, Tela_Chat_Bot::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_perfil -> {
                    val intent = Intent(this, Adm_Tela_De_Perfil::class.java)
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
                applyLocalFilterAndOrder(currentFilterOption)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        bottomNavigation.menu.findItem(R.id.nav_livraria).isChecked = true
    }

    private fun showFilterModal() {
        val modal = ReviewFilterModal()
        modal.setFilterListener(this)
        modal.show(supportFragmentManager, "ReviewFilterModal")
    }

    override fun onFilterApplied(orderBy: FilterOption) {
        if (currentFilterOption != orderBy) {
            currentFilterOption = orderBy
            applyLocalFilterAndOrder(orderBy)
        }
    }

    /**
     * FUNÇÃO CENTRAL: Aplica a BUSCA (agora sem acentos) e a ORDENAÇÃO na lista LOCAL (allReviewsList).
     */
    private fun applyLocalFilterAndOrder(orderBy: FilterOption) {

        if (allReviewsList.isEmpty()) return

        // 1. Aplica a busca de texto primeiro na lista completa (allReviewsList)
        val currentSearchQuery = searchBar.text?.toString()

        val listAfterSearch = if (currentSearchQuery.isNullOrBlank()) {
            allReviewsList
        } else {
            // 🎯 APLICAÇÃO DA CORREÇÃO DE ACENTOS NA QUERY DIGITADA
            val normalizedQuery = currentSearchQuery.normalizeAccents().toLowerCase()

            allReviewsList.filter { review ->
                // 🎯 APLICAÇÃO DA CORREÇÃO DE ACENTOS EM TODOS OS CAMPOS DO BD
                review.textoReview.normalizeAccents().toLowerCase().contains(normalizedQuery) ||
                        review.livroTitulo.normalizeAccents().toLowerCase().contains(normalizedQuery) ||
                        review.userName.normalizeAccents().toLowerCase().contains(normalizedQuery)
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
        if (::adapter.isInitialized) {
            adapter.updateReviews(finalOrderedList.toMutableList())
        }
    }

    private fun loadInitialReviews() {
        val query: Query = firestore.collection("reviews")
            .orderBy("timestamp", Query.Direction.DESCENDING)

        query.get()
            .addOnSuccessListener { snapshots ->
                val listaReviews = mutableListOf<Review>()
                for (doc in snapshots) {
                    val review = doc.toObject(Review::class.java)
                    review.id = doc.id
                    listaReviews.add(review)
                }

                allReviewsList = listaReviews

                if (!::adapter.isInitialized) {
                    adapter = AdminReviewsAdapter(listaReviews, this)
                    recyclerView.adapter = adapter
                } else {
                    adapter.updateReviews(listaReviews)
                }

                applyLocalFilterAndOrder(currentFilterOption)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar reviews: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onReviewClicked(review: Review) {
        showReviewDetailModal(review)
    }

    private fun showReviewDetailModal(review: Review) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_admin_review_detail, null)

        view.findViewById<TextView>(R.id.tv_detail_user_name).text = review.userName
        view.findViewById<TextView>(R.id.tv_detail_book_title).text = "Livro: ${review.livroTitulo}"
        view.findViewById<TextView>(R.id.tv_detail_review_text).text = review.textoReview
        view.findViewById<RatingBar>(R.id.rb_detail_rating).rating = review.rating

        view.findViewById<ImageView>(R.id.img_detail_user_photo).setImageResource(android.R.drawable.ic_menu_help)

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        view.findViewById<Button>(R.id.btn_delete_review).setOnClickListener {
            dialog.dismiss()
            showDeleteConfirmationPopup(review)
        }

        dialog.show()
    }

    private fun showDeleteConfirmationPopup(review: Review) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Exclusão")
            .setMessage("Deseja realmente remover a review de ${review.userName} sobre o livro ${review.livroTitulo}?")
            .setPositiveButton("Sim") { _, _ ->
                performReviewDeletion(review)
            }
            .setNegativeButton("Não") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun performReviewDeletion(review: Review) {
        if (review.id.isNullOrEmpty()) {
            Toast.makeText(this, "Erro: ID da review não encontrado.", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("reviews").document(review.id!!)
            .delete()
            .addOnSuccessListener {
                adapter.removeReview(review)
                removeReviewFromMasterList(review)

                Toast.makeText(this, "Review de ${review.userName} removida com sucesso!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao deletar: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun removeReviewFromMasterList(review: Review) {
        val mutableList = allReviewsList.toMutableList()
        mutableList.removeIf { it.id == review.id }
        allReviewsList = mutableList
    }
}

// 🎯 FUNÇÃO DE EXTENSÃO PARA REMOVER ACENTOS
fun String.normalizeAccents(): String {
    return Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
}