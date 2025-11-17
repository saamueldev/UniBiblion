package com.example.unibiblion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import java.util.Locale

class LivroAdapter(options: FirestoreRecyclerOptions<Livro>) :
    FirestoreRecyclerAdapter<Livro, LivroAdapter.LivroViewHolder>(options) {

    private val listaCompleta = ArrayList<Livro>()
    private val listaVisivel = ArrayList<Livro>()

    override fun onDataChanged() {
        super.onDataChanged()
        listaCompleta.clear()
        listaCompleta.addAll(snapshots)
        // Na primeira vez, a lista visível é a lista completa (sem filtros)
        aplicarFiltrosCombinados("", "Todos", "Todos")
    }

    /**
     * Método público que aplica os três filtros de uma vez.
     */
    fun aplicarFiltrosCombinados(texto: String, estado: String, curso: String) {
        val resultados = ArrayList<Livro>()
        val textoBusca = texto.lowercase(Locale.ROOT).trim()

        for (livro in listaCompleta) {
            val correspondeTexto = if (textoBusca.isEmpty()) {
                true
            } else {
                livro.titulo.lowercase(Locale.ROOT).contains(textoBusca)
            }

            val correspondeEstado = if (estado == "Todos") {
                true
            } else {
                livro.estado.equals(estado, ignoreCase = true)
            }

            val correspondeCurso = if (curso == "Todos") {
                true
            } else {
                livro.curso.equals(curso, ignoreCase = true)
            }

            // O livro só é adicionado se corresponder a TODOS os filtros
            if (correspondeTexto && correspondeEstado && correspondeCurso) {
                resultados.add(livro)
            }
        }

        listaVisivel.clear()
        listaVisivel.addAll(resultados)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: LivroViewHolder, position: Int, model: Livro) {
        holder.bind(listaVisivel[position])
    }

    override fun getItemCount(): Int = listaVisivel.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LivroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_livro, parent, false)
        return LivroViewHolder(view)
    }

    class LivroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tituloTextView: TextView = itemView.findViewById(R.id.textViewTitulo)
        private val autorTextView: TextView = itemView.findViewById(R.id.textViewAutor)
        private val anoTextView: TextView = itemView.findViewById(R.id.textViewAno)
        private val capaImageView: ImageView = itemView.findViewById(R.id.imageViewCapa)

        fun bind(livro: Livro) {
            tituloTextView.text = livro.titulo
            autorTextView.text = livro.autor
            anoTextView.text = if (livro.ano > 0) livro.ano.toString() else ""
            if (livro.capaUrl.isNotEmpty()) {
                Glide.with(itemView.context).load(livro.capaUrl).into(capaImageView)
            } else {
                capaImageView.setImageDrawable(null)
            }
        }
    }
}
