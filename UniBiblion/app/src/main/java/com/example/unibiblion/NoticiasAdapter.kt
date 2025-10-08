package com.example.unibiblion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent // Necessário para a navegação!
// Importa as constantes que definimos na Noticia.kt
import com.example.unibiblion.Noticia.Companion.TIPO_IMAGEM_GRANDE
import com.example.unibiblion.Noticia.Companion.TIPO_IMAGEM_LATERAL

class NoticiasAdapter(private val listaNoticias: List<Noticia>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // --- 1. ViewHolders (Acessam os elementos do Layout) ---

    // ViewHolder 1: Para o Layout de Imagem Grande
    inner class ImagemGrandeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.text_titulo_grande)
        val preview: TextView = view.findViewById(R.id.text_preview_grande)
        val imagem: ImageView = view.findViewById(R.id.img_noticia_grande)

        fun bind(noticia: Noticia) {
            titulo.text = noticia.titulo
            preview.text = noticia.preview

            // --- CÓDIGO DO CLIQUE ADICIONADO AQUI ---
            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, NoticiaDetalheActivity::class.java).apply {
                    putExtra("EXTRA_TITULO", noticia.titulo)
                    putExtra("EXTRA_CORPO", noticia.corpo) // Enviando o corpo completo
                    putExtra("EXTRA_IMAGEM", noticia.urlImagem)
                }
                context.startActivity(intent)
            }
            // ----------------------------------------
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

            // --- CÓDIGO DO CLIQUE ADICIONADO AQUI ---
            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, NoticiaDetalheActivity::class.java).apply {
                    putExtra("EXTRA_TITULO", noticia.titulo)
                    putExtra("EXTRA_CORPO", noticia.corpo) // Enviando o corpo completo
                    putExtra("EXTRA_IMAGEM", noticia.urlImagem)
                }
                context.startActivity(intent)
            }
            // ----------------------------------------
        }
    }

    // --- 2. Lógica de Múltiplos Layouts (Sem Alterações) ---

    override fun getItemViewType(position: Int): Int {
        return listaNoticias[position].tipoLayout
    }

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

    // --- 3. Preenchimento de Dados (Sem Alterações) ---

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val noticia = listaNoticias[position]

        when (holder.itemViewType) {
            TIPO_IMAGEM_GRANDE -> (holder as ImagemGrandeViewHolder).bind(noticia)
            TIPO_IMAGEM_LATERAL -> (holder as ImagemLateralViewHolder).bind(noticia)
        }
    }

    override fun getItemCount() = listaNoticias.size
}