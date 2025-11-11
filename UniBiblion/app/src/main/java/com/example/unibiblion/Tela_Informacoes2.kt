package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Tela_Informacoes2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_informacoes2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonConfirmarInf2 = findViewById<Button>(R.id.buttonConfirmarInf2)

        buttonConfirmarInf2.setOnClickListener {

            val intent = Intent(this, Tela_Livro_Desejado::class.java)

            startActivity(intent)


        }

        val buttonVoltarInf2 = findViewById<Button>(R.id.buttonVoltarInf2)

        buttonVoltarInf2.setOnClickListener {

            val intent = Intent(this, Tela_Informacoes1::class.java)

            startActivity(intent)

        }
    }
}
