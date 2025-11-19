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
// Importações Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class Tela_Confirma_Senha_Atual : AppCompatActivity() {

    // 1. Variáveis de classe para acesso global
    private lateinit var auth: FirebaseAuth
    private lateinit var senhaAtualEditText: EditText // Resolvendo 'Unresolved reference'

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ❌ CORREÇÃO 1: O 'R.layout' geralmente é minúsculo e corresponde ao nome do arquivo XML
        // O erro "Unresolved reference 'activity_tela_confirma_senha_atual'" é porque
        // o compilador não reconhece o ID do layout. O nome correto no Kotlin é R.layout.nome_do_arquivo
        // Assumindo que o nome do seu arquivo XML é activity_tela_confirma_senha_atual.xml
        setContentView(R.layout.activity_tela_confirma_senha_atual)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        // 2. Inicialização das variáveis de classe
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
     * Tenta re-autenticar o usuário com a senha atual para confirmar a identidade.
     * Esta função agora tem acesso direto a 'senhaAtualEditText' por ser uma variável de classe.
     */
    private fun verificarSenhaAtual(senhaAtual: String) {
        val usuario = auth.currentUser

        if (usuario == null || usuario.email == null) {
            Toast.makeText(this, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val credential = EmailAuthProvider.getCredential(usuario.email!!, senhaAtual)

        usuario.reauthenticate(credential)
            .addOnSuccessListener {
                // SUCESSO: A senha atual está correta.
                Toast.makeText(this, "Senha verificada. Avançando...", Toast.LENGTH_SHORT).show()

                // NAVEGAÇÃO: Passa a senha atual para a próxima tela
                val intent = Intent(this, Tela_Trocar_Senha_Via_Perfil::class.java)
                intent.putExtra("EXTRA_SENHA_ATUAL", senhaAtual)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                // ERRO: A senha atual fornecida está incorreta.
                Toast.makeText(this, "Senha atual incorreta. Tente novamente.", Toast.LENGTH_LONG).show()

                // ✅ CORREÇÃO 2: Acessando a variável de classe para limpar o campo e focar.
                senhaAtualEditText.setText("")
                senhaAtualEditText.requestFocus()
            }
    }
}