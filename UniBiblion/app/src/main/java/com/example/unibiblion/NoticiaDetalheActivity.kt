package com.example.unibiblion

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
// 🎯 NOVO IMPORT NECESSÁRIO
import com.bumptech.glide.Glide

class NoticiaDetalheActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noticia_detalhe)

        // 1. Opcional: Ativa o botão de voltar na barra superior
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        // 2. Receber os Dados da Intent
        val titulo = intent.getStringExtra("EXTRA_TITULO")
        val corpo = intent.getStringExtra("EXTRA_CORPO")
        val urlImagem = intent.getStringExtra("EXTRA_IMAGEM") // Recebemos a URL!

        // 3. Obter as Views do Layout
        val imgDetalhe: ImageView = findViewById(R.id.img_detalhe)
        val textTitulo: TextView = findViewById(R.id.text_titulo_detalhe)
        val textCorpo: TextView = findViewById(R.id.text_corpo_detalhe)

        // 4. Preencher as Views com os Dados
        textTitulo.text = titulo ?: "Erro ao carregar título"
        textCorpo.text = corpo ?: "Não foi possível carregar o conteúdo da notícia."

        // 🎯 LÓGICA DO GLIDE: Carrega a imagem de destaque
        if (!urlImagem.isNullOrEmpty()) {
            Glide.with(this)
                .load(urlImagem) // Carrega a URL da imagem
                .placeholder(R.drawable.placeholder_covid) // Placeholder opcional
                .error(R.drawable.placeholder_covid) // Em caso de falha
                .into(imgDetalhe) // O ImageView de destino
        } else {
            // Se a URL for nula ou vazia, usa apenas o placeholder estático
            imgDetalhe.setImageResource(R.drawable.placeholder_covid)
        }
    }

    // Método para fazer a seta 'Voltar' funcionar corretamente
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}