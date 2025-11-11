

package com.example.unibiblion

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class ReviewsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)

        // 1. Configurar RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recycler_reviews)
        val listaReviews = criarDadosDeExemplo() // Carrega os dados de simulação

        val adapter = ReviewsAdapter(listaReviews)
        recyclerView.adapter = adapter

        // 2. Configurar o botão de Filtro (apenas a ação inicial)
        findViewById<android.widget.ImageButton>(R.id.btn_filter).setOnClickListener {
            Toast.makeText(this, "Abrir opções de Filtro", Toast.LENGTH_SHORT).show()
            // Lógica futura: Abrir um BottomSheetDialog para filtros
        }

        // 3. Configurar a Bottom Navigation (se necessário, para navegação entre telas)
        // Isso depende de como você centraliza a navegação no seu projeto
    }

    // Função de simulação de dados
    private fun criarDadosDeExemplo(): List<Review> {
        // NOTE: Substitua R.drawable.ic_profile (ou ic_user) por um drawable de usuário real
        // Se você não tem, use um ícone padrão: android.R.drawable.ic_menu_help (apenas para teste)
        val user1 = UsuarioReview("Ana Lúcia", android.R.drawable.ic_menu_help)
        val user2 = UsuarioReview("Marcos Vinicius", android.R.drawable.ic_menu_help)
        val user3 = UsuarioReview("Gabriela Dias", android.R.drawable.ic_menu_help)

        return listOf(
            Review("R1", "Introdução à Robótica", "Muito didático, o livro consegue simplificar conceitos complexos. Leitura obrigatória!", user1, 4.5f),
            Review("R2", "O Código Perdido", "Um thriller eletrizante! Não consegui largar. A reviravolta no final é genial.", user2, 5.0f),
            Review("R3", "História da Arte Moderna", "Conteúdo excelente, mas o layout poderia ser mais visual. Mesmo assim, um bom material de estudo.", user3, 3.5f),
            Review("R4", "O Código Perdido", "Li em 2 dias. Perfeito para quem ama suspense e mistério.", user1, 5.0f)
        )
    }
}