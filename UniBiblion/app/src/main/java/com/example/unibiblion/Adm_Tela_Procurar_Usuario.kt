package com.example.unibiblion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import com.example.unibiblion.Adm_Tela_Perfil_Usuario

class Adm_Tela_Procurar_Usuario : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adm_tela_procurar_usuario)

        val userCard = findViewById<CardView>(R.id.userCard)

        userCard.setOnClickListener {
            val intent = Intent(this, Adm_Tela_Perfil_Usuario::class.java)

            startActivity(intent)
        }
    }
}