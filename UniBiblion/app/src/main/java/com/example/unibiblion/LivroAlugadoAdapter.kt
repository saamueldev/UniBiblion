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
import com.google.firebase.Timestamp // Importa a classe Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

// *** CORREÇÃO: Altera o tipo de 'dataDevolucao' para Timestamp? ***
data class LivroAlugado(
    val titulo: String = "",
    val capaURL: String = "",
    val dataDevolucao: Timestamp? = null, // Agora é um Timestamp que pode ser nulo
    val renovado: Boolean = false
)

class LivroAlugadoAdapter(
    options: FirestoreRecyclerOptions<LivroAlugado>,
    private val listener: OnItemClickListener
) : FirestoreRecyclerAdapter<LivroAlugado, LivroAlugadoAdapter.LivroAlugadoViewHolder>(options) {

    interface OnItemClickListener {
        fun onItemClick(documentId: String, livro: LivroAlugado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LivroAlugadoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_livro_renovacao, parent, false)
        return LivroAlugadoViewHolder(view)
    }

    override fun onBindViewHolder(holder: LivroAlugadoViewHolder, position: Int, model: LivroAlugado) {
        val documentId = snapshots.getSnapshot(position).id
        holder.bind(documentId, model, listener)
    }

    class LivroAlugadoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tituloTextView: TextView = itemView.findViewById(R.id.txtTituloRenovacao)
        private val dataDevolucaoTextView: TextView = itemView.findViewById(R.id.txtDataDevolucao)
        private val capaImageView: ImageView = itemView.findViewById(R.id.imgCapaRenovacao)

        fun bind(documentId: String, livro: LivroAlugado, listener: OnItemClickListener) {
            tituloTextView.text = livro.titulo

            // *** CORREÇÃO: Formata o Timestamp para exibição ***
            livro.dataDevolucao?.let { timestamp ->
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                dataDevolucaoTextView.text = sdf.format(timestamp.toDate())
            } ?: run {
                dataDevolucaoTextView.text = "Data inválida"
            }

            if (livro.capaURL.isNotEmpty()) {
                Glide.with(itemView.context).load(livro.capaURL).placeholder(R.drawable.sommervile).into(capaImageView)
            } else {
                capaImageView.setImageResource(R.drawable.sommervile)
            }

            itemView.setOnClickListener {
                listener.onItemClick(documentId, livro)
            }
        }
    }
}
