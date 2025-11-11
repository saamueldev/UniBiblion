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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

// Implementa a interface de clique do nosso novo Adapter
class AdminReviewsActivity : AppCompatActivity(), OnReviewAdminClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminReviewsAdapter
    private lateinit var reviewsList: MutableList<Review>

    // 1. DECLARAÇÃO: Declara a BottomNavigationView como variável da classe
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews) // Reutiliza o layout principal

        // 2. INICIALIZAÇÃO: Inicializa a variável aqui
        bottomNavigation = findViewById(R.id.bottom_navigation)

        // 3. Configurar RecyclerView e Dados
        recyclerView = findViewById(R.id.recycler_reviews)
        reviewsList = criarDadosDeExemplo().toMutableList()

        adapter = AdminReviewsAdapter(reviewsList, this)
        recyclerView.adapter = adapter

        // 4. Configurar a barra de título/pesquisa
        findViewById<android.widget.ImageButton>(R.id.btn_filter).setOnClickListener {
            Toast.makeText(this, "Abrir Filtro de Reviews Admin", Toast.LENGTH_SHORT).show()
        }

        // 5. CONFIGURAÇÃO DA BOTTOM NAVIGATION
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_livraria -> {
                    // Navegar de volta para o Dashboard Central/Home Admin
                    val intent = Intent(this, Adm_Tela_Central_Livraria::class.java)
                    // Estas flags ajudam a limpar a pilha e trazer a tela principal para frente
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

    // 6. SOLUÇÃO: Mover a lógica de seleção para onResume()
    override fun onResume() {
        super.onResume()
        // Esta função é chamada toda vez que a Activity se torna visível novamente,
        // garantindo que o ícone de Livraria (o fluxo desta tela) seja selecionado.
        bottomNavigation.menu.findItem(R.id.nav_livraria).isChecked = true
    }

    // Funções de simulação de dados e modais (Sem alterações)
    // ...
    private fun criarDadosDeExemplo(): List<Review> {
        val user1 = UsuarioReview("Ana Lúcia", android.R.drawable.ic_menu_help)
        val user2 = UsuarioReview("Marcos Vinicius", android.R.drawable.ic_menu_help)
        val user3 = UsuarioReview("Gabriela Dias", android.R.drawable.ic_menu_help)

        return listOf(
            Review("R1", "Introdução à Robótica", "Muito didático, o livro consegue simplificar conceitos complexos. Leitura obrigatória!", user1, 4.5f),
            Review("R2", "O Código Perdido", "Um thriller eletrizante! Não consegui largar. A reviravolta no final é genial.", user2, 5.0f),
            Review("R3", "História da Arte Moderna", "Conteúdo excelente, mas o layout poderia ser mais visual. Mesmo assim, um bom material de estudo.", user3, 3.5f),
            Review("R4", "O Código Perdido", "Li em 2 dias. Perfeito para quem ama suspense e mistério.", user1, 5.0f)
        )
    }

    override fun onReviewClicked(review: Review) {
        showReviewDetailModal(review)
    }

    private fun showReviewDetailModal(review: Review) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_admin_review_detail, null)

        view.findViewById<TextView>(R.id.tv_detail_user_name).text = review.usuario.nome
        view.findViewById<TextView>(R.id.tv_detail_book_title).text = "Livro: ${review.livroTitulo}"
        view.findViewById<TextView>(R.id.tv_detail_review_text).text = review.textoReview
        view.findViewById<RatingBar>(R.id.rb_detail_rating).rating = review.rating
        view.findViewById<ImageView>(R.id.img_detail_user_photo).setImageResource(review.usuario.fotoResourceId)

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
            .setMessage("Deseja realmente remover a review de ${review.usuario.nome} sobre o livro ${review.livroTitulo}?")
            .setPositiveButton("Sim") { _, _ ->
                performReviewDeletion(review)
            }
            .setNegativeButton("Não") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun performReviewDeletion(review: Review) {
        adapter.removeReview(review)
        Toast.makeText(this, "Review de ${review.usuario.nome} removida com sucesso!", Toast.LENGTH_SHORT).show()
    }
}
