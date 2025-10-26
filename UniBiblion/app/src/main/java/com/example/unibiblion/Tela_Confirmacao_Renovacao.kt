package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Tela_Confirmacao_Renovacao : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_confirmacao_renovacao)

        val btnConfirmar = findViewById<Button>(R.id.btn_confirmar_renovacao)
        val btnCancelar = findViewById<Button>(R.id.btn_cancelar_renovacao)

        btnConfirmar.setOnClickListener {

            val intent = Intent(this, Tela_Renovacao_Livros::class.java)
            startActivity(intent)


            finish()
        }

        btnCancelar.setOnClickListener {

            val intent = Intent(this, Tela_Renovacao_Livros::class.java)
            startActivity(intent)


            finish()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}