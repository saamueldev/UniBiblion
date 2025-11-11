package com.example.unibiblion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast

class Adm_Tela_Notificacoes : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adm_tela_notificacoes)

        val buttonAdd: ImageButton = findViewById(R.id.buttonAdd)

        buttonAdd.setOnClickListener {
            val intent = Intent(this, Adm_Criar_Notificacao::class.java)

            startActivity(intent)

        }
    }
}