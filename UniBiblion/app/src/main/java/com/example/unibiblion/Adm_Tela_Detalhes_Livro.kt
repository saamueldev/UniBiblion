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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.Serializable

class Adm_Tela_Detalhes_Livro : AppCompatActivity() {

    private var livroSelecionado: Livro? = null
    private var bottomNavigationView: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adm_tela_detalhes_livro)

        livroSelecionado = getSerializable(intent, "LIVRO_SELECIONADO", Livro::class.java)

        if (livroSelecionado == null) {
            Toast.makeText(this, "Erro ao carregar os detalhes do livro.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 1. Encontrando os componentes
        bottomNavigationView = findViewById(R.id.bottom_navigation)

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
                    .placeholder(R.drawable.sommervile)
                    .error(R.drawable.sommervile)
                    .into(imgCapa)
            } else {
                imgCapa.setImageResource(R.drawable.sommervile)
            }

            // 3. AÇÃO DO BOTÃO EDITAR
            btnEditar.setOnClickListener {
                val intent = Intent(this, Adm_Tela_Cadastro_Livro::class.java).apply {
                    putExtra("LIVRO_PARA_EDITAR", livro)
                }
                startActivity(intent)
                finish()
            }

            // 4. AÇÃO DO BOTÃO REMOVER
            btnRemover.setOnClickListener {
                mostrarDialogoDeConfirmacao(livro)
            }
        }

        // 5. CHAMADA DA FUNÇÃO DE NAVEGAÇÃO
        setupBottomNavigation()
    }

    // -----------------------------------------------------
    // FUNÇÃO DA BARRA DE NAVEGAÇÃO INFERIOR (CORRIGIDA)
    // -----------------------------------------------------
    private fun setupBottomNavigation() {
        bottomNavigationView?.let { bottomNav ->

            // Marca o item de Livraria como selecionado (onde esta tela pertence)
            bottomNav.selectedItemId = R.id.nav_livraria

            bottomNav.setOnItemSelectedListener { item ->

                // Ignora o clique se o item já estiver selecionado para evitar recarregar
                if (item.itemId == bottomNav.selectedItemId) {
                    return@setOnItemSelectedListener true
                }

                val activityClass = when (item.itemId) {
                    R.id.nav_livraria -> Adm_Tela_Central_Livraria::class.java
                    R.id.nav_noticias -> Adm_Tela_Mural_Noticias_Eventos::class.java
                    R.id.nav_chatbot -> Tela_Adm_Chat_Bot::class.java
                    R.id.nav_perfil -> Adm_Tela_De_Perfil::class.java
                    else -> null
                }

                if (activityClass != null) {
                    val intent = Intent(this, activityClass).apply {
                        // Flags para limpar a pilha de atividades e iniciar a nova no topo
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                    finish() // Fecha esta Activity (Adm_Tela_Detalhes_Livro)
                    return@setOnItemSelectedListener true
                }

                return@setOnItemSelectedListener false
            }
        }
    }


    // -----------------------------------------------------
    // FUNÇÕES AUXILIARES
    // -----------------------------------------------------
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
                // Redireciona o usuário para a lista principal da livraria após remover
                val intent = Intent(this, Adm_Tela_Central_Livraria::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
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