package com.example.unibiblion

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat

class CabinesAdapter(private val context: Context, private val cabines: MutableList<Cabine>) : BaseAdapter() {

    // NOVO: Variável para rastrear a posição da cabine atualmente selecionada
    private var selectedPosition: Int = -1

    override fun getCount(): Int = cabines.size
    override fun getItem(position: Int): Any = cabines[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_cabine_placeholder, parent, false)

        val cabine = cabines[position]
        val textView = view as TextView

        textView.text = cabine.numero

        // 3. Lógica de Estilo (Agora usa a variável 'selectedPosition')
        val backgroundResId: Int = when {
            // A. ESTADO SELECIONADO: Se a posição atual for a posição selecionada
            position == selectedPosition -> R.drawable.cabine_selecionada_bg
            // B. ESTADO OCUPADO
            cabine.estado == Cabine.ESTADO_OCUPADO -> R.drawable.cabine_ocupada_bg
            // C. ESTADO PADRÃO (LIVRE)
            else -> R.drawable.cabine_livre_bg
        }

        textView.background = ContextCompat.getDrawable(context, backgroundResId)

        return view
    }

    // NOVO MÉTODO: Seleciona uma única cabine e deseleciona a anterior
    fun selectSingleCabine(position: Int) {

        if (selectedPosition == position) {
            // Se o usuário clicar na mesma cabine, DESSELECIONA
            selectedPosition = -1 // -1 significa nenhuma selecionada
        } else {
            // Se for uma cabine diferente, seleciona a nova
            selectedPosition = position
        }

        // Redesenha toda a grade para aplicar as mudanças de cor
        notifyDataSetChanged()
    }

    // Remova ou ignore o antigo método 'toggleSelection'
    fun getSelectedPosition(): Int {
        return selectedPosition
    }
}