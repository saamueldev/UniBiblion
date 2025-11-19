package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
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

// Implementa a interface de clique do nosso novo Adapter
class AdminReviewsActivity : AppCompatActivity(), OnReviewAdminClickListener {

    private lateinit var firestore: FirebaseFirestore // ⬅️ Novo: Variável para o BD
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminReviewsAdapter
    // A reviewsList agora será preenchida pelo Firebase
    // private lateinit var reviewsList: MutableList<Review> // ❌ Não mais usada diretamente no escopo

    // 1. DECLARAÇÃO: Declara a BottomNavigationView como variável da classe
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)

        firestore = FirebaseFirestore.getInstance() // ⬅️ Inicialização do Firebase
        bottomNavigation = findViewById(R.id.bottom_navigation)

        // 3. Configurar RecyclerView e Dados
        recyclerView = findViewById(R.id.recycler_reviews)
        // Adiciona o LayoutManager, essencial para o RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 🎯 NOVA CHAMADA: Carrega os dados reais do Firestore
        loadAllReviewsFromFirestore()

        // 4. Configurar a barra de título/pesquisa
        findViewById<android.widget.ImageButton>(R.id.btn_filter).setOnClickListener {
            Toast.makeText(this, "Abrir Filtro de Reviews Admin", Toast.LENGTH_SHORT).show()
        }

        // 5. CONFIGURAÇÃO DA BOTTOM NAVIGATION
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

    override fun onResume() {
        super.onResume()
        bottomNavigation.menu.findItem(R.id.nav_livraria).isChecked = true
    }

    // 🎯 FUNÇÃO QUE CARREGA DADOS REAIS DO FIREBASE
    private fun loadAllReviewsFromFirestore() {
        val query: Query = firestore.collection("reviews")
            // Ordena pela data de criação, mostrando as mais recentes no topo
            .orderBy("timestamp", Query.Direction.DESCENDING)

        query.get()
            .addOnSuccessListener { snapshots ->
                val listaReviews = mutableListOf<Review>()
                for (doc in snapshots) {
                    val review = doc.toObject(Review::class.java)
                    // Para fins de administração, é bom ter o ID do documento
                    review.id = doc.id
                    listaReviews.add(review)
                }

                // Inicializa o Adapter com os dados reais
                adapter = AdminReviewsAdapter(listaReviews, this)
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar reviews: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    // ❌ Função criarDadosDeExemplo() REMOVIDA

    override fun onReviewClicked(review: Review) {
        showReviewDetailModal(review)
    }

    private fun showReviewDetailModal(review: Review) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_admin_review_detail, null)

        // 🔑 Usando campos reais do modelo (userName, livroTitulo)
        view.findViewById<TextView>(R.id.tv_detail_user_name).text = review.userName
        view.findViewById<TextView>(R.id.tv_detail_book_title).text = "Livro: ${review.livroTitulo}"
        view.findViewById<TextView>(R.id.tv_detail_review_text).text = review.textoReview
        view.findViewById<RatingBar>(R.id.rb_detail_rating).rating = review.rating

        // Mantido o placeholder, pois carregar URLs aqui pode ser complexo
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
            // Usando review.userName (o nome real do usuário)
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
        // 🎯 Lógica Real de Exclusão do Firestore
        if (review.id.isNullOrEmpty()) {
            Toast.makeText(this, "Erro: ID da review não encontrado.", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("reviews").document(review.id!!)
            .delete()
            .addOnSuccessListener {
                // Atualiza a UI após sucesso
                adapter.removeReview(review)
                Toast.makeText(this, "Review de ${review.userName} removida com sucesso!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao deletar: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}