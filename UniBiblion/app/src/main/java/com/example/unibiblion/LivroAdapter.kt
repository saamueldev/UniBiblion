package com.example.unibiblion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import java.util.Locale

class LivroAdapter(options: FirestoreRecyclerOptions<Livro>) :
    FirestoreRecyclerAdapter<Livro, LivroAdapter.LivroViewHolder>(options), Filterable {

    // Lista de backup (modificável) para guardar todos os itens originais.
    private val listaCompleta = ArrayList<Livro>()
    // Lista (modificável) que será exibida e alterada pelo filtro.
    private val listaFiltrada = ArrayList<Livro>()

    /**
     * *** CORREÇÃO PRINCIPAL ***
     * Este método é chamado automaticamente pelo FirebaseUI quando os dados são carregados.
     * É o momento perfeito para popular nossas listas.
     */
    override fun onDataChanged() {
        super.onDataChanged()
        // Limpa as listas para o caso de uma atualização de dados.
        listaCompleta.clear()
        // A lista 'snapshots' do adapter original contém todos os dados do Firestore.
        listaCompleta.addAll(snapshots)

        // Popula a lista visível (filtrada) com todos os itens para a exibição inicial.
        listaFiltrada.clear()
        listaFiltrada.addAll(snapshots)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: LivroViewHolder, position: Int, model: Livro) {
        // Agora usamos a lista filtrada para obter o item correto.
        holder.bind(listaFiltrada[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LivroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_livro, parent, false)
        return LivroViewHolder(view)
    }

    // O adapter agora usa o tamanho da lista filtrada para saber quantos itens desenhar.
    override fun getItemCount(): Int {
        return listaFiltrada.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val textoFiltrado = constraint.toString().lowercase(Locale.ROOT).trim()
                val resultadosDaBusca = ArrayList<Livro>()

                if (textoFiltrado.isEmpty()) {
                    // Se a pesquisa estiver vazia, retorna a lista completa de backup.
                    resultadosDaBusca.addAll(listaCompleta)
                } else {
                    // Caso contrário, filtra a partir da lista completa.
                    for (livro in listaCompleta) {
                        if (livro.titulo.lowercase(Locale.ROOT).contains(textoFiltrado)) {
                            resultadosDaBusca.add(livro)
                        }
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = resultadosDaBusca
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                // Limpa e preenche nossa lista filtrada com os resultados da busca.
                listaFiltrada.clear()
                if (results?.values is List<*>) {
                    listaFiltrada.addAll(results.values as List<Livro>)
                }
                // Notifica o RecyclerView que os dados mudaram.
                notifyDataSetChanged()
            }
        }
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
