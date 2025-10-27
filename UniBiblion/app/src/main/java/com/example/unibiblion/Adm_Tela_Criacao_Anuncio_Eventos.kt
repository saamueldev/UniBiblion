package com.example.unibiblion

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.unibiblion.Noticia.Companion.TIPO_IMAGEM_GRANDE
import com.example.unibiblion.Noticia.Companion.TIPO_IMAGEM_LATERAL

class Adm_Tela_Criacao_Anuncio_Eventos : AppCompatActivity() {

    // --- DECLARAÇÕES DE CAMPOS E BOTÕES ---
    private lateinit var titleEditText: EditText
    private lateinit var subtitleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var publishButton: Button
    private lateinit var deleteButton: Button // Botão de Deletar
    private lateinit var selectCoverButton: Button
    private lateinit var addExtraPhotosButton: Button
    private lateinit var formatDropdown: AutoCompleteTextView

    // Variáveis de Controle de Estado
    private var selectedFormat: String = ""
    private var isEditMode: Boolean = false
    private var editingItemId: String? = null // Usado como ID único (URL da Imagem)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_adm_tela_criacao_anuncio_eventos)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Conexão dos Componentes
        titleEditText = findViewById(R.id.edit_text_title)
        subtitleEditText = findViewById(R.id.edit_text_subtitle)
        contentEditText = findViewById(R.id.edit_text_content)
        publishButton = findViewById(R.id.button_publish)
        deleteButton = findViewById(R.id.button_delete_announcement)
        selectCoverButton = findViewById(R.id.button_select_cover)
        addExtraPhotosButton = findViewById(R.id.button_add_extra_photos)
        formatDropdown = findViewById(R.id.auto_complete_format_type)

        // 2. Configuração do Dropdown
        setupFormatDropdown()

        // 3. Verifica e configura o modo de edição, pré-preenchendo dados
        checkEditMode()

        // 4. Ações de Imagem (Simulação)
        selectCoverButton.setOnClickListener {
        }
        addExtraPhotosButton.setOnClickListener {

        }

        // 5. Ação do Botão Principal (Publicar/Salvar Alterações)
        publishButton.setOnClickListener {
            if (validateFields()) {
                if (isEditMode) {
                    saveChanges() // RF04.03.07 - Salvar Alterações
                } else {
                    publishContent() // Criação (RF04.03.04)
                }
            }
        }

