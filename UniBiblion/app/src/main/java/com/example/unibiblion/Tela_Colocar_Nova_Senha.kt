package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Tela_Colocar_Nova_Senha : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_colocar_nova_senha)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Conecta o botão de Salvar Alterações
        // Assumindo que o ID do botão de salvar no seu XML seja 'button_save_password'
        val saveButton: Button = findViewById(R.id.button_save_password)
        saveButton.setOnClickListener {

            // Lógica de salvar a nova senha no banco de dados iria aqui.

            // Navegação: Volta para a tela de seleção de Login/Cadastro
            val intent = Intent(this, Tela_Login_Resgister::class.java)

            // FLAG_ACTIVITY_CLEAR_TASK garante que todas as telas de recuperação (código, nova senha)
            // sejam fechadas, e o usuário retorne limpo para a tela inicial.
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}