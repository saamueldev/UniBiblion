package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast // Import necessário
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

// IMPORTS DO FIREBASE
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ListenerRegistration

class NoticiasActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var noticiasAdapter: NoticiasAdapter
    private lateinit var listaNoticiasCompleta: List<Noticia> // Lista original para busca
    private lateinit var searchBar: EditText // Referência à barra de pesquisa

    // VARIÁVEIS DO FIREBASE
    private lateinit var db: FirebaseFirestore
    private var noticiasListener: ListenerRegistration? = null // Listener para tempo real

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_noticias)

        // 1. INICIALIZAÇÃO DO FIREBASE
        db = FirebaseFirestore.getInstance()

        // 2. OBTÉM AS REFERÊNCIAS
        bottomNavigation = findViewById(R.id.bottom_navigation)
        searchBar = findViewById(R.id.search_bar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- PREPARAÇÃO DO RECYCLERVIEW ---
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_noticias)

        // Inicializa a lista completa como vazia (será preenchida pelo Firebase)
        listaNoticiasCompleta = mutableListOf()

        recyclerView.layoutManager = LinearLayoutManager(this)

        // O Adapter é inicializado com a lista vazia
        noticiasAdapter = NoticiasAdapter(listaNoticiasCompleta.toMutableList(), isAdmin = false)
        recyclerView.adapter = noticiasAdapter

        // 3. CHAMA A FUNÇÃO DE BUSCA REAL-TIME
        carregarNoticiasDoFirebase()

        // 4. CONFIGURAÇÃO DA BARRA DE PESQUISA
        configurarBusca()

        // 5. CONFIGURAÇÃO DA BOTTOM NAVIGATION LISTENER
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_livraria -> {
                    val intent = Intent(this, Tela_Central_Livraria::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_noticias -> {
                    true
                }
                R.id.nav_chatbot -> {
                    val intent = Intent(this, Tela_Chat_Bot::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_perfil -> {
                    val intent = Intent(this, Tela_De_Perfil::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    // 6. GARANTE REMOÇÃO DO LISTENER
    override fun onDestroy() {
        super.onDestroy()
        noticiasListener?.remove()
    }

    // Garante que o ícone de Notícias esteja selecionado ao retornar
    override fun onResume() {
        super.onResume()
        if (::bottomNavigation.isInitialized) {
            bottomNavigation.menu.findItem(R.id.nav_noticias).isChecked = true
        }
    }

    /**
     * Busca as notícias do Firestore em tempo real, ordenadas por data.
     */
    private fun carregarNoticiasDoFirebase() {

        noticiasListener?.remove()

        noticiasListener = db.collection("noticias")
            // 🎯 Ordena pela data de criação (mais recente primeiro)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->

                if (e != null) {
                    Toast.makeText(this, "Erro ao carregar notícias: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    // Mapeia os documentos para o modelo Noticia
                    val novaLista = snapshots.toObjects(Noticia::class.java)

                    // Atualiza a lista completa.
                    listaNoticiasCompleta = novaLista

                    // Re-aplica a filtragem (se houver texto na barra de busca)
                    noticiasAdapter.filtrar(searchBar.text.toString(), listaNoticiasCompleta)
                }
            }
    }

    /**
     * Configura o TextWatcher para filtrar o RecyclerView em tempo real.
     */
    private fun configurarBusca() {
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Dispara a filtragem a cada tecla digitada
                noticiasAdapter.filtrar(s.toString(), listaNoticiasCompleta)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // REMOVA A FUNÇÃO criarDadosDeExemplo() que estava aqui antes.
}