        // 6. Ação do Botão de Exclusão (RF04.03.09/10)
        deleteButton.setOnClickListener {
            confirmDeletion() // Chama o diálogo de confirmação
        }
    }

    // --- LÓGICA DE EDIÇÃO/PRÉ-PREENCHIMENTO (RF04.03.07) ---
    private fun checkEditMode() {
        // Verifica se a Intent veio com a flag de EDIÇÃO
        isEditMode = intent.getBooleanExtra("EXTRA_MODE_EDIT", false)

        if (isEditMode) {
            // MODO EDIÇÃO
            editingItemId = intent.getStringExtra("EXTRA_ID_ITEM")

            // 1. Ajustar Interface
            publishButton.text = "SALVAR ALTERAÇÕES" // RF04.03.07
            deleteButton.visibility = View.VISIBLE   // RF04.03.09

            // 2. Pré-preencher campos com os dados da Notícia
            val titulo = intent.getStringExtra("EXTRA_TITULO_EDIT") ?: ""
            val preview = intent.getStringExtra("EXTRA_PREVIEW_EDIT") ?: ""
            val corpo = intent.getStringExtra("EXTRA_CORPO_EDIT") ?: ""
            val tipoLayout = intent.getIntExtra("EXTRA_LAYOUT_TIPO_EDIT", TIPO_IMAGEM_GRANDE)

            titleEditText.setText(titulo)
            subtitleEditText.setText(preview)
            contentEditText.setText(corpo)

            // 3. Pré-selecionar o dropdown
            val formatText = if (tipoLayout == TIPO_IMAGEM_GRANDE) "Imagem Grande (Destaque)" else "Imagem Lateral (Padrão)"
            formatDropdown.setText(formatText, false)
            selectedFormat = formatText

        } else {
            // MODO CRIAÇÃO
            deleteButton.visibility = View.GONE
            publishButton.text = "PUBLICAR"
        }
    }

    // --- FUNÇÕES DE AÇÃO ---

    private fun publishContent() {
        val novoAnuncio = Noticia(
            titulo = titleEditText.text.toString().trim(),
            preview = subtitleEditText.text.toString().trim(),
            corpo = contentEditText.text.toString().trim(),
            urlImagem = "url_simulada_${System.currentTimeMillis()}", // Simulação de ID
            tipoLayout = if (selectedFormat.contains("Grande")) TIPO_IMAGEM_GRANDE else TIPO_IMAGEM_LATERAL
        )
        NoticiasRepositorySimulado.addNoticia(novoAnuncio)

        Toast.makeText(this, "Anúncio publicado com sucesso!", Toast.LENGTH_LONG).show()
        finish() // RF04.03.04
    }

    private fun saveChanges() {
        val id = editingItemId ?: return // Se não tiver ID, impede salvar

        // 1. Criar o objeto Noticia atualizado
        val noticiaAtualizada = Noticia(
            titulo = titleEditText.text.toString().trim(),
            preview = subtitleEditText.text.toString().trim(),
            corpo = contentEditText.text.toString().trim(),
            urlImagem = id,
            tipoLayout = if (selectedFormat.contains("Grande")) TIPO_IMAGEM_GRANDE else TIPO_IMAGEM_LATERAL
        )

        // 2. Chamar a função de atualização no repositório (RF04.03.08)
        NoticiasRepositorySimulado.updateNoticia(noticiaAtualizada, id)

        Toast.makeText(this, "Anúncio editado.", Toast.LENGTH_LONG).show() // RF04.03.08 Pop-up
        finish() // RF04.03.08 Redirecionar
    }

    // --- LÓGICA DE EXCLUSÃO COM CONFIRMAÇÃO (RF04.03.10) ---

    // Exibe o diálogo de confirmação antes de deletar
    private fun confirmDeletion() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Confirmar Exclusão")
        builder.setMessage("Tem certeza de que deseja deletar permanentemente esta publicação?")

        // Botão de Confirmação (DELETAR)
        builder.setPositiveButton("DELETAR") { dialog, which ->
            executeDeletion()
        }

        // Botão de Cancelamento
        builder.setNegativeButton("Cancelar") { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }

    // Executa a exclusão APÓS a confirmação
    private fun executeDeletion() {
        val id = editingItemId
        if (id == null || !isEditMode) {
            Toast.makeText(this, "Erro: ID da notícia não encontrado.", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Excluir a publicação (RF04.03.10)
        NoticiasRepositorySimulado.deleteNoticia(id)

        // 2. Exibir pop-up "Anúncio deletado" (RF04.03.10)
        Toast.makeText(this, "Anúncio deletado.", Toast.LENGTH_LONG).show()

        // 3. Redirecionar para o mural (RF04.03.10)
        finish()
    }


    // --- FUNÇÕES AUXILIARES ---
    private fun setupFormatDropdown() {
        val formats = listOf("Imagem Grande (Destaque)", "Imagem Lateral (Padrão)")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, formats)
        formatDropdown.setAdapter(adapter)

        formatDropdown.setOnItemClickListener { parent, view, position, id ->
            selectedFormat = formats[position]
        }
        formatDropdown.setText(formats[0], false)
        selectedFormat = formats[0]
    }

    private fun validateFields(): Boolean {
        val title = titleEditText.text.toString().trim()
        val content = contentEditText.text.toString().trim()
        // Subtitle é opcional

        if (title.isEmpty()) {
            titleEditText.error = "O título é obrigatório."
            titleEditText.requestFocus()
            return false
        }
        if (content.isEmpty() || content.length < 50) {
            contentEditText.error = "O conteúdo precisa ter pelo menos 50 caracteres."
            contentEditText.requestFocus()
            return false
        }
        if (selectedFormat.isEmpty()) {
            Toast.makeText(this, "Por favor, selecione um formato de anúncio.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}