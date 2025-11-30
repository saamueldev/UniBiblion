package com.example.unibiblion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BookAdapter(
    private val books: List<Livro>,
    private val onBookClick: (Livro) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookCoverImage: ImageView = itemView.findViewById(R.id.book_cover_image)
        val bookTitle: TextView = itemView.findViewById(R.id.book_title)
        val bookAuthor: TextView = itemView.findViewById(R.id.book_author)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book_card, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]

        holder.bookTitle.text = book.titulo

        if (book.autor.isNotBlank()) {
            holder.bookAuthor.text = book.autor
            holder.bookAuthor.visibility = View.VISIBLE
        } else {
            holder.bookAuthor.visibility = View.GONE
        }

        Glide.with(holder.itemView.context)
            .load(book.capaUrl)
            .placeholder(R.drawable.ic_book2)
            .error(R.drawable.ic_book2)
            .into(holder.bookCoverImage)

        holder.itemView.setOnClickListener {
            onBookClick(book)
        }
    }

    override fun getItemCount() = books.size
}
