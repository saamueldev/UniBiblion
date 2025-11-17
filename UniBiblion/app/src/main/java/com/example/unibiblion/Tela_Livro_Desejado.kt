package com.example.unibiblion

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import java.io.Serializable

class Tela_Livro_Desejado : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_livro_desejado)

        val livro = getSerializable(intent, "LIVRO_SELECIONADO", Livro::class.java)

        val imgCapa: ImageView = findViewById(R.id.img_detalhe_capa)
        val txtTitulo: TextView = findViewById(R.id.tv_detalhe_titulo)
        val txtAutor: TextView = findViewById(R.id.tv_detalhe_autor)
        val txtEstado: TextView = findViewById(R.id.tv_detalhe_estado)
        val txtIdioma: TextView = findViewById(R.id.tv_detalhe_idioma)
        val txtEstoque: TextView = findViewById(R.id.tv_detalhe_estoque)
        val txtResumo: TextView = findViewById(R.id.tv_detalhe_resumo)
        val btnAlugar: MaterialButton = findViewById(R.id.btn_detalhe_alugar)
        val btnReviews: MaterialButton = findViewById(R.id.btn_detalhe_reviews)

        if (livro != null) {
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

            // --- LÓGICA DE VISIBILIDADE DOS BOTÕES ---
            if (livro.qEstoque > 0) {
                // Se tem estoque, mostra os dois botões
                btnAlugar.visibility = View.VISIBLE
                btnReviews.visibility = View.VISIBLE
            } else {
                // Se NÃO tem estoque, esconde o botão de alugar e mostra SÓ o de reviews
                btnAlugar.visibility = View.GONE
                btnReviews.visibility = View.VISIBLE
            }

            // AÇÃO DO BOTÃO DE ALUGAR
            btnAlugar.setOnClickListener {
                val intentAlugar = Intent(this, Tela_Informacoes1::class.java)
                // Opcional: passe informações do livro para a próxima tela
                // intentAlugar.putExtra("LIVRO_SELECIONADO", livro)
                startActivity(intentAlugar)
            }

            // AÇÃO DO BOTÃO DE REVIEWS
            btnReviews.setOnClickListener {
                val intentReviews = Intent(this, Tela_Review_Livro::class.java)
                // Opcional: passe o ID ou título do livro para a tela de reviews
                // intentReviews.putExtra("LIVRO_TITULO", livro.titulo)
                startActivity(intentReviews)
            }

        } else {
            // Caso de erro: o livro não foi passado corretamente.
            title = "Erro"
            txtTitulo.text = "Livro não encontrado."
            finish()
        }
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
