package com.example.unibiblion

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import java.io.Serializable

class Tela_Livro_Desejado : AppCompatActivity() {
    private lateinit var btnEscreverReview: MaterialButton
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_livro_desejado)

        bottomNavigation = findViewById(R.id.bottom_navigation)

        val livro = getSerializable(intent, "LIVRO_SELECIONADO", Livro::class.java)

        // Validação crucial para evitar crashes
        if (livro == null) {
            Toast.makeText(this, "Erro: Não foi possível carregar os dados do livro.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val imgCapa: ImageView = findViewById(R.id.img_detalhe_capa)
        val txtTitulo: TextView = findViewById(R.id.tv_detalhe_titulo)
        val txtAutor: TextView = findViewById(R.id.tv_detalhe_autor)
        val txtEstado: TextView = findViewById(R.id.tv_detalhe_estado)
        val txtIdioma: TextView = findViewById(R.id.tv_detalhe_idioma)
        val txtEstoque: TextView = findViewById(R.id.tv_detalhe_estoque)
        val txtResumo: TextView = findViewById(R.id.tv_detalhe_resumo)
        val btnAlugar: MaterialButton = findViewById(R.id.btn_detalhe_alugar)
        val btnReviews: MaterialButton = findViewById(R.id.btn_detalhe_reviews)
        btnEscreverReview = findViewById(R.id.btn_detalhe_escrever_review)

        // Preenche a UI com os dados do livro
        title = livro.titulo
        txtTitulo.text = livro.titulo
        txtAutor.text = "por ${livro.autor}"
        txtEstado.text = "Estado: ${livro.estado}"
        txtIdioma.text = "Idioma: ${livro.idioma}"
        txtEstoque.text = "Estoque: ${livro.qEstoque}"
        txtResumo.text = livro.resumo

        if (livro.capaUrl.isNotEmpty()) {
            Glide.with(this)
                .load(livro.capaUrl)
                .placeholder(R.drawable.sommervile)
                .error(R.drawable.sommervile)
                .into(imgCapa)
        } else {
            imgCapa.setImageResource(R.drawable.sommervile)
        }

        if (livro.qEstoque > 0) {
            btnAlugar.visibility = View.VISIBLE
            btnReviews.visibility = View.VISIBLE
            btnEscreverReview.visibility = View.VISIBLE
        } else {
            btnAlugar.visibility = View.GONE
            btnReviews.visibility = View.VISIBLE
            btnEscreverReview.visibility = View.VISIBLE
        }

        // AÇÃO DO BOTÃO DE ALUGAR
        btnAlugar.setOnClickListener {
            val intentAlugar = Intent(this, Tela_Informacoes1::class.java).apply {
                // Envia o objeto LIVRO INTEIRO para a próxima tela
                putExtra("LIVRO_SELECIONADO", livro)
            }
            startActivity(intentAlugar)
        }

        // AÇÃO DO BOTÃO DE REVIEWS
        btnReviews.setOnClickListener {
            val intentReviews = Intent(this, ReviewsActivity::class.java)
            intentReviews.putExtra(CriarReviewActivity.EXTRA_LIVRO_ID, livro.id)
            startActivity(intentReviews)
        }

        // AÇÃO DO BOTÃO DE ESCREVER REVIEW
        btnEscreverReview.setOnClickListener {
            val intentCriarReview = Intent(this, CriarReviewActivity::class.java)
            intentCriarReview.putExtra(CriarReviewActivity.EXTRA_LIVRO_ID, livro.id)
            intentCriarReview.putExtra(CriarReviewActivity.EXTRA_LIVRO_TITULO, livro.titulo)
            startActivity(intentCriarReview)
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_livraria -> {
                    val intent = Intent(this, Tela_Central_Livraria::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_noticias -> {
                    val intent = Intent(this, NoticiasActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_chatbot -> {
                    val intent = Intent(this, Tela_Chat_Bot::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_perfil -> {
                    val intent = Intent(this, Tela_De_Perfil::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        bottomNavigation.menu.findItem(R.id.nav_livraria).isChecked = true
    }

    private fun <T : Serializable?> getSerializable(intent: Intent, key: String, clazz: Class<T>): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(key, clazz)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(key) as? T
        }
    }
}
