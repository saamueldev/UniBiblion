package com.example.unibiblion

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.Serializable

class Adm_Tela_Detalhes_Livro : AppCompatActivity() {

    private var livroSelecionado: Livro? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adm_tela_detalhes_livro)

        // Recebe o objeto Livro da tela de acervo
        livroSelecionado = getSerializable(intent, "LIVRO_SELECIONADO", Livro::class.java)

        // Validação de segurança para evitar crash se o livro não for recebido
        if (livroSelecionado == null) {
            Toast.makeText(this, "Erro ao carregar os detalhes do livro.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 1. Encontrando os componentes pelos IDs corretos do XML
        val imgCapa: ImageView = findViewById(R.id.capaLivro)
        val txtTitulo: TextView = findViewById(R.id.nomeLivroLabel)
        val txtAutor: TextView = findViewById(R.id.nomeAutorLabel)
        val txtResumo: TextView = findViewById(R.id.resumoLivro)
        val txtEstado: TextView = findViewById(R.id.estadoLivro)
        val txtEstoque: TextView = findViewById(R.id.estoqueLivro)
        val txtIdioma: TextView = findViewById(R.id.idiomaLivro)
        val btnEditar: Button = findViewById(R.id.buttonEditar)
        val btnRemover: Button = findViewById(R.id.buttonRemover)

        // 2. Preenchendo a UI com os dados do livro selecionado
        livroSelecionado?.let { livro ->
            txtTitulo.text = livro.titulo
            txtAutor.text = "por ${livro.autor}"
            txtResumo.text = livro.resumo
            txtEstado.text = livro.estado
            txtEstoque.text = livro.qEstoque.toString()
            txtIdioma.text = livro.idioma

            if (livro.capaUrl.isNotEmpty()) {
                Glide.with(this)
                    .load(livro.capaUrl)
                    .placeholder(R.drawable.sommervile) // Imagem enquanto carrega
                    .error(R.drawable.sommervile)       // Imagem em caso de erro
                    .into(imgCapa)
            } else {
                imgCapa.setImageResource(R.drawable.sommervile) // Imagem padrão
            }

            // 3. *** AÇÃO DO BOTÃO EDITAR IMPLEMENTADA ***
            btnEditar.setOnClickListener {
                // Cria a intenção de ir para a tela de cadastro
                val intent = Intent(this, Adm_Tela_Cadastro_Livro::class.java).apply {
                    // Envia o objeto 'Livro' completo para ser editado
                    putExtra("LIVRO_PARA_EDITAR", livro)
                }
                // Inicia a tela de edição
                startActivity(intent)
                // Fecha a tela de detalhes para que, ao salvar, o app volte para o acervo
                finish()
            }

            // 4. AÇÃO DO BOTÃO REMOVER (Sem alterações)
            btnRemover.setOnClickListener {
                mostrarDialogoDeConfirmacao(livro)
            }
        }
    }

    // --- (Funções mostrarDialogoDeConfirmacao, removerLivroDoFirestore e getSerializable sem alterações) ---
    private fun mostrarDialogoDeConfirmacao(livro: Livro) {
        AlertDialog.Builder(this)
            .setTitle("Remover Livro")
            .setMessage("Você deseja remover o livro?")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("Sim") { _, _ ->
                removerLivroDoFirestore(livro)
            }
            .setNegativeButton("Não", null)
            .show()
    }

    private fun removerLivroDoFirestore(livro: Livro) {
        if (livro.id.isEmpty()) {
            Toast.makeText(this, "Erro: ID do livro inválido. Não é possível remover.", Toast.LENGTH_SHORT).show()
            return
        }

        Firebase.firestore.collection("livros").document(livro.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Livro removido com sucesso.", Toast.LENGTH_LONG).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao remover o livro: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun <T : Serializable?> getSerializable(intent: Intent, key: String, clazz: Class<T>): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(key, clazz)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(key) as? T
        }
    }
}
