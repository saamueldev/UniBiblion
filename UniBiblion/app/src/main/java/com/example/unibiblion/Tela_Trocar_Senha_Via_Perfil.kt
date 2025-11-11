package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Tela_Trocar_Senha_Via_Perfil : AppCompatActivity() {

    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_trocar_senha_via_perfil)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Conexão dos campos e botão
        newPasswordEditText = findViewById(R.id.edit_text_new_password)
        confirmPasswordEditText = findViewById(R.id.edit_text_confirm_password)
        val saveButton: Button = findViewById(R.id.button_save_changes)

        saveButton.setOnClickListener {
            if (validateAndSavePassword()) {

                Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_LONG).show()

                // **** MUDANÇA AQUI: NAVEGAÇÃO PARA Tela_Editar_Perfil ****
                // Usa FLAG_ACTIVITY_CLEAR_TOP para fechar as telas de código/troca de senha
                // e voltar para a Tela_Editar_Perfil (ou para onde ela estiver na pilha).
                val intent = Intent(this, Tela_De_Perfil_Dados::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)

                // Finaliza a Activity atual
                finish()
            }
        }
    }

    private fun validateAndSavePassword(): Boolean {
        val newPassword = newPasswordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Preencha a nova senha e a confirmação.", Toast.LENGTH_SHORT).show()
            return false
        }

        if (newPassword.length < 6) {
            Toast.makeText(this, "A senha deve ter no mínimo 6 caracteres.", Toast.LENGTH_LONG).show()
            newPasswordEditText.requestFocus()
            return false
        }

        if (newPassword != confirmPassword) {
            Toast.makeText(this, "As senhas digitadas não coincidem.", Toast.LENGTH_LONG).show()
            confirmPasswordEditText.requestFocus()
            return false
        }

        // *** Lógica de salvamento REAL (API/Banco de Dados) aqui ***

        return true
    }
}