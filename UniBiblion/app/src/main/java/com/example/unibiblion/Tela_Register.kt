package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth // Importa FirebaseAuth
import com.google.firebase.auth.auth // Importa a extensão ktx
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class Tela_Register : AppCompatActivity() {

    private val TAG = "Tela_Register"

    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var confirmPasswordErrorTextView: TextView
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth // 1. Declaração do Firebase Auth

    private val DRAWABLE_BORDER_NORMAL = R.drawable.rounded_edittext_background_white
    private val DRAWABLE_BORDER_ERROR = R.drawable.rounded_edittext_background_red

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nameEditText: EditText = findViewById(R.id.edit_text_name)
        val emailEditText: EditText = findViewById(R.id.edit_text_email)
        passwordEditText = findViewById(R.id.edit_text_password)
        confirmPasswordEditText = findViewById(R.id.edit_text_confirm_password)
        confirmPasswordErrorTextView = findViewById(R.id.text_view_confirm_password_error)

        db = Firebase.firestore
        auth = Firebase.auth // 2. Inicialização do Firebase Auth

        confirmPasswordEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateConfirmPassword()
            } else {
                clearConfirmPasswordError()
            }
        }

        val registerButton: Button = findViewById(R.id.button_avancar)

        registerButton.setOnClickListener {
            handleRegister(nameEditText, emailEditText, passwordEditText, confirmPasswordEditText)
        }
    }

    private fun validateConfirmPassword(): Boolean {
        val senha = passwordEditText.text.toString()
        val confirmarSenha = confirmPasswordEditText.text.toString()

        if (senha != confirmarSenha) {
            confirmPasswordEditText.setBackgroundResource(DRAWABLE_BORDER_ERROR)
            confirmPasswordErrorTextView.visibility = View.VISIBLE
            return false
        } else {
            clearConfirmPasswordError()
            return true
        }
    }

    private fun clearConfirmPasswordError() {
        confirmPasswordEditText.setBackgroundResource(DRAWABLE_BORDER_NORMAL)
        confirmPasswordErrorTextView.visibility = View.GONE
    }

    private fun isPasswordValid(password: String): Boolean {
        if (password.length < 6 || password.length > 16) return false
        val hasLetters = password.any { it.isLetter() }
        val hasDigits = password.any { it.isDigit() }
        return hasLetters && hasDigits
    }

    private fun handleRegister(
        nameField: EditText,
        emailField: EditText,
        passField: EditText,
        confirmPassField: EditText
    ) {
        val nome = nameField.text.toString().trim()
        val email = emailField.text.toString().trim()
        val senha = passField.text.toString()
        val confirmarSenha = confirmPassField.text.toString()

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isPasswordValid(senha)) {
            val message = "A senha deve ter entre 6 e 16 dígitos e conter letras e números."
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            return
        }

        if (!validateConfirmPassword()) {
            Toast.makeText(this, "As senhas não coincidem.", Toast.LENGTH_SHORT).show()
            return
        }

        // 3. Chamamos a nova função de autenticação
        registerUserWithAuth(nome, email, senha)
    }

    /**
     * 4. Nova função: Cria o usuário no Firebase Auth.
     */
    private fun registerUserWithAuth(nome: String, email: String, senha: String) {
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Autenticação bem-sucedida. O Firebase Auth salvou a senha de forma segura.
                    Log.d(TAG, "Usuário do Auth criado com sucesso.")
                    val firebaseUser = auth.currentUser
                    val uid = firebaseUser?.uid

                    if (uid != null) {
                        // Agora salvamos os dados adicionais no Firestore usando o UID.
                        saveUserToFirestore(uid, nome, email)
                    } else {
                        Log.e(TAG, "UID do usuário Auth é nulo após o registro.")
                        Toast.makeText(this, "Erro interno de registro. Tente novamente.", Toast.LENGTH_LONG).show()
                    }

                } else {
                    // Falha na autenticação (ex: email já em uso, senha inválida).
                    val exception = task.exception
                    val errorMessage = exception?.localizedMessage ?: "Erro desconhecido de autenticação."

                    Log.w(TAG, "Falha no Auth: $errorMessage", exception)

                    // Tratamento de erro mais amigável
                    if (errorMessage.contains("email address is already in use")) {
                        Toast.makeText(this, "Este email já está cadastrado.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Erro no cadastro. Verifique a senha e o email.", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }


    /**
     * 5. Função atualizada: Salva os dados do usuário (exceto a senha) no Firestore.
     */
    private fun saveUserToFirestore(uid: String, nome: String, email: String) {
        val user = hashMapOf(
            "nome" to nome,
            "email" to email
            // A senha NÃO é salva aqui, pois o Firebase Auth já a está gerenciando com segurança.
        )

        db.collection("usuarios")
            .document(uid) // Usa o UID do Auth como ID do documento para vinculação 1:1
            .set(user)
            .addOnSuccessListener {
                Log.d(TAG, "Dados do usuário salvos no Firestore com UID: $uid")
                Toast.makeText(this, "Cadastro realizado com sucesso! Faça login.", Toast.LENGTH_LONG).show()

                // Redireciona para a tela de login
                val intent = Intent(this, Tela_Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Erro ao salvar dados adicionais no Firestore", e)

                // Prática de segurança: se a autenticação funcionou, mas o Firestore falhou,
                // devemos excluir o usuário do Auth para evitar contas "órfãs".
                auth.currentUser?.delete()

                Toast.makeText(this, "Erro ao finalizar cadastro. Tente novamente.", Toast.LENGTH_LONG).show()
            }
    }

    // A função checkEmailUniqueness e a versão antiga de saveUserToFirestore foram removidas/substituídas.
}