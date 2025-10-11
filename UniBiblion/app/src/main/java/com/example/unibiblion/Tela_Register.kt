package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Tela_Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_register) // Carrega seu layout XML

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Conecta o botão de Cadastro/Avançar
        // O ID do XML é: button_avancar
        val registerButton: Button = findViewById(R.id.button_avancar)
        registerButton.setOnClickListener {

            // Lógica de Validação iria aqui.

            // Navegação: Simulando cadastro bem-sucedido e voltando para a tela de seleção inicial
            val intent = Intent(this, Tela_Login_Resgister::class.java)

            // FLAG_ACTIVITY_CLEAR_TASK fecha a Tela_Register, completando o ciclo.
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}