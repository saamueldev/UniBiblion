package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
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
import com.google.firebase.auth.FirebaseAuth // 🎯 NOVO IMPORT

class NoticiasActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var noticiasAdapter: NoticiasAdapter
    private lateinit var listaNoticiasCompleta: List<Noticia> // Lista original para busca
    private lateinit var searchBar: EditText // Referência à barra de pesquisa

    // VARIÁVEIS DO FIREBASE
    private lateinit var auth: FirebaseAuth // 🎯 NOVO: Referência para a autenticação
    private lateinit var db: FirebaseFirestore
    private var noticiasListener: ListenerRegistration? = null // Listener para tempo real
    private var isCurrentUserAdmin: Boolean = false // 🎯 NOVO: Status do ADM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_noticias)

        // 1. INICIALIZAÇÃO DO FIREBASE
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // 2. OBTÉM AS REFERÊNCIAS
        bottomNavigation = findViewById(R.id.bottom_navigation)
        searchBar = findViewById(R.id.search_bar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        // 🎯 3. VERIFICA O STATUS DE ADM E CHAMA A CONFIGURAÇÃO PRINCIPAL
        checkAdminStatusAndSetup()

        // 4. CONFIGURAÇÃO DA BARRA DE PESQUISA
        configurarBusca()

        // 5. CONFIGURAÇÃO DA BOTTOM NAVIGATION LISTENER
        NavigationHelper.setupBottomNavigation(this, bottomNavigation, R.id.nav_noticias)
    }

// ------------------------ ## 🔑 Lógica de Verificação de Administrador (ADM)----------------------------------------------


    /**
     * Verifica o status do usuário logado no Firestore e, em seguida,
     * configura o RecyclerView e carrega os dados.
     */
    private fun checkAdminStatusAndSetup() {
        val currentUser = auth.currentUser

        // Se não há usuário logado, não é ADM.
        if (currentUser == null) {
            isCurrentUserAdmin = false
            setupRecyclerView()
            carregarNoticiasDoFirebase()
            return
        }

        // Verifica na coleção "administradores" se o UID do usuário logado existe como documento
        db.collection("administradores")
            .document(currentUser.uid)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result?.exists() == true) {
                    // O documento do ADM existe
                    isCurrentUserAdmin = true
                    Toast.makeText(this, "Modo Administrador Ativo", Toast.LENGTH_SHORT).show()
                } else {
                    isCurrentUserAdmin = false
                }
                // 3. INICIALIZAÇÃO DO RECYCLERVIEW
                setupRecyclerView()
                // CHAMA A FUNÇÃO DE BUSCA REAL-TIME
                carregarNoticiasDoFirebase()
            }
    }

    /**
     * Prepara o RecyclerView e inicializa o Adapter com o status de ADM obtido.
     */
    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_noticias)

        // Inicializa a lista completa como vazia
        listaNoticiasCompleta = mutableListOf()

        recyclerView.layoutManager = LinearLayoutManager(this)

        // 🎯 O Adapter é inicializado com o status dinâmico de ADM
        noticiasAdapter = NoticiasAdapter(listaNoticiasCompleta.toMutableList(), isAdmin = isCurrentUserAdmin)
        recyclerView.adapter = noticiasAdapter
    }
// ----------------------------------------------------------------------

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
                    val novaLista = mutableListOf<Noticia>()
                    for (document in snapshots.documents) {
                        // Tenta mapear o documento para a classe Noticia
                        val noticia = document.toObject(Noticia::class.java)

                        // Mapeia e injeta o ID do documento (necessário para edição/exclusão)
                        if (noticia != null) {
                            val noticiaComId = noticia.copy(id = document.id)
                            novaLista.add(noticiaComId)
                        }
                    }

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
}