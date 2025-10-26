package com.example.unibiblion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Interface para notificar a Activity sobre o clique
interface OnReviewAdminClickListener {
    fun onReviewClicked(review: Review)
}

class AdminReviewsAdapter(
    private val reviews: MutableList<Review>, // Usamos MutableList para permitir remoção
    private val listener: OnReviewAdminClickListener
) :
    RecyclerView.Adapter<AdminReviewsAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userPhoto: ImageView = itemView.findViewById(R.id.img_user_photo)
        val userName: TextView = itemView.findViewById(R.id.tv_user_name)
        val ratingBar: RatingBar = itemView.findViewById(R.id.rating_bar)
        val reviewText: TextView = itemView.findViewById(R.id.tv_review_text)
        val bookTitle: TextView = itemView.findViewById(R.id.tv_book_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false) // Reutiliza o item_review.xml
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]

        // 1. Dados do Usuário
        holder.userName.text = review.usuario.nome
        holder.userPhoto.setImageResource(review.usuario.fotoResourceId)

        // 2. Dados da Review
        holder.ratingBar.rating = review.rating
        holder.reviewText.text = review.textoReview

        // 3. Título do Livro
        holder.bookTitle.text = "Livro: ${review.livroTitulo}"

        // 4. LÓGICA DE CLIQUE PARA O ADMINISTRADOR
        holder.itemView.setOnClickListener {
            listener.onReviewClicked(review)
        }
    }

    override fun getItemCount() = reviews.size

    /**
     * Remove a review da lista local e notifica o adapter para atualizar a UI.
     */
    fun removeReview(review: Review) {
        val position = reviews.indexOfFirst { it.id == review.id }
        if (position != -1) {
            reviews.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}