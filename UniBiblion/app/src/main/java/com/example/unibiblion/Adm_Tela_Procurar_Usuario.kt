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

        recyclerView = findViewById(R.id.recycler_view_users)
        searchEditText = findViewById(R.id.editTextSearch)

        setupRecyclerView()
        fetchAllUsers()
        setupSearchListener()
    }

    private fun setupRecyclerView() {
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
                    val userList = documents.toObjects(Usuario::class.java)
                    allUsers.addAll(userList)
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

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterUsers(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterUsers(query: String) {
        val filteredList = if (query.isBlank()) {
            allUsers
        } else {
            allUsers.filter { user ->
                user.nome.contains(query, ignoreCase = true) ||
                        user.email.contains(query, ignoreCase = true)
            }
        }
        userAdapter.updateUsers(filteredList)
    }
}
