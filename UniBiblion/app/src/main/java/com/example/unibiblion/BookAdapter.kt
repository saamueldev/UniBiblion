package com.example.unibiblion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.unibiblion.R

class BookAdapter(
    private val books: List<Book>,
    private val onBookClick: (Book) -> Unit
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

        holder.bookTitle.text = book.title
        holder.bookAuthor.text = "Autor Desconhecido"
        holder.bookCoverImage.setImageResource(R.drawable.ic_book2)

        holder.itemView.setOnClickListener {
            onBookClick(book)
            Toast.makeText(holder.itemView.context, "Livro: ${book.title}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount() = books.size
}