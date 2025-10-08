package com.example.unibiblion

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NoticiaDetalheActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noticia_detalhe)

        // 1. Opcional: Ativa o botão de voltar na barra superior
        // Isso melhora a navegação em telas de detalhe.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "" // Deixa o título vazio, o título da notícia já é grande.

        // 2. Receber os Dados da Intent
        // Usamos as chaves que definiremos no próximo passo (Parte 3)
        val titulo = intent.getStringExtra("EXTRA_TITULO")
        val corpo = intent.getStringExtra("EXTRA_CORPO")
        val urlImagem = intent.getStringExtra("EXTRA_IMAGEM")

        // 3. Obter as Views do Layout
        val imgDetalhe: ImageView = findViewById(R.id.img_detalhe)
        val textTitulo: TextView = findViewById(R.id.text_titulo_detalhe)
        val textCorpo: TextView = findViewById(R.id.text_corpo_detalhe)

        // 4. Preencher as Views com os Dados
        // O operador Elvis (?: "") garante que, se o dado for nulo, ele use uma string vazia.
        textTitulo.text = titulo ?: "Erro ao carregar título"
        textCorpo.text = corpo ?: "Não foi possível carregar o conteúdo da notícia."

        // Carrega o placeholder por enquanto. Se usássemos Glide, a URL seria usada aqui.
        imgDetalhe.setImageResource(R.drawable.placeholder_covid)
    }

    // Método para fazer a seta 'Voltar' funcionar corretamente
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}