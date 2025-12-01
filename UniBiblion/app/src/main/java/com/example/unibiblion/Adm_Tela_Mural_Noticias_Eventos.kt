package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

// Imports do Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class Adm_Tela_Mural_Noticias_Eventos : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var noticiasAdapter: NoticiasAdapter
    private lateinit var bottomNav: BottomNavigationView // Adicionada declaração lateinit

    // Variáveis do Firebase
    private val db = FirebaseFirestore.getInstance()
    private var firestoreListener: ListenerRegistration? = null

    // Flag que indica que esta Activity é de Administração
    private val isAdminUser = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_adm_mural_noticias_eventos)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- CONFIGURAÇÃO INICIAL ---

        recyclerView = findViewById(R.id.recycler_view_noticias)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializa o Adapter vazio. Os dados chegarão pelo Firebase.
        noticiasAdapter = NoticiasAdapter(mutableListOf(), isAdminUser)
        recyclerView.adapter = noticiasAdapter

        // Inicializa o BottomNav
        bottomNav = findViewById(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_noticias

        // Configuração do Botão Flutuante (FAB) para Criar Anúncio
        val fabCreate: FloatingActionButton = findViewById(R.id.fab_create_announcement)
        fabCreate.setOnClickListener {
            val targetActivity = Adm_Tela_Criacao_Anuncio_Eventos::class.java
            val intent = Intent(this, targetActivity)
            intent.putExtra("EXTRA_MODE_EDIT", false) // Modo Criação
            startActivity(intent)
        }

        // --- CONFIGURAÇÃO DA NAVEGAÇÃO INFERIOR ---
        bottomNav.setOnItemSelectedListener { item ->
            val activityClass = when (item.itemId) {
                R.id.nav_livraria -> Adm_Tela_Central_Livraria::class.java // Home do Admin
                R.id.nav_noticias -> null // Já estamos aqui
                R.id.nav_chatbot -> Tela_Adm_Chat_Bot::class.java
                R.id.nav_perfil -> Adm_Tela_De_Perfil::class.java
                else -> null
            }

            if (activityClass != null) {
                val intent = Intent(this, activityClass).apply {
                    // Evita empilhamento e garante que a Activity de destino seja a raiz
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
                // Se a navegação não for para a Livraria (Home), esta Activity é encerrada
                // Se for para Livraria, a FLAG_ACTIVITY_CLEAR_TOP cuida disso
                if (item.itemId != R.id.nav_livraria) {
                    finish()
                }
                return@setOnItemSelectedListener true
            }
            // Retorna true se for o item atual (nav_noticias), ou false se for inválido
            return@setOnItemSelectedListener item.itemId == R.id.nav_noticias
        }
        // --- FIM DA CONFIGURAÇÃO DA NAVEGAÇÃO INFERIOR ---

        // Iniciar busca de dados
        carregarNoticiasFirestore()
    }

    // Chamamos a busca no onResume para garantir que a lista atualize se voltarmos da tela de criação
    override fun onResume() {
        super.onResume()
        if (firestoreListener == null) {
            carregarNoticiasFirestore()
        }
        // Garante que o item 'nav_noticias' esteja selecionado ao retornar
        if (::bottomNav.isInitialized) {
            bottomNav.selectedItemId = R.id.nav_noticias
        }
    }

    // Importante: Parar de escutar o banco quando sair da tela para economizar dados
    override fun onDestroy() {
        super.onDestroy()
        firestoreListener?.remove()
    }

    private fun carregarNoticiasFirestore() {
        // Remove listener anterior se existir para evitar duplicidade
        firestoreListener?.remove()

        firestoreListener = db.collection("noticias")
            .orderBy("timestamp", Query.Direction.DESCENDING) // Mais recentes primeiro
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Erro ao carregar notícias.", Toast.LENGTH_SHORT).show()
                    firestoreListener = null // Indica que o listener falhou
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val listaNoticias = mutableListOf<Noticia>()

                    for (document in snapshots.documents) {
                        val noticia = document.toObject(Noticia::class.java)
                        // Mapeia o ID do documento para permitir Edição/Exclusão
                        if (noticia != null) {
                            val noticiaComId = noticia.copy(id = document.id)
                            listaNoticias.add(noticiaComId)
                        }
                    }

                    // Atualiza o Adapter com a lista real do Firebase
                    noticiasAdapter.updateList(listaNoticias)
                }
            }
    }
}