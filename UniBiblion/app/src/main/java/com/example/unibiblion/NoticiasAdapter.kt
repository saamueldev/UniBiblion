package com.example.unibiblion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
// Importa as constantes que definimos na Noticia.kt
import com.example.unibiblion.Noticia.Companion.TIPO_IMAGEM_GRANDE
import com.example.unibiblion.Noticia.Companion.TIPO_IMAGEM_LATERAL

class NoticiasAdapter(private val listaNoticias: List<Noticia>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() { // Usamos ViewHolder genérico para múltiplos tipos

    // --- 1. ViewHolders (Acessam os elementos do Layout) ---

    // ViewHolder 1: Para o Layout de Imagem Grande
    inner class ImagemGrandeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.text_titulo_grande)
        val preview: TextView = view.findViewById(R.id.text_preview_grande)
        // A imagem já foi resolvida com o placeholder
        val imagem: ImageView = view.findViewById(R.id.img_noticia_grande)

        fun bind(noticia: Noticia) {
            titulo.text = noticia.titulo
            preview.text = noticia.preview
            // Aqui, em um app real, você usaria Glide/Picasso para carregar a imagem da URL
        }
    }

    // ViewHolder 2: Para o Layout de Imagem Lateral
    inner class ImagemLateralViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.text_titulo_lateral)
        val preview: TextView = view.findViewById(R.id.text_preview_lateral)
        val imagem: ImageView = view.findViewById(R.id.img_noticia_lateral)

        fun bind(noticia: Noticia) {
            titulo.text = noticia.titulo
            preview.text = noticia.preview
        }
    }

    // --- 2. Lógica de Múltiplos Layouts ---

    // Retorna a constante (1 ou 2) para o tipo de item na posição 'position'
    override fun getItemViewType(position: Int): Int {
        return listaNoticias[position].tipoLayout
    }

    // Cria a View e o ViewHolder corretos baseado no 'viewType' retornado acima
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TIPO_IMAGEM_GRANDE -> {
                val view = inflater.inflate(R.layout.item_noticia_imagem_grande, parent, false)
                ImagemGrandeViewHolder(view)
            }
            TIPO_IMAGEM_LATERAL -> {
                val view = inflater.inflate(R.layout.item_noticia_imagem_lateral, parent, false)
                ImagemLateralViewHolder(view)
            }
            else -> throw IllegalArgumentException("Tipo de layout desconhecido: $viewType")
        }
    }

    // --- 3. Preenchimento de Dados ---

    // Preenche os dados no ViewHolder correto
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val noticia = listaNoticias[position]

        when (holder.itemViewType) {
            TIPO_IMAGEM_GRANDE -> (holder as ImagemGrandeViewHolder).bind(noticia)
            TIPO_IMAGEM_LATERAL -> (holder as ImagemLateralViewHolder).bind(noticia)
        }
    }

    // Retorna o número total de itens (necessário para o RecyclerView)
    override fun getItemCount() = listaNoticias.size
}