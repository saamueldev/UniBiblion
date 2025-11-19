package com.example.unibiblion

import android.graphics.BitmapFactory // Importante
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage // Importante

class NoticiaDetalheActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noticia_detalhe)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        val titulo = intent.getStringExtra("EXTRA_TITULO")
        val corpo = intent.getStringExtra("EXTRA_CORPO")
        val urlImagem = intent.getStringExtra("EXTRA_IMAGEM")

        val imgDetalhe: ImageView = findViewById(R.id.img_detalhe)
        val textTitulo: TextView = findViewById(R.id.text_titulo_detalhe)
        val textCorpo: TextView = findViewById(R.id.text_corpo_detalhe)

        textTitulo.text = titulo ?: "Erro"
        textCorpo.text = corpo ?: ""

        // 🎯 CARREGAMENTO NATIVO DO FIREBASE STORAGE (Sem Glide)
        if (!urlImagem.isNullOrEmpty()) {
            try {
                val storage = FirebaseStorage.getInstance()
                val imageRef = storage.getReferenceFromUrl(urlImagem)

                // Limite de 5MB para a imagem de detalhe
                val MAX_BYTES: Long = 1024 * 1024 * 5

                imageRef.getBytes(MAX_BYTES).addOnSuccessListener { bytes ->
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    imgDetalhe.setImageBitmap(bitmap)
                }.addOnFailureListener {
                    // Falha no download
                    imgDetalhe.setImageResource(R.drawable.placeholder_covid)
                }
            } catch (e: Exception) {
                // URL inválida
                imgDetalhe.setImageResource(R.drawable.placeholder_covid)
            }
        } else {
            imgDetalhe.setImageResource(R.drawable.placeholder_covid)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}