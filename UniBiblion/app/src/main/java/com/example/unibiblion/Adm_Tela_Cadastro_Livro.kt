package com.example.unibiblion

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class Adm_Tela_Cadastro_Livro : AppCompatActivity() {

    // Constante para a requisição de seleção de imagem
    private val PICK_IMAGE_REQUEST = 1

    // Declaração das Views
    private lateinit var imgCapaLivro: ImageView
    private lateinit var editTitulo: TextInputEditText
    private lateinit var editSinopse: TextInputEditText
    private lateinit var spinnerCurso: Spinner
    private lateinit var editEstoque: TextInputEditText
    private lateinit var radioGroupEstado: RadioGroup
    private lateinit var spinnerIdioma: Spinner
    private lateinit var btnCadastrarLivro: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adm_tela_cadastro_livro)

        // Inicialização das Views
        imgCapaLivro = findViewById(R.id.img_capa_livro)
        editTitulo = findViewById(R.id.edit_titulo)
        editSinopse = findViewById(R.id.edit_sinopse)
        spinnerCurso = findViewById(R.id.spinner_curso)
        editEstoque = findViewById(R.id.edit_estoque)
        radioGroupEstado = findViewById(R.id.radio_group_estado)
        spinnerIdioma = findViewById(R.id.spinner_idioma)
        btnCadastrarLivro = findViewById(R.id.btn_cadastrar_livro)

        // Configuração do Spinner de Curso
        ArrayAdapter.createFromResource(
            this,
            R.array.cursos_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCurso.adapter = adapter
        }

        // Configuração do Spinner de Idioma
        ArrayAdapter.createFromResource(
            this,
            R.array.idiomas_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerIdioma.adapter = adapter
        }

        // Ação de clique para selecionar a imagem da capa
        imgCapaLivro.setOnClickListener {
            abrirSeletorDeImagem()
        }

        // Ação de clique para o botão de cadastro
        btnCadastrarLivro.setOnClickListener {
            cadastrarLivro()
        }
    }

    // Função para abrir a galeria de imagens
    private fun abrirSeletorDeImagem() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Lidar com o resultado da seleção de imagem
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri = data.data!!
            // Define a imagem selecionada no ImageView
            imgCapaLivro.setImageURI(imageUri)
        }
    }

    // Função para coletar e validar os dados do formulário
    private fun cadastrarLivro() {
        // 1. Coletar dados dos EditText
        val titulo = editTitulo.text.toString().trim()
        val sinopse = editSinopse.text.toString().trim()
        val estoqueStr = editEstoque.text.toString().trim()

        // 2. Coletar dados dos Spinners
        val curso = spinnerCurso.selectedItem.toString()
        val idioma = spinnerIdioma.selectedItem.toString()

        // 3. Coletar dados do RadioGroup (Estado)
        val selectedId = radioGroupEstado.checkedRadioButtonId
        val estadoRadioButton = findViewById<RadioButton>(selectedId)
        val estado = estadoRadioButton?.text.toString() ?: ""

        // 4. Coletar a imagem da capa (simplesmente pega a drawable atual)
        // Em um projeto real, você precisaria lidar com o upload e o caminho da imagem.
        val capa = imgCapaLivro.drawable
        val capaSelecionada = capa != null && capa !is BitmapDrawable // Verifica se é diferente da drawable padrão (ícone)

        // 5. Validação básica
        if (titulo.isEmpty() || sinopse.isEmpty() || estoqueStr.isEmpty() || curso == "Selecione o Curso" || idioma == "Selecione o Idioma" || !capaSelecionada) {
            Toast.makeText(this, "Por favor, preencha todos os campos e selecione uma capa.", Toast.LENGTH_LONG).show()
            return
        }

        val estoque = estoqueStr.toIntOrNull()
        if (estoque == null || estoque < 0) {
            editEstoque.error = "Estoque inválido"
            return
        }

        // 6. Preparação dos dados para o cadastro (Exemplo)
        val dadosLivro = "Título: $titulo\n" +
                "Sinopse: ${sinopse.substring(0, minOf(sinopse.length, 50))}...\n" +
                "Curso: $curso\n" +
                "Estoque: $estoque\n" +
                "Estado: $estado\n" +
                "Idioma: $idioma\n" +
                "Capa Selecionada: Sim"

        Toast.makeText(this, "Livro Cadastrado com Sucesso!\n$dadosLivro", Toast.LENGTH_LONG).show()

    }
}