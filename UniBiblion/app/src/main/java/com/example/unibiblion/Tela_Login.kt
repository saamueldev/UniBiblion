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
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class Tela_Login : AppCompatActivity() {

    private val TAG = "Tela_Login"
    private lateinit var emailEditText: EditText
    private lateinit var senhaEditText: EditText
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

        emailEditText = findViewById(R.id.edit_text_email)
        senhaEditText = findViewById(R.id.edit_text_password)
        db = Firebase.firestore
        auth = Firebase.auth

        val loginButton: Button = findViewById(R.id.button_login)
        loginButton.setOnClickListener {
            handleLogin()
        }

        val forgotPasswordLink: TextView = findViewById(R.id.text_view_forgot_password)
        forgotPasswordLink.setOnClickListener {
            val intent = Intent(this, Tela_Esquecer_Senha::class.java)
            startActivity(intent)
        }

        val registerLink: TextView = findViewById(R.id.text_view_register_link)

        // =======================================================
        // LÓGICA DE DESTAQUE APLICADA VIA SPANNABLESTRING
        // =======================================================
        val fullText = "Não tem conta? Cadastre-se agora!"
        val highlightText = "Cadastre-se agora!"
        val startIndex = fullText.indexOf(highlightText)
        val endIndex = startIndex + highlightText.length

        if (startIndex != -1) {
            val spannable = SpannableString(fullText)

            // 1. Aplica negrito
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // 2. Aplica a cor Branca (#FFFFFF). A cor principal é definida no XML,
            // mas manter o span de cor para a parte destacada garante consistência.
            val whiteColor = Color.parseColor("#FFFFFF")
            spannable.setSpan(
                ForegroundColorSpan(whiteColor),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            registerLink.text = spannable
        }

        // Ação de clique para ir para a Tela_Register
        registerLink.setOnClickListener {
            val intent = Intent(this, Tela_Register::class.java)
            startActivity(intent)
        }
        // =======================================================
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
        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 1. LOGIN SUCESSO
                    val user = auth.currentUser
                    user?.uid?.let { uid ->
                        // 2. Busca dados adicionais no Firestore
                        fetchUserDetails(uid)
                    }

                } else {
                    // 3. LOGIN FALHA: Dados Incorretos
                    Log.w(TAG, "Falha na autenticação do Firebase Auth.", task.exception)
                    Toast.makeText(this, "E-mail ou senha incorretos.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * Busca os dados do usuário (principalmente o campo 'admin') no Firestore
     * após a autenticação bem-sucedida.
     */
    private fun fetchUserDetails(uid: String) {
        db.collection("usuarios")
            .document(uid) // Busca pelo UID do Auth, que é o ID do documento
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val isAdmin = document.getBoolean("admin") == true

                    Toast.makeText(this, "Login efetuado com sucesso!", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Resultado da verificação isAdmin: $isAdmin")

                    if (isAdmin) {
                        Log.i(TAG, "Login de Administrador.")
                        val intent = Intent(this, Adm_Tela_Central_Livraria::class.java)
                        startActivity(intent)
                    } else {
                        Log.i(TAG, "Login de Usuário Comum.")
                        val intent = Intent(this, Tela_Central_Livraria::class.java)
                        startActivity(intent)
                    }
                    finish()
                } else {
                    Log.e(TAG, "Documento do usuário não encontrado no Firestore para o UID: $uid")
                    Toast.makeText(this, "Erro: Perfil incompleto.", Toast.LENGTH_LONG).show()
                    // Desloga o usuário se o documento do perfil não for encontrado
                    auth.signOut()
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Erro ao buscar detalhes do usuário no Firestore: ${e.message}", e)
                Toast.makeText(this, "Erro ao buscar dados. Tente novamente.", Toast.LENGTH_LONG).show()
                // Desloga o usuário em caso de falha de rede/firestore
                auth.signOut()
            }
    }
}