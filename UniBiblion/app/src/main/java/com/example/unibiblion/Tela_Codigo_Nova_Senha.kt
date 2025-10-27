package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText // Necessário para o campo de código
import android.widget.Toast // Necessário para o pop-up
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Tela_Codigo_Nova_Senha : AppCompatActivity() {

    // Código correto simulado
    private val CODIGO_CORRETO = "000000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Certifique-se de que este é o nome do seu arquivo XML
        setContentView(R.layout.activity_tela_codigo_nova_senha)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Conexão do campo de código (EditText)
        // ID CORRIGIDO: Usando o ID real do XML: edit_text_code_verification
        val codigoEditText: EditText = findViewById(R.id.edit_text_code_verification)

        // 2. Conexão do botão de Avançar
        val nextButton: Button = findViewById(R.id.button_verify_and_next)

        nextButton.setOnClickListener {

            // Pega o texto digitado e remove espaços em branco (trim)
            val codigoDigitado = codigoEditText.text.toString().trim()

            // LÓGICA DE VERIFICAÇÃO: Checa se o campo não está vazio e se é igual a '000000'
            if (codigoDigitado.isEmpty()) {
                Toast.makeText(this, "Por favor, insira o código.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (codigoDigitado == CODIGO_CORRETO) {
                // Sucesso: Navega para a tela final de definição da nova senha
                Toast.makeText(this, "Código verificado! Prossiga.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Tela_Colocar_Nova_Senha::class.java)
                startActivity(intent)
                finish()

            } else {
                // Erro: Exibe o pop-up (Toast)
                Toast.makeText(this, "Código inválido.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}