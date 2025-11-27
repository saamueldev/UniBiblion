package com.example.unibiblion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // 🎯 NOVO IMPORT

// Interface para notificar a Activity sobre o clique (Mantida)
interface OnReviewAdminClickListener {
    fun onReviewClicked(review: Review)
}

class AdminReviewsAdapter(
    private var reviews: MutableList<Review>,
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

        // 1. Dados do Usuário
        holder.userName.text = review.userName

        // 🎯 CORREÇÃO: Carregar a imagem via URL usando GLIDE
        val userPhotoUrl = review.userPhotoUrl

        Glide.with(holder.itemView.context)
            .load(userPhotoUrl) // Carrega a URL da imagem (deve vir do objeto Review)
            .placeholder(R.drawable.ic_profile) // Use seu placeholder padrão (R.drawable.ic_profile ou similar)
            .error(R.drawable.ic_profile)         // Use o mesmo para erro ou URL vazia
            .circleCrop()                               // Deixa a imagem redonda
            .into(holder.userPhoto)                     // Define o resultado na sua ImageView

        // 3. Dados da Review
        holder.ratingBar.rating = review.rating

        // Exibição truncada (mantida)
        val maxLines = 3
        val reviewText = if (review.textoReview.lines().size > maxLines) {
            review.textoReview.substringBefore('\n') + "..."
        } else {
            review.textoReview
        }
        holder.reviewText.text = reviewText

        // 4. Título do Livro
        holder.bookTitle.text = "Livro: ${review.livroTitulo}"

        // 5. LÓGICA DE CLIQUE
        holder.itemView.setOnClickListener {
            listener.onReviewClicked(review)
        }
    }

    override fun getItemCount() = reviews.size

    /**
     * Remove uma review da lista e notifica o RecyclerView.
     */
    fun removeReview(review: Review) {
        val position = reviews.indexOfFirst { it.id == review.id }
        if (position != -1) {
            reviews.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    /**
     * Atualiza a lista completa de reviews.
     */
    fun updateReviews(newReviews: List<Review>) {
        reviews = newReviews.toMutableList()
        notifyDataSetChanged()
    }
}