package com.example.unibiblion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

// Implementa a interface de clique do nosso novo Adapter
class AdminReviewsActivity : AppCompatActivity(), OnReviewAdminClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminReviewsAdapter
    private lateinit var reviewsList: MutableList<Review>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews) // Reutiliza o layout principal

        // 1. Ocultar a Bottom Navigation (Não necessária para a tela Admin)
        findViewById<View>(R.id.bottom_navigation).visibility = View.GONE

        // 2. Configurar RecyclerView e Dados
        recyclerView = findViewById(R.id.recycler_reviews)
        reviewsList = criarDadosDeExemplo().toMutableList() // Converte para MutableList

        adapter = AdminReviewsAdapter(reviewsList, this) // Passa 'this' como listener
        recyclerView.adapter = adapter

        // 3. Configurar a barra de título/pesquisa (RF 04.02.10)
        // O layout da barra de pesquisa e filtro já está pronto no XML reutilizado.
        findViewById<android.widget.ImageButton>(R.id.btn_filter).setOnClickListener {
            Toast.makeText(this, "Abrir Filtro de Reviews Admin", Toast.LENGTH_SHORT).show()
        }
    }

    // Função de simulação de dados (Copie do seu ReviewsActivity.kt)
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

    // --- Implementação do Fluxo de Exclusão (RF 04.02.11) ---

    /**
     * Chamada pelo Adapter quando o Administrador clica em uma review.
     */
    override fun onReviewClicked(review: Review) {
        showReviewDetailModal(review)
    }

    /**
     * Mostra o modal com a review completa e o botão 'Excluir Review'.
     */
    private fun showReviewDetailModal(review: Review) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_admin_review_detail, null)

        // 1. Preencher os dados no modal
        view.findViewById<TextView>(R.id.tv_detail_user_name).text = review.usuario.nome
        view.findViewById<TextView>(R.id.tv_detail_book_title).text = "Livro: ${review.livroTitulo}"
        view.findViewById<TextView>(R.id.tv_detail_review_text).text = review.textoReview
        view.findViewById<RatingBar>(R.id.rb_detail_rating).rating = review.rating
        view.findViewById<ImageView>(R.id.img_detail_user_photo).setImageResource(review.usuario.fotoResourceId)

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        // 2. Listener do Botão Excluir
        view.findViewById<Button>(R.id.btn_delete_review).setOnClickListener {
            dialog.dismiss() // Fecha o modal de detalhes
            showDeleteConfirmationPopup(review) // Abre o pop-up de confirmação
        }

        dialog.show()
    }

    /**
     * Mostra o pop-up de confirmação de exclusão (Sim/Não).
     */
    private fun showDeleteConfirmationPopup(review: Review) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Exclusão")
            .setMessage("Deseja realmente remover a review de ${review.usuario.nome} sobre o livro ${review.livroTitulo}?")
            .setPositiveButton("Sim") { _, _ ->
                // Remove a review e notifica o usuário
                performReviewDeletion(review)
            }
            .setNegativeButton("Não") { dialog, _ ->
                dialog.dismiss() // Fecha o pop-up
            }
            .show()
    }

    /**
     * Simula a remoção da review do backend (localmente, remove da lista do Adapter).
     */
    private fun performReviewDeletion(review: Review) {
        // Simulação de exclusão:
        adapter.removeReview(review)

        Toast.makeText(this, "Review de ${review.usuario.nome} removida com sucesso!", Toast.LENGTH_SHORT).show()
        // FUTURO: Aqui você adicionaria a chamada API/Firestore para deletar
    }
}