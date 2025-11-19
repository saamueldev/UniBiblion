package com.example.unibiblion

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
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
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class Adm_Tela_Criacao_Anuncio_Eventos : AppCompatActivity() {

    // Constante para o seletor de imagens
    companion object {
        private const val PICK_IMAGE_REQUEST = 100
        private const val COLECAO_NOTICIAS = "noticias"
    }

    // --- DECLARAÇÕES DE CAMPOS E BOTÕES ---
    private lateinit var titleEditText: EditText
    private lateinit var subtitleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var publishButton: Button
    private lateinit var deleteButton: Button
    private lateinit var selectCoverButton: Button
    private lateinit var formatDropdown: AutoCompleteTextView

    // Variáveis Firebase
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // Variáveis de Controle de Estado
    private var selectedFormat: String = ""
    private var isEditMode: Boolean = false
    private var editingItemId: String? = null // ID do Documento Firestore
    private var currentImageUrl: String? = null // URL da imagem atual (se estiver em edição)
    private var selectedImageUri: Uri? = null // URI da nova imagem selecionada

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
        // O botão 'addExtraPhotosButton' foi removido desta versão simplificada com Firebase Storage
        formatDropdown = findViewById(R.id.auto_complete_format_type)

        // 2. Configuração do Dropdown
        setupFormatDropdown()

        // 3. Verifica e configura o modo de edição, pré-preenchendo dados
        checkEditMode()

        // 4. Ação de Imagem (Abrir Seletor de Imagem)
        selectCoverButton.setOnClickListener {
            openImageChooser()
        }

        // 5. Ação do Botão Principal (Publicar/Salvar Alterações)
        publishButton.setOnClickListener {
            if (validateFields()) {
                if (isEditMode) {
                    saveChanges()
                } else {
                    publishContent()
                }
            }
        }

        // 6. Ação do Botão de Exclusão
        deleteButton.setOnClickListener {
            confirmDeletion()
        }
    }

    // --- LÓGICA DE SELEÇÃO DE IMAGEM ---
    private fun openImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Selecione a Imagem de Capa"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data
            Toast.makeText(this, "Imagem selecionada. Pronto para Upload.", Toast.LENGTH_SHORT).show()
            selectCoverButton.text = "IMAGEM SELECIONADA"
        }
    }

    // --- LÓGICA DE EDIÇÃO/PRÉ-PREENCHIMENTO ---
    private fun checkEditMode() {
        isEditMode = intent.getBooleanExtra("EXTRA_MODE_EDIT", false)

        if (isEditMode) {
            editingItemId = intent.getStringExtra("EXTRA_ID_ITEM")
            val titulo = intent.getStringExtra("EXTRA_TITULO_EDIT") ?: ""
            val preview = intent.getStringExtra("EXTRA_PREVIEW_EDIT") ?: ""
            val corpo = intent.getStringExtra("EXTRA_CORPO_EDIT") ?: ""
            val tipoLayout = intent.getIntExtra("EXTRA_LAYOUT_TIPO_EDIT", TIPO_IMAGEM_GRANDE)
            currentImageUrl = intent.getStringExtra("EXTRA_URL_IMAGEM_EDIT") // Novo campo para URL

            titleEditText.setText(titulo)
            subtitleEditText.setText(preview)
            contentEditText.setText(corpo)

            val formatText = if (tipoLayout == TIPO_IMAGEM_GRANDE) "Imagem Grande (Destaque)" else "Imagem Lateral (Padrão)"
            formatDropdown.setText(formatText, false)
            selectedFormat = formatText

            publishButton.text = "SALVAR ALTERAÇÕES"
            deleteButton.visibility = View.VISIBLE
            selectCoverButton.text = "TROCAR IMAGEM (Atual: ${currentImageUrl?.substring(0, 20)}...)"

        } else {
            deleteButton.visibility = View.GONE
            publishButton.text = "PUBLICAR"
        }
    }

    // --- FUNÇÕES DE AÇÃO ---

    private fun publishContent() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Por favor, selecione uma imagem de capa.", Toast.LENGTH_LONG).show()
            return
        }
        uploadImageAndSaveData()
    }

    private fun saveChanges() {
        // 1. Se uma nova imagem foi selecionada, faça o upload e depois atualize os dados.
        if (selectedImageUri != null) {
            uploadImageAndSaveData(isUpdating = true)
        } else {
            // 2. Se a imagem não mudou, apenas atualize o texto no Firestore.
            updateDataInFirestore(currentImageUrl)
        }
    }

    // --- LÓGICA DE UPLOAD PARA O FIREBASE STORAGE ---
    private fun uploadImageAndSaveData(isUpdating: Boolean = false) {
        val fileUri = selectedImageUri ?: return
        val imageName = "images/${UUID.randomUUID()}.jpg" // Caminho no Storage
        val imageRef = storage.reference.child(imageName)

        publishButton.isEnabled = false // Desabilita o botão para evitar cliques múltiplos
        publishButton.text = "UPLOAD..."

        imageRef.putFile(fileUri)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    if (isUpdating) {
                        updateDataInFirestore(downloadUrl)
                    } else {
                        saveDataToFirestore(downloadUrl)
                    }
                    // Opcional: Deletar a imagem antiga do Storage se estiver em modo de edição
                    if (isUpdating && currentImageUrl != null) {
                        // Implemente a lógica para deletar a imagem antiga aqui, se necessário.
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Falha no upload da imagem: ${it.message}", Toast.LENGTH_LONG).show()
                publishButton.isEnabled = true
                publishButton.text = if (isUpdating) "SALVAR ALTERAÇÕES" else "PUBLICAR"
            }
    }

    // --- LÓGICA DE ARMAZENAMENTO NO FIREBASE FIRESTORE ---

    // Criação (Inserir novo documento)
    private fun saveDataToFirestore(urlImagem: String) {
        val novoAnuncio = hashMapOf(
            "titulo" to titleEditText.text.toString().trim(),
            "preview" to subtitleEditText.text.toString().trim(),
            "corpo" to contentEditText.text.toString().trim(),
            "urlImagem" to urlImagem,
            "tipoLayout" to if (selectedFormat.contains("Grande")) TIPO_IMAGEM_GRANDE else TIPO_IMAGEM_LATERAL,
            "timestamp" to Timestamp.now()
        )

        db.collection(COLECAO_NOTICIAS)
            .add(novoAnuncio)
            .addOnSuccessListener {
                Toast.makeText(this, "Anúncio publicado com sucesso!", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao publicar: ${e.message}", Toast.LENGTH_LONG).show()
                publishButton.isEnabled = true
                publishButton.text = "PUBLICAR"
            }
    }

    // Edição (Atualizar documento existente)
    private fun updateDataInFirestore(urlImagem: String?) {
        val id = editingItemId
        if (id == null) {
            Toast.makeText(this, "Erro: ID não encontrado para edição.", Toast.LENGTH_SHORT).show()
            return
        }

        val dadosAtualizados = hashMapOf<String, Any>(
            "titulo" to titleEditText.text.toString().trim(),
            "preview" to subtitleEditText.text.toString().trim(),
            "corpo" to contentEditText.text.toString().trim(),
            "tipoLayout" to if (selectedFormat.contains("Grande")) TIPO_IMAGEM_GRANDE else TIPO_IMAGEM_LATERAL
            // Não atualiza o timestamp na edição, a menos que você deseje.
        )
        // Adiciona urlImagem apenas se foi passado um novo valor (ou se for o mesmo que o atual)
        if (urlImagem != null) {
            dadosAtualizados["urlImagem"] = urlImagem
        }


        db.collection(COLECAO_NOTICIAS).document(id)
            .update(dadosAtualizados)
            .addOnSuccessListener {
                Toast.makeText(this, "Anúncio editado.", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao salvar alterações: ${e.message}", Toast.LENGTH_LONG).show()
                publishButton.isEnabled = true
                publishButton.text = "SALVAR ALTERAÇÕES"
            }
    }

    // --- LÓGICA DE EXCLUSÃO COM CONFIRMAÇÃO ---
    private fun confirmDeletion() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Exclusão")
            .setMessage("Tem certeza de que deseja deletar permanentemente esta publicação?")
            .setPositiveButton("DELETAR") { _, _ -> executeDeletion() }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun executeDeletion() {
        val id = editingItemId
        if (id == null || !isEditMode) {
            Toast.makeText(this, "Erro: ID da notícia não encontrado.", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Deletar o documento do Firestore (RF04.03.10)
        db.collection(COLECAO_NOTICIAS).document(id)
            .delete()
            .addOnSuccessListener {
                // 2. Opcional: Deletar a imagem do Storage (Boa Prática)
                currentImageUrl?.let { url ->
                    val imageRef = storage.getReferenceFromUrl(url)
                    imageRef.delete().addOnSuccessListener {
                        // Imagem deletada com sucesso.
                    }.addOnFailureListener {
                        // Log de erro, mas não bloqueia a exclusão do documento.
                    }
                }

                // 3. Exibir pop-up e redirecionar (RF04.03.10)
                Toast.makeText(this, "Anúncio deletado.", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao deletar: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    // --- FUNÇÕES AUXILIARES ---
    private fun setupFormatDropdown() {
        val formats = listOf("Imagem Grande (Destaque)", "Imagem Lateral (Padrão)")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, formats)
        formatDropdown.setAdapter(adapter)

        formatDropdown.setOnItemClickListener { _, _, position, _ ->
            selectedFormat = formats[position]
        }
        // Configuração inicial
        formatDropdown.setText(formats[0], false)
        selectedFormat = formats[0]
    }

    private fun validateFields(): Boolean {
        // ... (Sua lógica de validação permanece a mesma)
        val title = titleEditText.text.toString().trim()
        val content = contentEditText.text.toString().trim()

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