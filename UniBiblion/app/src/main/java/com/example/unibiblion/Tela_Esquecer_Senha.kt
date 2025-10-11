package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Tela_Esquecer_Senha : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_esquecer_senha)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Conecta o botão de Enviar Código à próxima tela
        // **ATENÇÃO:** Use o ID correto do botão no seu XML (ex: button_send_code)
        val sendCodeButton: Button = findViewById(R.id.button_send_code)
        sendCodeButton.setOnClickListener {
            val intent = Intent(this, Tela_Codigo_Nova_Senha::class.java)
            startActivity(intent)
        }
    }
}