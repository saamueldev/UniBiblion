package com.example.unibiblion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class RelatorioLivroAdapter(
    private var listaRelatorios: List<RelatorioLivro>
) : RecyclerView.Adapter<RelatorioLivroAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgCapa: ImageView = itemView.findViewById(R.id.img_capa_livro)
        val tvTitulo: TextView = itemView.findViewById(R.id.tv_titulo_livro)
        val tvNomeUsuario: TextView = itemView.findViewById(R.id.tv_nome_usuario)
        val tvDataRetirada: TextView = itemView.findViewById(R.id.tv_data_retirada)
        val tvDataDevolucao: TextView = itemView.findViewById(R.id.tv_data_devolucao)
        val tvStatusAluguel: TextView = itemView.findViewById(R.id.tv_status_aluguel)
        val tvStatusRenovado: TextView = itemView.findViewById(R.id.tv_status_renovado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_relatorio_livro, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val relatorio = listaRelatorios[position]
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Carrega a capa do livro
        if (relatorio.capaURL.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(relatorio.capaURL)
                .placeholder(R.drawable.sommervile)
                .into(holder.imgCapa)
        } else {
            holder.imgCapa.setImageResource(R.drawable.sommervile)
        }

        // Define os textos
        holder.tvTitulo.text = relatorio.tituloLivro
        holder.tvNomeUsuario.text = "👤 Alugado por: ${relatorio.nomeUsuario}"
        holder.tvDataRetirada.text = "📅 Retirada: ${relatorio.dataRetirada} às ${relatorio.horarioRetirada}"
        
        // Formata a data de devolução
        relatorio.dataDevolucao?.let { timestamp ->
            holder.tvDataDevolucao.text = "📅 Devolução: ${sdf.format(timestamp.toDate())}"
        } ?: run {
            holder.tvDataDevolucao.text = "📅 Devolução: Data não definida"
        }

        // Calcula e exibe o status do aluguel
        calcularEExibirStatus(holder, relatorio)

        // Exibe status de renovado
        if (relatorio.renovado) {
            holder.tvStatusRenovado.visibility = View.VISIBLE
        } else {
            holder.tvStatusRenovado.visibility = View.GONE
        }
    }

    /**
     * Calcula o status do aluguel baseado nas datas e exibe no TextView
     */
    private fun calcularEExibirStatus(holder: ViewHolder, relatorio: RelatorioLivro) {
        val agora = System.currentTimeMillis()
        val dataDevolucaoPrevista = relatorio.dataDevolucao?.toDate()?.time
        
        when {
            // CONCLUÍDO: Livro já foi devolvido
            relatorio.devolvido -> {
                holder.tvStatusAluguel.text = "CONCLUÍDO"
                holder.tvStatusAluguel.setBackgroundResource(R.drawable.rounded_edittext_background)
                holder.tvStatusAluguel.setTextColor(android.graphics.Color.parseColor("#4CAF50")) // Verde
            }
            // ATRASADO: Passou do prazo e não devolveu
            dataDevolucaoPrevista != null && agora > dataDevolucaoPrevista -> {
                holder.tvStatusAluguel.text = "ATRASADO"
                holder.tvStatusAluguel.setBackgroundResource(R.drawable.rounded_edittext_background_red)
                holder.tvStatusAluguel.setTextColor(android.graphics.Color.parseColor("#D32F2F")) // Vermelho
            }
            // ATIVO: Ainda está dentro do prazo
            else -> {
                holder.tvStatusAluguel.text = "ATIVO"
                holder.tvStatusAluguel.setBackgroundResource(R.drawable.bg_button_purple)
                holder.tvStatusAluguel.setTextColor(android.graphics.Color.WHITE) // Branco
            }
        }
    }

    override fun getItemCount(): Int = listaRelatorios.size

    /**
     * Atualiza a lista de relatórios e notifica o adapter
     */
    fun atualizarLista(novaLista: List<RelatorioLivro>) {
        android.util.Log.d("RelatorioLivroAdapter", "Atualizando lista: ${novaLista.size} itens")
        listaRelatorios = novaLista
        notifyDataSetChanged()
    }

    /**
     * Retorna a lista atual de livros
     */
    fun getListaLivros(): List<RelatorioLivro> = listaRelatorios
}
