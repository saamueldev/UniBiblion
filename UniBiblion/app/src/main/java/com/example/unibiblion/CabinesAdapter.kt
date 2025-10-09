package com.example.unibiblion

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat

class CabinesAdapter(private val context: Context, private val cabines: MutableList<Cabine>) : BaseAdapter() {

    override fun getCount(): Int = cabines.size

    override fun getItem(position: Int): Any = cabines[position]

    override fun getItemId(position: Int): Long = position.toLong()

    // Método principal: Cria e preenche a View para cada item da grade
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        // 1. Inflar o layout (item_cabine_placeholder.xml)
        val view: View = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_cabine_placeholder, parent, false)

        val cabine = cabines[position]
        val textView = view as TextView // Já que o placeholder é um TextView

        // 2. Preencher o número da cabine
        textView.text = cabine.numero

        // 3. Aplicar o estilo (Cor do background e Borda)
        val backgroundResId: Int = when {
            // A. Estado SELECIONADO (Azul, prioridade máxima)
            cabine.isSelecionada -> R.drawable.cabine_selecionada_bg
            // B. Estado OCUPADO (Vermelho)
            cabine.estado == Cabine.ESTADO_OCUPADO -> R.drawable.cabine_ocupada_bg
            // C. Estado PADRÃO (LIVRE - Verde)
            else -> R.drawable.cabine_livre_bg
        }

        // 4. Aplica o background
        textView.background = ContextCompat.getDrawable(context, backgroundResId)

        // Nota: Podemos também mudar a cor do texto se o fundo for azul, mas vamos manter o preto por enquanto.

        return view
    }

    // Método para atualizar o estado de seleção (essencial para o clique!)
    fun toggleSelection(position: Int) {
        val cabine = cabines[position]
        // Se já estiver selecionada, deseleciona. Caso contrário, seleciona.
        cabine.isSelecionada = !cabine.isSelecionada

        // Notifica o GridView para redesenhar a View naquela posição
        notifyDataSetChanged()
    }
}