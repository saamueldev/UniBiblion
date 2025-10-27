package com.example.unibiblion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unibiblion.Livro

class LivroAdapter(private val listaLivros: List<Livro>) :
    RecyclerView.Adapter<LivroAdapter.LivroViewHolder>() {

    // 1. Cria o ViewHolder, infla o layout (item_livro.xml) e o anexa ao RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LivroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_livro, parent, false)
        return LivroViewHolder(view)
    }

    // 2. Vincula os dados (Livro) com as Views do item_livro.xml
    override fun onBindViewHolder(holder: LivroViewHolder, position: Int) {
        val livro = listaLivros[position]
        holder.bind(livro)
    }

    // 3. Retorna o número total de itens na lista
    override fun getItemCount(): Int = listaLivros.size

    // Classe interna que segura as referências das Views para evitar findViewById() repetidas vezes
    inner class LivroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Referências das Views do layout item_livro.xml
        private val tituloTextView: TextView = itemView.findViewById(R.id.textViewTitulo)
        private val capaImageView: ImageView = itemView.findViewById(R.id.imageViewCapa)

        // Aqui você pode adicionar outras views (ex: Autor) se seu item_livro.xml tiver.

        fun bind(livro: Livro) {
            tituloTextView.text = livro.titulo

            // Define a imagem da capa usando o ID do recurso (Drawable)
            capaImageView.setImageResource(livro.capaResourceId)

            // Se você quiser que o item seja clicável (opcional)
            itemView.setOnClickListener {
                // Faça algo quando o livro for clicado, ex:
                // val context = itemView.context
                // Toast.makeText(context, "Clicou em: ${livro.titulo}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}