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
import com.google.firebase.auth.FirebaseAuth

class Tela_Trocar_Senha_Via_Perfil : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
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

        auth = FirebaseAuth.getInstance()

        newPasswordEditText = findViewById(R.id.edit_text_new_password)
        confirmPasswordEditText = findViewById(R.id.edit_text_confirm_password)
        val saveButton: Button = findViewById(R.id.button_save_changes)

        saveButton.setOnClickListener {
            iniciarTrocaDeSenha()
        }
    }

    private fun iniciarTrocaDeSenha() {
        val novaSenha = newPasswordEditText.text.toString()
        val confirmarSenha = confirmPasswordEditText.text.toString()

        if (novaSenha.isEmpty() || confirmarSenha.isEmpty()) {
            Toast.makeText(this, "Preencha a nova senha e a confirmação.", Toast.LENGTH_SHORT).show()
            return
        }

        // Regra RF: 6–16 caracteres + inclui letra + número
        val senhaValida =
            novaSenha.length in 6..16 &&
                    novaSenha.any { it.isDigit() } &&
                    novaSenha.any { it.isLetter() }

        if (!senhaValida) {
            Toast.makeText(
                this,
                "A senha deve ter 6 a 16 caracteres e incluir letras e números.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (novaSenha != confirmarSenha) {
            Toast.makeText(this, "As senhas digitadas não coincidem.", Toast.LENGTH_LONG).show()
            return
        }

        atualizarSenhaNoFirebase(novaSenha)
    }

    private fun atualizarSenhaNoFirebase(novaSenha: String) {
        val usuario = auth.currentUser ?: return

        usuario.updatePassword(novaSenha)
            .addOnSuccessListener {
                Toast.makeText(this, "Senha alterada com sucesso.", Toast.LENGTH_LONG).show()

                val intent = Intent(this, Tela_De_Perfil_Dados::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
    }
}
