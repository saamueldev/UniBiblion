package com.example.unibiblion

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat

// Mude o construtor para aceitar uma lista imutável e crie uma VAR mutável interna
class CabinesAdapter(private val context: Context, initialCabines: List<Cabine>) : BaseAdapter() {

    // ➡️ CRUCIAL: Mude de 'val' para 'var' e inicialize com uma MutableList
    private var cabines: MutableList<Cabine> = initialCabines.toMutableList()
    private var selectedPosition: Int = -1
    private val TAG = "CabinesAdapterDebug"

    private class ViewHolder(val textView: TextView)

    override fun getCount(): Int = cabines.size
    override fun getItem(position: Int): Any = cabines[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        // ... (o código do getView permanece igual e correto com o ViewHolder) ...
        val view: View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context)
                .inflate(R.layout.item_cabine_placeholder, parent, false)

            viewHolder = ViewHolder(view as TextView)
            view.tag = viewHolder
            Log.v(TAG, "Criando nova View e ViewHolder para posição $position.")
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
            Log.v(TAG, "Reutilizando View para posição $position.")
        }

        val cabine = cabines[position]

        viewHolder.textView.text = cabine.numero

        val backgroundResId: Int = when {
            position == selectedPosition -> R.drawable.cabine_selecionada_bg
            cabine.estado == Cabine.ESTADO_OCUPADO -> R.drawable.cabine_ocupada_bg
            else -> R.drawable.cabine_livre_bg
        }

        viewHolder.textView.background = ContextCompat.getDrawable(context, backgroundResId)

        return view
    }

    // 🎯 Método corrigido para debug: Garante a repopulação correta
    fun updateCabines(newCabines: List<Cabine>) {

        Log.d(TAG, "updateCabines chamado. Tamanho ANTERIOR: ${this.cabines.size}. Novo tamanho: ${newCabines.size}")

        this.cabines.clear() // Esvazia a lista
        this.cabines.addAll(newCabines) // Repopula a lista

        Log.d(TAG, "Lista do Adapter atualizada. Tamanho ATUAL: ${this.cabines.size}") // Deve ser 25

        if (this.cabines.size == 0) {
            Log.e(TAG, "ERRO CRÍTICO: Lista do Adapter está VAZIA após addAll. Isso fará o GridView sumir.")
        }

        notifyDataSetChanged()
        Log.d(TAG, "notifyDataSetChanged chamado para redesenhar o GridView.")
    }

    // ... (restante dos métodos selectSingleCabine e getSelectedPosition) ...
    fun selectSingleCabine(position: Int) {
        if (selectedPosition == position) {
            selectedPosition = -1
        } else {
            selectedPosition = position
        }
        Log.d(TAG, "Seleção alterada para posição $selectedPosition.")
        notifyDataSetChanged()
    }

    fun getSelectedPosition(): Int {
        return selectedPosition
    }
}