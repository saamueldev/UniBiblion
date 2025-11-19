package com.example.unibiblion

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CriarReviewActivity : AppCompatActivity() {

    // Chaves para passar dados do Livro entre Activities
    companion object {
        const val EXTRA_LIVRO_ID = "extra_livro_id"
        const val EXTRA_LIVRO_TITULO = "extra_livro_titulo"
    }

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var tvBookTitle: TextView
    private lateinit var ratingInput: RatingBar
    private lateinit var etReviewText: EditText
    private lateinit var btnSubmitReview: Button

    private var livroId: String? = null
    private var livroTitulo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_criar_review)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        tvBookTitle = findViewById(R.id.tv_book_title)
        ratingInput = findViewById(R.id.rating_input)
        etReviewText = findViewById(R.id.et_review_text)
        btnSubmitReview = findViewById(R.id.btn_submit_review)

        // 1. Receber dados do Livro da Activity anterior
        livroId = intent.getStringExtra(EXTRA_LIVRO_ID)
        livroTitulo = intent.getStringExtra(EXTRA_LIVRO_TITULO)

        if (livroId.isNullOrEmpty() || livroTitulo.isNullOrEmpty()) {
            Toast.makeText(this, "Erro: Livro não identificado.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        tvBookTitle.text = "Livro: $livroTitulo"

        // 2. Configurar o botão de envio
        btnSubmitReview.setOnClickListener {
            enviarReviewParaFirebase()
        }
    }

    private fun enviarReviewParaFirebase() {
        val rating = ratingInput.rating
        val reviewText = etReviewText.text.toString().trim()
        val userId = auth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(this, "Você precisa estar logado para enviar uma review.", Toast.LENGTH_SHORT).show()
            return
        }

        if (rating == 0f) {
            Toast.makeText(this, "Por favor, selecione uma nota.", Toast.LENGTH_SHORT).show()
            return
        }

        if (reviewText.isEmpty()) {
            Toast.makeText(this, "Escreva sua avaliação para enviar.", Toast.LENGTH_SHORT).show()
            return
        }

        // 🚨 Assumindo que você tem um perfil de usuário para pegar o nome
        val userName = auth.currentUser?.displayName ?: "Usuário Anônimo"
        val userPhotoUrl = auth.currentUser?.photoUrl.toString()

        // Criar o objeto Review
        val newReview = Review(
            livroId = livroId!!,
            userId = userId,
            userName = userName,
            userPhotoUrl = userPhotoUrl,
            livroTitulo = livroTitulo!!,
            textoReview = reviewText,
            rating = rating,
            timestamp = Timestamp.now()
        )

        // 3. Enviar para a coleção 'reviews' no Firebase
        db.collection("reviews")
            .add(newReview)
            .addOnSuccessListener {
                Toast.makeText(this, "Avaliação enviada com sucesso!", Toast.LENGTH_LONG).show()
                // Fechar a tela após o sucesso
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao enviar avaliação: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}