package com.example.unibiblion

import android.graphics.BitmapFactory // Necessário para criar a imagem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import android.content.Context
import android.widget.Toast
import com.example.unibiblion.Noticia.Companion.TIPO_IMAGEM_GRANDE
import com.example.unibiblion.Noticia.Companion.TIPO_IMAGEM_LATERAL

// Import nativo do Firebase Storage
import com.google.firebase.storage.FirebaseStorage

class NoticiasAdapter(
    private var listaNoticias: MutableList<Noticia>,
    private val isAdmin: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Instância do Storage para usar no download
    private val storage = FirebaseStorage.getInstance()

    // --- Função Auxiliar para Carregar Imagem (Sem Glide) ---
    private fun carregarImagemDoStorage(url: String, imageView: ImageView) {
        if (url.isNotEmpty()) {
            try {
                // 1. Cria a referência baseada na URL salva no Firestore
                val imageRef = storage.getReferenceFromUrl(url)

                // 2. Define um limite máximo de download (ex: 5MB) para evitar travar o app
                val ONE_MEGABYTE: Long = 1024 * 1024 * 5

                // 3. Baixa os bytes
                imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
                    // 4. Sucesso: Converte bytes em Bitmap e coloca na tela
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    imageView.setImageBitmap(bitmap)
                }.addOnFailureListener {
                    // Falha: Coloca o placeholder
                    imageView.setImageResource(R.drawable.placeholder_covid)
                }
            } catch (e: Exception) {
                // Se a URL for inválida (não for do Firebase), evita crash
                imageView.setImageResource(R.drawable.placeholder_covid)
            }
        } else {
            imageView.setImageResource(R.drawable.placeholder_covid)
        }
    }

    // --- ViewHolders ---

    inner class ImagemGrandeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.text_titulo_grande)
        val preview: TextView = view.findViewById(R.id.text_preview_grande)
        val imagem: ImageView = view.findViewById(R.id.img_noticia_grande)
        val editButton: ImageButton = view.findViewById(R.id.button_edit_grande)

        fun bind(noticia: Noticia) {
            titulo.text = noticia.titulo
            preview.text = noticia.preview
            val context = itemView.context

            // 🎯 CARREGAMENTO NATIVO
            carregarImagemDoStorage(noticia.urlImagem, imagem)

            itemView.setOnClickListener { abrirDetalhe(noticia, context) }

            if (isAdmin) {
                editButton.visibility = View.VISIBLE
                editButton.setOnClickListener { abrirEdicao(noticia, context) }
            } else {
                editButton.visibility = View.GONE
            }
        }
    }

    inner class ImagemLateralViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titulo: TextView = view.findViewById(R.id.text_titulo_lateral)
        val preview: TextView = view.findViewById(R.id.text_preview_lateral)
        val imagem: ImageView = view.findViewById(R.id.img_noticia_lateral)
        val editButton: ImageButton = view.findViewById(R.id.button_edit_lateral)

        fun bind(noticia: Noticia) {
            titulo.text = noticia.titulo
            preview.text = noticia.preview
            val context = itemView.context

            // 🎯 CARREGAMENTO NATIVO
            carregarImagemDoStorage(noticia.urlImagem, imagem)

            itemView.setOnClickListener { abrirDetalhe(noticia, context) }

            if (isAdmin) {
                editButton.visibility = View.VISIBLE
                editButton.setOnClickListener { abrirEdicao(noticia, context) }
            } else {
                editButton.visibility = View.GONE
            }
        }
    }

    // --- Métodos Padrão do Adapter ---

    override fun getItemViewType(position: Int): Int = listaNoticias[position].tipoLayout

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TIPO_IMAGEM_GRANDE -> ImagemGrandeViewHolder(inflater.inflate(R.layout.item_noticia_imagem_grande, parent, false))
            TIPO_IMAGEM_LATERAL -> ImagemLateralViewHolder(inflater.inflate(R.layout.item_noticia_imagem_lateral, parent, false))
            else -> throw IllegalArgumentException("Layout desconhecido")
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

    // --- Navegação ---

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
            putExtra("EXTRA_URL_IMAGEM_EDIT", noticia.urlImagem)
            putExtra("EXTRA_LAYOUT_TIPO_EDIT", noticia.tipoLayout)
            putExtra("EXTRA_ID_ITEM", noticia.id)
        }
        context.startActivity(intent)
    }

    // --- Filtro ---
    fun filtrar(termo: String, listaCompleta: List<Noticia>) {
        val termoNormalizado = termo.lowercase().trim()
        listaNoticias.clear()
        if (termoNormalizado.isEmpty()) {
            listaNoticias.addAll(listaCompleta)
        } else {
            listaNoticias.addAll(listaCompleta.filter {
                it.titulo.lowercase().contains(termoNormalizado) ||
                        it.preview.lowercase().contains(termoNormalizado)
            })
        }
        notifyDataSetChanged()
    }

    fun updateList(newList: List<Noticia>) {
        listaNoticias.clear()
        listaNoticias.addAll(newList)
        notifyDataSetChanged()
    }
}