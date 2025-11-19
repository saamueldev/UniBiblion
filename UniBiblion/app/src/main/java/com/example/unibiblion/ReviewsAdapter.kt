package com.example.unibiblion

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ReviewsAdapter(private val reviews: MutableList<Review>) :
    RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

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

    // 2. O método onBindViewHolder foi completamente corrigido
    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]

        // Dados do Usuário
        holder.userName.text = review.userName

        // --- INÍCIO DA CORREÇÃO DA IMAGEM ---
        val userPhotoUrl = review.userPhotoUrl
        // Adiciona um log para ajudar a depurar. Verifique o Logcat com a tag "ReviewsAdapter"
        Log.d("ReviewsAdapter", "Carregando imagem para '${review.userName}'. URL: '$userPhotoUrl'")

        Glide.with(holder.itemView.context)
            .load(userPhotoUrl) // Carrega a URL da imagem (do Firebase Storage)
            .placeholder(R.drawable.ic_profile) // Imagem padrão enquanto carrega
            .error(R.drawable.ic_profile)         // Imagem para casos de erro ou URL vazia
            .circleCrop()                               // Deixa a imagem redonda
            .into(holder.userPhoto)                     // Define o resultado na sua ImageView
        // --- FIM DA CORREÇÃO DA IMAGEM ---

        // Dados da Review
        holder.ratingBar.rating = review.rating
        holder.reviewText.text = review.textoReview

        // Título do Livro
        holder.bookTitle.text = "Livro: ${review.livroTitulo}"
    }

    override fun getItemCount() = reviews.size

    fun updateList(newReviews: MutableList<Review>) {
        reviews.clear()
        reviews.addAll(newReviews)
        notifyDataSetChanged()
    }
}
