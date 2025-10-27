package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText // Importar EditText
import android.widget.Toast // Importar Toast para o pop-up
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Tela_Colocar_Nova_Senha : AppCompatActivity() {

    // IDs dos campos de texto (EditText) extraídos do seu XML
    // @id/edit_text_new_password
    // @id/edit_text_confirm_new_password

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_colocar_nova_senha)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Conexão dos campos de senha
        val newPasswordEditText: EditText = findViewById(R.id.edit_text_new_password)
        val confirmPasswordEditText: EditText = findViewById(R.id.edit_text_confirm_new_password)

        // 2. Conecta o botão de Salvar Alterações
        // O ID do XML é: button_save_password
        val saveButton: Button = findViewById(R.id.button_save_password)

        saveButton.setOnClickListener {
            handlePasswordChange(newPasswordEditText, confirmPasswordEditText)
        }
    }

    /**
     * Função que contém a lógica de verificação de senhas e navegação.
     */
    private fun handlePasswordChange(newPassField: EditText, confirmPassField: EditText) {
        val novaSenha = newPassField.text.toString()
        val confirmarSenha = confirmPassField.text.toString()

        // 1. Verifica se os campos estão vazios
        if (novaSenha.isEmpty() || confirmarSenha.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha ambos os campos.", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Verifica se as senhas são iguais
        if (novaSenha == confirmarSenha) {

            // Sucesso: As senhas são iguais e podem ser salvas (simulação)
            Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_LONG).show()

            // Lógica de salvar a nova senha no banco de dados iria aqui.

            // Navegação: Leva para a tela de login e limpa o histórico de recuperação
            val intent = Intent(this, Tela_Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        } else {
            // Erro: As senhas são diferentes
            Toast.makeText(this, "As senhas não coincidem. Tente novamente.", Toast.LENGTH_LONG).show()
        }
    }
}