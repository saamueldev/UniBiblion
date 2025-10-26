package com.example.unibiblion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ImageButton // Import necessário
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import android.content.Context // Import necessário
import android.widget.Toast
import com.example.unibiblion.Noticia.Companion.TIPO_IMAGEM_GRANDE
import com.example.unibiblion.Noticia.Companion.TIPO_IMAGEM_LATERAL

// NOVO: Adiciona 'isAdmin' ao construtor
class NoticiasAdapter(
    private var listaNoticias: MutableList<Noticia>,
    private val isAdmin: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // --- 1. ViewHolders (COM LÓGICA DO LÁPIS) ---

    inner class ImagemGrandeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.text_titulo_grande)
        val preview: TextView = view.findViewById(R.id.text_preview_grande)
        val imagem: ImageView = view.findViewById(R.id.img_noticia_grande)
        // CONECTA o botão de Edição
        val editButton: ImageButton = view.findViewById(R.id.button_edit_grande)

        fun bind(noticia: Noticia) {
            titulo.text = noticia.titulo
            preview.text = noticia.preview
            val context = itemView.context

            // Ação de Detalhe
            itemView.setOnClickListener {
                abrirDetalhe(noticia, context)
            }

            // LÓGICA DO LÁPIS RESTAURADA
            if (isAdmin) {
                editButton.visibility = View.VISIBLE
                editButton.setOnClickListener {
                    abrirEdicao(noticia, context)
                }
            } else {
                editButton.visibility = View.GONE
            }
        }
    }

    inner class ImagemLateralViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.text_titulo_lateral)
        val preview: TextView = view.findViewById(R.id.text_preview_lateral)
        val imagem: ImageView = view.findViewById(R.id.img_noticia_lateral)
        // CONECTA o botão de Edição
        val editButton: ImageButton = view.findViewById(R.id.button_edit_lateral)

        fun bind(noticia: Noticia) {
            titulo.text = noticia.titulo
            preview.text = noticia.preview
            val context = itemView.context

            // Ação de Detalhe
            itemView.setOnClickListener {
                abrirDetalhe(noticia, context)
            }

            // LÓGICA DO LÁPIS RESTAURADA
            if (isAdmin) {
                editButton.visibility = View.VISIBLE
                editButton.setOnClickListener {
                    abrirEdicao(noticia, context)
                }
            } else {
                editButton.visibility = View.GONE
            }
        }
    }

    // --- 2. Lógica de Múltiplos Layouts (Mantida) ---

    override fun getItemViewType(position: Int): Int = listaNoticias[position].tipoLayout

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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val noticia = listaNoticias[position]

        when (holder.itemViewType) {
            TIPO_IMAGEM_GRANDE -> (holder as ImagemGrandeViewHolder).bind(noticia)
            TIPO_IMAGEM_LATERAL -> (holder as ImagemLateralViewHolder).bind(noticia)
        }
    }

    override fun getItemCount() = listaNoticias.size

    // --- 3. Funções de Navegação (Com a função de Edição RESTAURADA) ---

    private fun abrirDetalhe(noticia: Noticia, context: Context) {
        val intent = Intent(context, NoticiaDetalheActivity::class.java).apply {
            putExtra("EXTRA_TITULO", noticia.titulo)
            putExtra("EXTRA_CORPO", noticia.corpo)
            putExtra("EXTRA_IMAGEM", noticia.urlImagem)
        }
        context.startActivity(intent)
    }

    private fun abrirEdicao(noticia: Noticia, context: Context) {
        val intent = Intent(context, Adm_Tela_Criacao_Anuncio_Eventos::class.java).apply {
            putExtra("EXTRA_MODE_EDIT", true)
            putExtra("EXTRA_TITULO_EDIT", noticia.titulo)
            putExtra("EXTRA_PREVIEW_EDIT", noticia.preview)
            putExtra("EXTRA_CORPO_EDIT", noticia.corpo)
            putExtra("EXTRA_IMAGEM_URL_EDIT", noticia.urlImagem)
            putExtra("EXTRA_LAYOUT_TIPO_EDIT", noticia.tipoLayout)
            putExtra("EXTRA_ID_ITEM", noticia.urlImagem)
        }
        context.startActivity(intent)
        Toast.makeText(context, "Abrindo Edição de: ${noticia.titulo}", Toast.LENGTH_SHORT).show()
    }

    // --- 4. MÉTODO DE ATUALIZAÇÃO (Mantido) ---

    fun updateList(newList: List<Noticia>) {
        listaNoticias.clear()
        listaNoticias.addAll(newList)
        notifyDataSetChanged()
    }
}