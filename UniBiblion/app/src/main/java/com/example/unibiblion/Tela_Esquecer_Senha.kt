package com.example.unibiblion

// Importações Padrão do Android
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

// Importações do Firebase
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class Tela_Esquecer_Senha : AppCompatActivity() {

    private val TAG = "Tela_Esquecer_Senha"

    // Use MaterialButton para corresponder ao seu XML
    private lateinit var emailEditText: EditText
    private lateinit var sendCodeButton: MaterialButton
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Certifique-se de que o nome do seu layout XML corresponde a esta referência!
        setContentView(R.layout.activity_tela_esquecer_senha)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Inicializa o Firebase Auth
        auth = Firebase.auth

        // 2. Vincula as Views do Layout
        // Usando o tipo MaterialButton, pois é o que está no seu XML
        emailEditText = findViewById(R.id.edit_text_email_recover)
        sendCodeButton = findViewById(R.id.button_send_code)

        // 3. Configura o Listener para enviar o e-mail de redefinição
        sendCodeButton.setOnClickListener {
            sendPasswordReset()
        }
    }

    /**
     * Valida o email e envia o link de redefinição de senha via Firebase Auth.
     * Não há tela de "enviar código" separada; o Firebase envia o link de redefinição.
     */
    private fun sendPasswordReset() {
        val email = emailEditText.text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(this, "Por favor, insira seu e-mail.", Toast.LENGTH_SHORT).show()
            return
        }

        // Desabilita o botão e muda o texto enquanto processa
        sendCodeButton.isEnabled = false
        sendCodeButton.text = "Enviando..."

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                // Reabilita o botão e reverte o texto
                sendCodeButton.isEnabled = true
                sendCodeButton.text = "Enviar Código"

                if (task.isSuccessful) {
                    // Feedback positivo: informa que as instruções foram enviadas.
                    Log.d(TAG, "Link de redefinição enviado para o e-mail: $email")
                    Toast.makeText(
                        this,
                        "Instruções de redefinição enviadas! Verifique sua caixa de entrada.",
                        Toast.LENGTH_LONG
                    ).show()

                    // Opcional: Redirecionar para a tela de Login ou Home após o envio
                    // Se você deseja ir para outra tela, use um Intent aqui:
                    // val intent = Intent(this, SuaTelaDeLogin::class.java)
                    // startActivity(intent)
                    finish()
                } else {
                    // Feedback de falha (conexão ou e-mail não registrado)
                    Log.w(TAG, "Falha ao enviar e-mail de redefinição.", task.exception)
                    Toast.makeText(
                        this,
                        "Falha no envio. Verifique o e-mail digitado e sua conexão.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}