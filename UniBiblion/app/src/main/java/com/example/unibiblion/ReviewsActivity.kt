package com.example.unibiblion

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.example.unibiblion.ReviewsAdapter
import com.example.unibiblion.CriarReviewActivity
class ReviewsActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private var livroId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)

        firestore = FirebaseFirestore.getInstance()

        // 1. Receber o ID do Livro (Passado pela Tela_Livro_Desejado.kt)
        // Usamos a constante EXTRA_LIVRO_ID definida na CriarReviewActivity.
        livroId = intent.getStringExtra(CriarReviewActivity.EXTRA_LIVRO_ID)

        if (livroId.isNullOrEmpty()) {
            Toast.makeText(this, "Erro: ID do livro não encontrado. Não é possível carregar reviews.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        recyclerView = findViewById(R.id.recycler_reviews)
        // 2. Configura LayoutManager e Adapters
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 3. Configurar o botão de filtro e navegação (apenas a ação inicial)
        findViewById<android.widget.ImageButton>(R.id.btn_filter).setOnClickListener {
            Toast.makeText(this, "Abrir opções de Filtro e Busca de Reviews", Toast.LENGTH_SHORT).show()
        }

        // 4. Iniciar a busca no Firebase
        loadReviewsFromFirestore()
    }

    private fun loadReviewsFromFirestore() {
        // 🔑 Cria a Query: Filtra pela coleção 'reviews' onde 'livroId' é igual ao ID recebido
        val query: Query = firestore.collection("reviews")
            .whereEqualTo("livroId", livroId) // Filtro essencial para o livro correto
            .orderBy("timestamp", Query.Direction.DESCENDING) // Ordena pela mais recente

        query.get()
            .addOnSuccessListener { snapshots ->
                val listaReviews = mutableListOf<Review>()

                // Itera sobre os resultados e mapeia para o seu modelo Review.kt
                for (doc in snapshots) {
                    val review = doc.toObject(Review::class.java)

                    // Nota: O campo 'id' do Review.kt é opcional, mas se necessário,
                    // você pode preenchê-lo aqui: review.id = doc.id

                    listaReviews.add(review)
                }

                if (listaReviews.isEmpty()) {
                    Toast.makeText(this, "Nenhuma avaliação encontrada para este livro.", Toast.LENGTH_LONG).show()
                    // Exibir uma view de "sem reviews" se você tiver uma no layout
                }

                // 5. Atribui a lista filtrada ao seu ReviewsAdapter existente
                val adapter = ReviewsAdapter(listaReviews)
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Falha ao carregar avaliações: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    // Você pode remover a função 'criarDadosDeExemplo' se ela ainda existir.
}