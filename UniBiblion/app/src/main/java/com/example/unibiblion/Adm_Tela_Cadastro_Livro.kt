package com.example.unibiblion

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore

class Adm_Tela_Cadastro_Livro : AppCompatActivity() {

    // Declaração das Views
    private lateinit var editTitulo: TextInputEditText
    private lateinit var editAutor: TextInputEditText
    private lateinit var editAno: TextInputEditText
    private lateinit var editSinopse: TextInputEditText
    private lateinit var editCapaUrl: TextInputEditText // Campo para URL
    private lateinit var spinnerCurso: Spinner
    private lateinit var editEstoque: TextInputEditText
    private lateinit var radioGroupEstado: RadioGroup
    private lateinit var spinnerIdioma: Spinner
    private lateinit var btnCadastrarLivro: Button
    private lateinit var progressBar: ProgressBar

    // Instância do Firebase Firestore
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Com a correção do tema, esta linha agora funciona
        setContentView(R.layout.activity_adm_tela_cadastro_livro)

        // Inicialização das Views
        editTitulo = findViewById(R.id.edit_titulo)
        editAutor = findViewById(R.id.edit_autor)
        editAno = findViewById(R.id.edit_ano)
        editSinopse = findViewById(R.id.edit_sinopse)
        editCapaUrl = findViewById(R.id.edit_capa_url) // Inicializando o novo campo
        spinnerCurso = findViewById(R.id.spinner_curso)
        editEstoque = findViewById(R.id.edit_estoque)
        radioGroupEstado = findViewById(R.id.radio_group_estado)
        spinnerIdioma = findViewById(R.id.spinner_idioma)
        btnCadastrarLivro = findViewById(R.id.btn_cadastrar_livro)
        progressBar = findViewById(R.id.progressBar)

        // Configuração dos Spinners
        ArrayAdapter.createFromResource(
            this, R.array.cursos_array, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCurso.adapter = adapter
        }
        ArrayAdapter.createFromResource(
            this, R.array.idiomas_array, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerIdioma.adapter = adapter
        }

        // Ação de clique para o botão de cadastro
        btnCadastrarLivro.setOnClickListener { validarEIniciarCadastro() }
    }

    private fun validarEIniciarCadastro() {
        // Coleta de dados dos campos
        val titulo = editTitulo.text.toString().trim()
        val autor = editAutor.text.toString().trim()
        val anoStr = editAno.text.toString().trim()
        val sinopse = editSinopse.text.toString().trim()
        val capaUrl = editCapaUrl.text.toString().trim()
        val estoqueStr = editEstoque.text.toString().trim()
        val curso = spinnerCurso.selectedItem.toString()
        val idioma = spinnerIdioma.selectedItem.toString()
        val selectedRadioId = radioGroupEstado.checkedRadioButtonId
        val estado = if (selectedRadioId != -1) findViewById<RadioButton>(selectedRadioId).text.toString() else ""

        // Validação completa dos campos
        if (titulo.isEmpty() || autor.isEmpty() || anoStr.isEmpty() || sinopse.isEmpty() ||
            capaUrl.isEmpty() || estoqueStr.isEmpty() || estado.isEmpty() ||
            spinnerCurso.selectedItemPosition == 0 || spinnerIdioma.selectedItemPosition == 0) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_LONG).show()
            return
        }

        val estoque = estoqueStr.toLongOrNull()
        val ano = anoStr.toLongOrNull()

        if (ano == null || ano <= 1000 || ano > 2025) { // Ano realista
            editAno.error = "Ano inválido"; return
        }
        if (estoque == null || estoque < 0) {
            editEstoque.error = "Estoque inválido"; return
        }

        // Se a validação passou, salva os dados diretamente
        salvarLivroNoFirestore(
            titulo, autor, ano, sinopse, capaUrl, estoque, curso, estado, idioma)
    }

    private fun salvarLivroNoFirestore(titulo: String, autor: String, ano: Long, sinopse: String, capaUrl: String, estoque: Long, curso: String, estado: String, idioma: String) {
        setLoading(true)

        // Cria o objeto Livro com os dados coletados
        val livro = Livro(
            titulo = titulo,
            autor = autor,
            ano = ano,
            capaUrl = capaUrl,
            estado = estado,
            curso = curso,
            idioma = idioma,
            qEstoque = estoque,
            resumo = sinopse
        )

        // Adiciona o objeto à coleção "livros" no Firestore
        db.collection("livros")
            .add(livro)
            .addOnSuccessListener {
                setLoading(false)
                Toast.makeText(this, "Livro cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                finish() // Fecha a tela e volta para o acervo
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(this, "Erro ao cadastrar o livro: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun setLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnCadastrarLivro.isEnabled = !isLoading
    }
}
