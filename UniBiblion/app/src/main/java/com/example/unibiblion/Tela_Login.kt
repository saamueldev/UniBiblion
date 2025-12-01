package com.example.unibiblion

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton // Importação correta para MaterialButton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class Tela_Login : AppCompatActivity() {

    private val TAG = "Tela_Login"
    private lateinit var emailEditText: EditText
    private lateinit var senhaEditText: EditText
    private lateinit var loginButton: MaterialButton // Tipagem correta
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicialização de Views e Firebase
        emailEditText = findViewById(R.id.edit_text_email)
        senhaEditText = findViewById(R.id.edit_text_password)
        loginButton = findViewById(R.id.button_login) // Usando MaterialButton
        db = Firebase.firestore
        auth = Firebase.auth

        // Links
        val forgotPasswordLink: TextView = findViewById(R.id.text_view_forgot_password)
        val registerLink: TextView = findViewById(R.id.text_view_register_link)

        // Listeners
        loginButton.setOnClickListener { handleLogin() }

        forgotPasswordLink.setOnClickListener {
            val intent = Intent(this, Tela_Esquecer_Senha::class.java)
            startActivity(intent)
        }

        registerLink.setOnClickListener {
            val intent = Intent(this, Tela_Register::class.java)
            startActivity(intent)
        }

        // Estilização do Link de Cadastro
        applyRegisterLinkStyling(registerLink)
    }

    /**
     * Gerencia o estado do botão de login (habilita/desabilita) durante as requisições.
     */
    private fun setUIState(isLoading: Boolean) {
        loginButton.isEnabled = !isLoading
        loginButton.text = if (isLoading) "Entrando..." else "Login"
        // TODO: Mostrar/Esconder ProgressBar aqui se houver uma.
    }

    private fun handleLogin() {
        val email = emailEditText.text.toString().trim()
        val senha = senhaEditText.text.toString().trim()

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            return
        }

        authenticateUserWithFirebase(email, senha)
    }

    /**
     * Tenta fazer login usando Firebase Authentication (seguro)
     */
    private fun authenticateUserWithFirebase(email: String, senha: String) {
        setUIState(true) // Inicia estado de carregamento

        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.uid?.let { uid ->
                        fetchUserDetails(uid)
                    }

                } else {
                    setUIState(false) // Reverte estado de carregamento em caso de falha Auth
                    Log.w(TAG, "Falha na autenticação do Firebase Auth.", task.exception)
                    Toast.makeText(this, "E-mail ou senha incorretos.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * Busca os dados do usuário (principalmente o campo 'admin') no Firestore.
     */
    private fun fetchUserDetails(uid: String) {
        db.collection("usuarios")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                setUIState(false) // Desativa estado de carregamento após sucesso/falha do Firestore

                if (document.exists()) {
                    val isAdmin = document.getBoolean("admin") == true

                    Log.d(TAG, "Resultado da verificação isAdmin: $isAdmin")

                    val targetActivity = if (isAdmin) Adm_Tela_Central_Livraria::class.java else Tela_Central_Livraria::class.java
                    val intent = Intent(this, targetActivity)
                    startActivity(intent)
                    finish()

                } else {
                    Log.e(TAG, "Documento do usuário não encontrado no Firestore para o UID: $uid")
                    Toast.makeText(this, "Erro: Perfil incompleto. Tente registrar novamente.", Toast.LENGTH_LONG).show()
                    auth.signOut()
                    setUIState(false)
                }
            }
            .addOnFailureListener { e ->
                setUIState(false) // Desativa estado de carregamento em caso de falha do Firestore
                Log.e(TAG, "Erro ao buscar detalhes do usuário no Firestore: ${e.message}", e)
                Toast.makeText(this, "Erro ao buscar dados. Tente novamente.", Toast.LENGTH_LONG).show()
                auth.signOut() // Desloga o usuário se o Firestore falhar (segurança)
            }
    }

    /**
     * Aplica o negrito e a cor branca à parte "Cadastre-se agora!" do link de registro.
     */
    private fun applyRegisterLinkStyling(textView: TextView) {
        val fullText = "Não tem conta? Cadastre-se agora!"
        val highlightText = "Cadastre-se agora!"
        val startIndex = fullText.indexOf(highlightText)
        val endIndex = startIndex + highlightText.length

        if (startIndex != -1) {
            val spannable = SpannableString(fullText)

            // 1. Aplica negrito (StyleSpan)
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // 2. Aplica a cor Branca (ForegroundColorSpan)
            val whiteColor = Color.parseColor("#FFFFFF")
            spannable.setSpan(
                ForegroundColorSpan(whiteColor),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            textView.text = spannable
        }
    }
}