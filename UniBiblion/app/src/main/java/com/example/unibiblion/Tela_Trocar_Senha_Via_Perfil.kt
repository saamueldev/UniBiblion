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

// 🔑 Importações corretas do Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class Tela_Trocar_Senha_Via_Perfil : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private var senhaAtual: String? = null

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

        // Recebendo senha atual
        senhaAtual = intent.getStringExtra("EXTRA_SENHA_ATUAL")

        if (senhaAtual == null) {
            Toast.makeText(this, "Erro de sessão. Senha atual não disponível.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

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

        if (novaSenha.length < 6) {
            Toast.makeText(this, "A senha deve ter no mínimo 6 caracteres.", Toast.LENGTH_LONG).show()
            newPasswordEditText.requestFocus()
            return
        }

        if (novaSenha != confirmarSenha) {
            Toast.makeText(this, "As senhas digitadas não coincidem.", Toast.LENGTH_LONG).show()
            confirmPasswordEditText.requestFocus()
            return
        }

        if (senhaAtual != null) {
            atualizarSenhaNoFirebase(senhaAtual!!, novaSenha)
        } else {
            Toast.makeText(this, "Erro de sessão. Tente refazer o processo.", Toast.LENGTH_LONG).show()
        }
    }

    private fun atualizarSenhaNoFirebase(senhaAtual: String, novaSenha: String) {
        val usuario = auth.currentUser

        if (usuario == null || usuario.email == null) {
            Toast.makeText(this, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val credential = EmailAuthProvider.getCredential(usuario.email!!, senhaAtual)

        usuario.reauthenticate(credential)
            .addOnSuccessListener {

                usuario.updatePassword(novaSenha)
                    .addOnSuccessListener {
                        Toast.makeText(this, "✅ Senha alterada com sucesso!", Toast.LENGTH_LONG).show()

                        val intent = Intent(this, Tela_De_Perfil_Dados::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        when (e) {
                            is FirebaseAuthWeakPasswordException ->
                                newPasswordEditText.error = "Senha muito fraca. Tente uma combinação mais forte."

                            else ->
                                Toast.makeText(this, "Erro ao salvar: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Erro de segurança. A senha atual está incorreta. Tente refazer a verificação.",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}
