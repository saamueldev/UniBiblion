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

class Tela_Email_Trocar_Senha_Via_Perfil : AppCompatActivity() {

    // Simulação do código esperado. Usaremos "000000" para testes.
    private val EXPECTED_CODE = "000000" // <--- CÓDIGO FIXO PARA TESTES

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Carrega o layout de verificação de código
        setContentView(R.layout.activity_tela_email_trocar_senha_via_perfil)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // IDs dos componentes (do XML que criamos antes)
        val codeEditText: EditText = findViewById(R.id.edit_text_code_verification)
        val nextButton: Button = findViewById(R.id.button_verify_and_next)

        nextButton.setOnClickListener {
            val enteredCode = codeEditText.text.toString()

            if (enteredCode.isEmpty() || enteredCode.length != 6) {
                Toast.makeText(this, "Insira o código de 6 dígitos.", Toast.LENGTH_SHORT).show()
                codeEditText.requestFocus()
                return@setOnClickListener
            }

            // Lógica de validação: SÓ AVANÇA SE O CÓDIGO FOR 000000
            if (enteredCode == EXPECTED_CODE) {

                Toast.makeText(this, "Código verificado (SIMULADO). Prossiga.", Toast.LENGTH_SHORT).show()

                // NAVEGAÇÃO: Vai para a SEGUNDA TELA do fluxo
                val intent = Intent(this, Tela_Trocar_Senha_Via_Perfil::class.java)
                startActivity(intent)

                finish()

            } else {
                Toast.makeText(this, "Código incorreto. Tente novamente.", Toast.LENGTH_LONG).show()
                codeEditText.setText("")
                codeEditText.requestFocus()
            }
        }
    }
}