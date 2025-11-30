package com.example.unibiblion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class Adm_Tela_Notificacoes : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var notificationsRecyclerView: RecyclerView
    private lateinit var notificationAdapter: AdmNotificationAdapter
    private val notificationsList = mutableListOf<Notification>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // CORREÇÃO: setContentView() deve vir primeiro para "inflar" o layout
        setContentView(R.layout.activity_adm_tela_notificacoes)

        // Agora que o layout existe, podemos inicializar os componentes
        db = FirebaseFirestore.getInstance()

        // 1. Encontrar a RecyclerView no layout
        notificationsRecyclerView = findViewById(R.id.recycler_view_notificacoes_adm)

        // 2. Definir como os itens serão organizados (uma lista vertical)
        notificationsRecyclerView.layoutManager = LinearLayoutManager(this)

        // 3. Criar uma instância do seu adapter, passando a lista de notificações
        notificationAdapter = AdmNotificationAdapter(notificationsList)

        // 4. Conectar o adapter à RecyclerView
        notificationsRecyclerView.adapter = notificationAdapter

        // 5. Configurar o listener do botão de adicionar
        val buttonAdd: ImageButton = findViewById(R.id.buttonAdd)
        buttonAdd.setOnClickListener {
            val intent = Intent(this, Adm_Criar_Notificacao::class.java)
            startActivity(intent)
        }

        // 6. Chamar a função para buscar os dados do Firestore
        fetchNotifications()
    }

    private fun fetchNotifications() {
        db.collection("notifications")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.w("Firestore", "Erro ao buscar notificações", error)
                    Toast.makeText(this, "Erro ao carregar notificações.", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    notificationsList.clear()

                    val fetchedNotifications = snapshots.toObjects(Notification::class.java)
                    notificationsList.addAll(fetchedNotifications)

                    notificationAdapter.notifyDataSetChanged()
                }
            }
    }
}
