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
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class Tela_Trocar_Senha_Via_Perfil : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_trocar_senha_via_perfil) // Certifique-se que este layout só tem NOVA e CONFIRMA

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        // NÃO HÁ MAIS LÓGICA PARA RECEBER A SENHA ATUAL AQUI

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

        // Chama a função para atualizar a senha
        atualizarSenhaNoFirebase(novaSenha)
    }

    // Função agora chama updatePassword diretamente, pois o usuário já foi reautenticado na tela anterior.
    private fun atualizarSenhaNoFirebase(novaSenha: String) {
        val usuario = auth.currentUser

        if (usuario == null) {
            Toast.makeText(this, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        usuario.updatePassword(novaSenha)
            .addOnSuccessListener {
                Toast.makeText(this, "✅ Senha alterada com sucesso!", Toast.LENGTH_LONG).show()

                val intent = Intent(this, Tela_De_Perfil_Dados::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                // O principal erro aqui será o FirebaseAuthWeakPasswordException
                if (e is FirebaseAuthWeakPasswordException) {
                    newPasswordEditText.error = "Senha muito fraca. Tente uma combinação mais forte."
                } else {
                    Toast.makeText(this, "Erro ao salvar a nova senha: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}