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

class LivroAdapter(
    options: FirestoreRecyclerOptions<Livro>,
    private val listener: OnItemClickListener
) : FirestoreRecyclerAdapter<Livro, LivroAdapter.LivroViewHolder>(options) {

    private val listaCompleta = ArrayList<Livro>()
    private val listaVisivel = ArrayList<Livro>()
    private var isUsingFilter = false // Flag para controlar qual lista usar

    override fun onDataChanged() {
        super.onDataChanged()
        if (!isUsingFilter) {
            listaVisivel.clear()
            listaVisivel.addAll(snapshots)
            notifyDataSetChanged()
        }
    }

    fun aplicarFiltrosCombinados(texto: String, estado: String, curso: String) {
        isUsingFilter = true
        val resultados = ArrayList<Livro>()
        val textoBusca = texto.lowercase(Locale.ROOT).trim()

        if (listaCompleta.isEmpty()){
            listaCompleta.addAll(snapshots)
        }

        for (livro in listaCompleta) {
            val correspondeTexto = if (textoBusca.isEmpty()) true else livro.titulo.lowercase(Locale.ROOT).contains(textoBusca)
            val correspondeEstado = if (estado == "Todos") true else livro.estado.equals(estado, ignoreCase = true)
            val correspondeCurso = if (curso == "Todos") true else livro.curso.equals(curso, ignoreCase = true)

            if (correspondeTexto && correspondeEstado && correspondeCurso) {
                resultados.add(livro)
            }
        }

        listaVisivel.clear()
        listaVisivel.addAll(resultados)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: LivroViewHolder, position: Int, model: Livro) {
        if (position < listaVisivel.size) {
            holder.bind(listaVisivel[position])
        }
    }

    override fun getItemCount(): Int = listaVisivel.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LivroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_livro, parent, false)
        return LivroViewHolder(view, listener, listaVisivel)
    }

    interface OnItemClickListener {
        fun onItemClick(livro: Livro)
    }

    class LivroViewHolder(
        itemView: View,
        private val listener: OnItemClickListener,
        private val listaLivros: List<Livro>
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private val tituloTextView: TextView = itemView.findViewById(R.id.textViewTitulo)
        private val autorTextView: TextView = itemView.findViewById(R.id.textViewAutor)
        private val anoTextView: TextView = itemView.findViewById(R.id.textViewAno)
        private val capaImageView: ImageView = itemView.findViewById(R.id.imageViewCapa)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION && position < listaLivros.size) {
                listener.onItemClick(listaLivros[position])
            }
        }

        fun bind(livro: Livro) {
            tituloTextView.text = livro.titulo
            autorTextView.text = livro.autor
            anoTextView.text = if (livro.ano > 0) livro.ano.toString() else ""
            if (livro.capaUrl.isNotEmpty()) {
                Glide.with(itemView.context).load(livro.capaUrl).into(capaImageView)
            } else {
                capaImageView.setImageDrawable(null) // ou uma imagem placeholder
            }
        }
    }
}
