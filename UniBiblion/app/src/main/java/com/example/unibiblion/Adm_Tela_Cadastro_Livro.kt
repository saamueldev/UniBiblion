package com.example.unibiblion

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable

class Adm_Tela_Cadastro_Livro : AppCompatActivity() {

    // Declaração das Views
    private lateinit var editTitulo: TextInputEditText
    private lateinit var editAutor: TextInputEditText
    private lateinit var editAno: TextInputEditText
    private lateinit var editSinopse: TextInputEditText
    private lateinit var editCapaUrl: TextInputEditText
    private lateinit var editEstoque: TextInputEditText
    private lateinit var spinnerCurso: Spinner
    private lateinit var radioGroupEstado: RadioGroup
    private lateinit var spinnerIdioma: Spinner
    private lateinit var btnAcao: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var titleTextView: TextView

    // Variáveis para controlar o modo de edição
    private var livroParaEditar: Livro? = null
    private var isEditMode = false

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adm_tela_cadastro_livro)

        // 1. Inicializar os componentes da UI
        iniciarComponentes()
        configurarSpinners()

        // 2. Verificar se a tela foi aberta para edição
        if (intent.hasExtra("LIVRO_PARA_EDITAR")) {
            isEditMode = true
            livroParaEditar = getSerializable(intent, "LIVRO_PARA_EDITAR", Livro::class.java)
            configurarModoEdicao()
        } else {
            isEditMode = false
            configurarModoCadastro()
        }

        // 3. Configurar a ação do botão
        btnAcao.setOnClickListener {
            if (isEditMode) {
                validarESalvarAlteracoes()
            } else {
                validarEIniciarCadastro()
            }
        }
    }

    private fun iniciarComponentes() {
        editTitulo = findViewById(R.id.edit_titulo)
        editAutor = findViewById(R.id.edit_autor)
        editAno = findViewById(R.id.edit_ano)
        editSinopse = findViewById(R.id.edit_sinopse)
        editCapaUrl = findViewById(R.id.edit_capa_url)
        editEstoque = findViewById(R.id.edit_estoque)
        spinnerCurso = findViewById(R.id.spinner_curso)
        radioGroupEstado = findViewById(R.id.radio_group_estado)
        spinnerIdioma = findViewById(R.id.spinner_idioma)
        btnAcao = findViewById(R.id.btn_cadastrar_livro) // ID do botão de ação
        progressBar = findViewById(R.id.progressBar)
        titleTextView = findViewById(R.id.title_cadastro) // ID do TextView do título (agora vai funcionar)
    }

    private fun configurarSpinners() {
        // Usando os arrays de strings do seu projeto
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
    }

    private fun configurarModoCadastro() {
        titleTextView.text = "Cadastro de Novo Livro"
        btnAcao.text = "Cadastrar Livro"
    }

    private fun configurarModoEdicao() {
        titleTextView.text = "Edição de Livro"
        btnAcao.text = "Salvar Alterações"
        preencherFormularioParaEdicao()
    }

    private fun preencherFormularioParaEdicao() {
        livroParaEditar?.let { livro ->
            editTitulo.setText(livro.titulo)
            editAutor.setText(livro.autor)
            editAno.setText(if (livro.ano > 0) livro.ano.toString() else "")
            editSinopse.setText(livro.resumo)
            editCapaUrl.setText(livro.capaUrl)
            editEstoque.setText(livro.qEstoque.toString())

            // Selecionar o item correto nos Spinners
            selecionarItemNoSpinner(spinnerCurso, livro.curso)
            selecionarItemNoSpinner(spinnerIdioma, livro.idioma)

            // Selecionar o RadioButton correto
            if (livro.estado.equals("Usado", ignoreCase = true)) {
                radioGroupEstado.check(R.id.radio_estado_usado)
            } else {
                radioGroupEstado.check(R.id.radio_estado_novo)
            }
        }
    }

    private fun validarEIniciarCadastro() {
        val novoLivro = coletarDadosDoFormulario() ?: return
        salvarLivroNoFirestore(novoLivro, false)
    }

    private fun validarESalvarAlteracoes() {
        val livroAtualizado = coletarDadosDoFormulario() ?: return
        salvarLivroNoFirestore(livroAtualizado, true)
    }

    private fun coletarDadosDoFormulario(): Livro? {
        val titulo = editTitulo.text.toString().trim()
        val autor = editAutor.text.toString().trim()
        val anoStr = editAno.text.toString().trim()
        val sinopse = editSinopse.text.toString().trim()
        val capaUrl = editCapaUrl.text.toString().trim()
        val estoqueStr = editEstoque.text.toString().trim()

        if (titulo.isEmpty()) {
            Toast.makeText(this, "O título é obrigatório.", Toast.LENGTH_SHORT).show()
            return null
        }
        // Adicione outras validações se necessário

        val ano = anoStr.toLongOrNull() ?: 0
        val estoque = estoqueStr.toLongOrNull() ?: 0
        val curso = spinnerCurso.selectedItem.toString()
        val idioma = spinnerIdioma.selectedItem.toString()
        val selectedRadioId = radioGroupEstado.checkedRadioButtonId
        val estado = if (selectedRadioId != -1) findViewById<RadioButton>(selectedRadioId).text.toString() else "Novo"

        return Livro(
            id = if (isEditMode) livroParaEditar?.id ?: "" else "", // Mantém o ID original na edição
            titulo = titulo,
            autor = autor,
            ano = ano,
            resumo = sinopse,
            capaUrl = capaUrl,
            qEstoque = estoque,
            curso = curso,
            estado = estado,
            idioma = idioma
        )
    }

    private fun salvarLivroNoFirestore(livro: Livro, isUpdate: Boolean) {
        setLoading(true)

        val task = if (isUpdate) {
            // Garante que o ID não esteja vazio ao atualizar
            if (livro.id.isEmpty()) {
                setLoading(false)
                Toast.makeText(this, "Erro: ID do livro não encontrado para atualização.", Toast.LENGTH_LONG).show()
                return
            }
            db.collection("livros").document(livro.id).set(livro)
        } else {
            db.collection("livros").add(livro)
        }

        task.addOnSuccessListener {
            setLoading(false)
            val mensagem = if (isUpdate) "Alterações salvas com sucesso!" else "Livro cadastrado com sucesso!"
            Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
            finish() // Fecha a tela e volta para a anterior (acervo)
        }.addOnFailureListener { e ->
            setLoading(false)
            val acao = if (isUpdate) "salvar as alterações" else "cadastrar o livro"
            Toast.makeText(this, "Erro ao $acao: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnAcao.isEnabled = !isLoading
    }

    // Função auxiliar para selecionar item em Spinner
    private fun selecionarItemNoSpinner(spinner: Spinner, valor: String) {
        val adapter = spinner.adapter as? ArrayAdapter<String>
        if (adapter != null) {
            val position = adapter.getPosition(valor)
            if (position >= 0) {
                spinner.setSelection(position)
            }
        }
    }

    // Função auxiliar para obter Serializable da Intent
    private fun <T : Serializable?> getSerializable(intent: Intent, key: String, clazz: Class<T>): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(key, clazz)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(key) as? T
        }
    }
}
