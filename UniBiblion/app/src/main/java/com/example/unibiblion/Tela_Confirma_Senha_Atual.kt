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
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class Tela_Confirma_Senha_Atual : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var senhaAtualEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_confirma_senha_atual)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        senhaAtualEditText = findViewById(R.id.edit_text_senha_atual)
        val nextButton: Button = findViewById(R.id.button_verify_and_next)

        nextButton.setOnClickListener {
            val senhaAtual = senhaAtualEditText.text.toString()

            if (senhaAtual.isEmpty()) {
                Toast.makeText(this, "Insira sua senha atual para continuar.", Toast.LENGTH_SHORT).show()
                senhaAtualEditText.requestFocus()
                return@setOnClickListener
            }

            // Chama a função para verificar a senha (re-autenticação)
            verificarSenhaAtual(senhaAtual)
        }
    }

    /**
     * Tenta re-autenticar o usuário com a senha atual.
     */
    private fun verificarSenhaAtual(senhaAtual: String) {
        val usuario = auth.currentUser

        if (usuario == null || usuario.email == null) {
            Toast.makeText(this, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show()
            // Opcional: Redirecionar para a tela de login aqui
            finish()
            return
        }

        val credential = EmailAuthProvider.getCredential(usuario.email!!, senhaAtual)

        usuario.reauthenticate(credential)
            .addOnSuccessListener {
                // ✅ SUCESSO: A senha atual está correta e a sessão foi revalidada.
                Toast.makeText(this, "Senha verificada. Avançando para a troca.", Toast.LENGTH_SHORT).show()

                // NAVEGAÇÃO: Avança sem a necessidade de passar a senha (segurança!)
                val intent = Intent(this, Tela_Trocar_Senha_Via_Perfil::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                // ❌ ERRO: A senha atual fornecida está incorreta.
                Toast.makeText(this, "Senha atual incorreta. Tente novamente.", Toast.LENGTH_LONG).show()
                senhaAtualEditText.setText("")
                senhaAtualEditText.requestFocus()
            }
    }
}