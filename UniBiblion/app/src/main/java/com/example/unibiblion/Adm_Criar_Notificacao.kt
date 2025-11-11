package com.example.unibiblion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class Adm_Criar_Notificacao : AppCompatActivity() {

    private lateinit var editTextTitulo: EditText
    private lateinit var editTextConteudo: EditText
    private lateinit var buttonEnviarNotificacao: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adm_criar_notificacao)

        editTextTitulo = findViewById(R.id.editTextTitulo)
        editTextConteudo = findViewById(R.id.editTextConteudo)
        buttonEnviarNotificacao = findViewById(R.id.buttonEnviarNotificacao)

        buttonEnviarNotificacao.setOnClickListener {
            enviarNotificacao()
        }
    }

    private fun enviarNotificacao() {
        val titulo = editTextTitulo.text.toString().trim()
        val conteudo = editTextConteudo.text.toString().trim()

        if (titulo.isEmpty()) {
            editTextTitulo.error = "O título não pode estar vazio"
            return
        }

        if (conteudo.isEmpty()) {
            editTextConteudo.error = "O conteúdo não pode estar vazio"
            return
        }

        val mensagem = "Notificação enviada:\nTítulo: \"$titulo\"\nConteúdo: \"$conteudo\""
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show()

        editTextTitulo.text.clear()
        editTextConteudo.text.clear()
    }
}