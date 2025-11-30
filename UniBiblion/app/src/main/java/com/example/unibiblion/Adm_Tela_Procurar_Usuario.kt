package com.example.unibiblion

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class Adm_Tela_Procurar_Usuario : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var userAdapter: UserAdapter
    private val allUsers = mutableListOf<Usuario>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adm_tela_procurar_usuario)

        // 1. Inicializa os componentes do layout
        recyclerView = findViewById(R.id.recycler_view_users)
        searchEditText = findViewById(R.id.editTextSearch)

        // 2. Configura a RecyclerView e o Adapter
        setupRecyclerView()

        // 3. Busca todos os usuários do Firestore para a memória
        fetchAllUsers()

        // 4. Configura o listener para a barra de busca
        setupSearchListener()
    }

    private fun setupRecyclerView() {
        // Inicializa o adapter com uma lista vazia
        userAdapter = UserAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = userAdapter
    }

    private fun fetchAllUsers() {
        db.collection("usuarios")
            .get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    allUsers.clear()
                    // Converte os documentos do Firestore para objetos da classe Usuario
                    val userList = documents.toObjects(Usuario::class.java)
                    allUsers.addAll(userList)
                    // Exibe todos os usuários na lista inicialmente
                    userAdapter.updateUsers(allUsers)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Erro ao buscar usuários: ", exception)
            }
    }

    private fun setupSearchListener() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            // Este método é chamado toda vez que o texto no EditText muda
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Chama a função para filtrar a lista local de usuários
                filterUsers(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterUsers(query: String) {
        // Filtra a lista 'allUsers' que já está na memória
        val filteredList = if (query.isBlank()) {
            // Se a busca estiver vazia, mostra todos os usuários
            allUsers
        } else {
            // Filtra por nome ou email, ignorando maiúsculas/minúsculas
            allUsers.filter { user ->
                user.nome.contains(query, ignoreCase = true) ||
                        user.email.contains(query, ignoreCase = true)
            }
        }
        // Atualiza o adapter com a nova lista filtrada
        userAdapter.updateUsers(filteredList)
    }
}
