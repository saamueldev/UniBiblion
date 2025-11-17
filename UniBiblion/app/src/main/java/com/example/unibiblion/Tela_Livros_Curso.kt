package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.io.Serializable

class Tela_Livros_Curso : AppCompatActivity(), LivroAdapter.OnItemClickListener {

    private lateinit var recyclerViewLivros: RecyclerView
    private var livroAdapter: LivroAdapter? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_livros_curso)

        val nomeCurso = intent.getStringExtra("CURSO_NOME") ?: "Curso"
        title = nomeCurso

        // Encontra o RecyclerView pelo ID (que já estava correto)
        recyclerViewLivros = findViewById(R.id.recyclerViewLivros)
        setupRecyclerView(nomeCurso)
    }

    private fun setupRecyclerView(nomeCurso: String) {
        val query: Query = db.collection("livros")
            .whereEqualTo("curso", nomeCurso)
            .orderBy("titulo", Query.Direction.ASCENDING)

        val options = FirestoreRecyclerOptions.Builder<Livro>()
            .setQuery(query, Livro::class.java)
            .build()

        // Passa o listener (this) para o adapter poder notificar o clique
        livroAdapter = LivroAdapter(options, this)

        recyclerViewLivros.layoutManager = GridLayoutManager(this, 2)
        recyclerViewLivros.adapter = livroAdapter
    }

    override fun onStart() {
        super.onStart()
        livroAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        livroAdapter?.stopListening()
    }

    // Função que é chamada quando um item do RecyclerView é clicado
    override fun onItemClick(livro: Livro) {
        val intent = Intent(this, Tela_Livro_Desejado::class.java)
        intent.putExtra("LIVRO_SELECIONADO", livro as Serializable)
        startActivity(intent)
    }
}
