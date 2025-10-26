package com.example.unibiblion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.Button

class Tela_Central_Livraria : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_central_livraria)

        val buttonAcervo = findViewById<Button>(R.id.buttonGerenciarCatalogo)
        val buttonLivros = findViewById<Button>(R.id.buttonGerenciarCabines)
        val buttonSala = findViewById<Button>(R.id.buttonGerenciamentoGeral)
        val buttonRenovar = findViewById<Button>(R.id.buttonRenovarLivros)

        buttonAcervo.setOnClickListener {

            val intent = Intent(this, Tela_Acervo_Livros::class.java)

            startActivity(intent)
        }

        buttonLivros.setOnClickListener {

            val intent = Intent(this, Tela_Livros_Curso::class.java)

            startActivity(intent)
        }

        buttonSala.setOnClickListener {

            val intent = Intent(this, Tela_Reservas_Cabines_Individuais::class.java)

            startActivity(intent)
        }

        buttonRenovar.setOnClickListener {

            val intent = Intent(this, Tela_Renovacao_Livros::class.java)

            startActivity(intent)
        }
    }
}