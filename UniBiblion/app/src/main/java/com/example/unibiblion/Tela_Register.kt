package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Tela_Register : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Define o layout XML que acabamos de criar
        setContentView(R.layout.activity_tela_register)

        val buttonAvancar: Button = findViewById(R.id.button_avancar)

        // Adiciona a lógica para o botão "Avançar"
        buttonAvancar.setOnClickListener {
            // TODO: Aqui você implementará a lógica de validação dos campos.
            // Se a validação for OK, você navegará para a próxima tela.

            // Exemplo de navegação para a Tela de Login após o cadastro (ajuste conforme o fluxo real do app)
            // val intent = Intent(this, SuaProximaActivity::class.java)
            // startActivity(intent)
        }
    }
}