package com.example.unibiblion

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat

class CabinesAdapter(private val context: Context, private val cabines: MutableList<Cabine>) : BaseAdapter() {

    private var selectedPosition: Int = -1

    override fun getCount(): Int = cabines.size
    override fun getItem(position: Int): Any = cabines[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_cabine_placeholder, parent, false)

        val cabine = cabines[position]
        val textView = view as TextView

        // Lendo o número da cabine diretamente
        textView.text = cabine.numero

        // 3. Lógica de Estilo
        val backgroundResId: Int = when {
            // A. ESTADO SELECIONADO
            position == selectedPosition -> R.drawable.cabine_selecionada_bg

            // B. ESTADO OCUPADO (Compara diretamente o estado da Cabine)
            cabine.estado == Cabine.ESTADO_OCUPADO -> R.drawable.cabine_ocupada_bg

            // C. ESTADO PADRÃO (LIVRE)
            else -> R.drawable.cabine_livre_bg
        }

        textView.background = ContextCompat.getDrawable(context, backgroundResId)

        return view
    }

    // 🎯 NOVO MÉTODO PARA ATUALIZAR A LISTA EM TEMPO REAL
    fun updateCabines(newCabines: List<Cabine>) {
        this.cabines.clear()
        this.cabines.addAll(newCabines)
        notifyDataSetChanged() // Força o redesenho do GridView
    }

    fun selectSingleCabine(position: Int) {
        if (selectedPosition == position) {
            selectedPosition = -1
        } else {
            selectedPosition = position
        }
        notifyDataSetChanged()
    }

    fun getSelectedPosition(): Int {
        return selectedPosition
    }
}