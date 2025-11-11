// Em: app/src/main/java/com/example/unibiblion/LivroAdapter.kt
package com.example.unibiblion // CORRIGIDO: "import" removido do final

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
// REMOVIDO: 'import androidx.compose...' que estava causando o erro

// A classe LivroAdapter agora funciona com a data class Livro que tem um Int para a imagem.
class LivroAdapter(private val livros: List<Livro>) : RecyclerView.Adapter<LivroAdapter.LivroViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LivroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_livro, parent, false)
        return LivroViewHolder(view)
    }

    override fun onBindViewHolder(holder: LivroViewHolder, position: Int) {
        val livro = livros[position]
        holder.bind(livro)
    }

    override fun getItemCount(): Int {
        return livros.size
    }

    class LivroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tituloTextView: TextView = itemView.findViewById(R.id.textViewTitulo)
        private val autorTextView: TextView = itemView.findViewById(R.id.textViewAutor)
        // Se seu item_livro.xml não tiver um textViewAno, pode remover a linha abaixo
        private val anoTextView: TextView = itemView.findViewById(R.id.textViewAno)
        private val capaImageView: ImageView = itemView.findViewById(R.id.imageViewCapa)

        fun bind(livro: Livro) {
            tituloTextView.text = livro.titulo
            autorTextView.text = livro.autor

            // CORREÇÃO 1: A data class Livro não tem mais 'anoPublicacao'.
            // Vamos esconder ou definir um texto padrão por enquanto.
            anoTextView.visibility = View.GONE // Opção 1: Esconde o campo de ano

            // CORREÇÃO 2: Carregar a imagem usando o ID do resource (Int).
            // A função setImageResource é feita exatamente para isso.
            capaImageView.setImageResource(livro.imagemId)
        }
    }
}
