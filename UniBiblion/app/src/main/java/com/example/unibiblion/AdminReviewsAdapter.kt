package com.example.unibiblion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
// Se você for usar o Glide, importe-o aqui
// import com.bumptech.glide.Glide

// Interface para notificar a Activity sobre o clique (Mantida)
interface OnReviewAdminClickListener {
    fun onReviewClicked(review: Review)
}

class AdminReviewsAdapter(
    private val reviews: MutableList<Review>,
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
            .inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]

        // 🔑 1. Dados do Usuário (CORRIGIDO)
        holder.userName.text = review.userName

        // 🔑 2. Foto do Usuário (Usando Placeholder/Padrão)
        // Se review.userPhotoUrl fosse uma URL real, o Glide seria necessário.
        holder.userPhoto.setImageResource(android.R.drawable.ic_menu_help)

        // 3. Dados da Review (Mantido)
        holder.ratingBar.rating = review.rating
        holder.reviewText.text = review.textoReview

        // 4. Título do Livro (Mantido)
        holder.bookTitle.text = "Livro: ${review.livroTitulo}"

        // 5. LÓGICA DE CLIQUE (Mantida)
        holder.itemView.setOnClickListener {
            listener.onReviewClicked(review)
        }
    }

    override fun getItemCount() = reviews.size

    fun removeReview(review: Review) {
        val position = reviews.indexOfFirst { it.id == review.id }
        if (position != -1) {
            reviews.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}