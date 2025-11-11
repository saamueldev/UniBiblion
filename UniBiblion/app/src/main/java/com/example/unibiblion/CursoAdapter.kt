package com.example.unibiblion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CursoAdapter(private val listaCursos: List<Curso>) :
    RecyclerView.Adapter<CursoAdapter.CursoViewHolder>() {

    // Interface ou função lambda para lidar com cliques
    var onItemClick: ((Curso) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CursoViewHolder {
        // Infla o layout padrão do Android: simple_list_item_1
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return CursoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CursoViewHolder, position: Int) {
        val curso = listaCursos[position]
        holder.bind(curso)
    }

    override fun getItemCount(): Int = listaCursos.size

    inner class CursoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // O layout simple_list_item_1 tem um TextView com o ID android.R.id.text1
        private val nomeCurso: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(curso: Curso) {
            nomeCurso.text = curso.nome

            // Define o clique para retornar o objeto Curso
            itemView.setOnClickListener {
                onItemClick?.invoke(curso)
            }
        }
    }
}