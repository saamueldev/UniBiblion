package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Tela_Codigo_Nova_Senha : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_codigo_nova_senha)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Conecta o botão de Avançar
        // O ID do XML é: button_verify_and_next
        val nextButton: Button = findViewById(R.id.button_verify_and_next)
        nextButton.setOnClickListener {

            // Lógica de verificação do código iria aqui.

            // Navegação: Leva para a tela final de definição da nova senha
            val intent = Intent(this, Tela_Colocar_Nova_Senha::class.java)
            startActivity(intent)
        }
    }
}