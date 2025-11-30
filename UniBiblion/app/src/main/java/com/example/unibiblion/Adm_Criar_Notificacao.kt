package com.example.unibiblion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class Adm_Criar_Notificacao : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

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
            saveNotificationToFirestore()
        }
    }

    private fun saveNotificationToFirestore() {
        val titulo = editTextTitulo.text.toString().trim()
        val conteudo = editTextConteudo.text.toString().trim()

        if (titulo.isEmpty()) {
            editTextTitulo.error = "O título não pode estar vazio"
            return
        }

        val notification = hashMapOf(
            "title" to titulo,
            "body" to conteudo,
            "userId" to null,
            "isRead" to false,
            "timestamp" to FieldValue.serverTimestamp()
        )

        db.collection("notifications")
            .add(notification)
            .addOnSuccessListener {
                Toast.makeText(this, "Notificação enviada com sucesso!", Toast.LENGTH_LONG).show()
                editTextTitulo.text.clear()
                editTextConteudo.text.clear()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao enviar notificação: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}

