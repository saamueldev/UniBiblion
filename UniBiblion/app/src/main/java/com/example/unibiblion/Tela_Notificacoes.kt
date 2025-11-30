package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Tela_Notificacoes : AppCompatActivity() {

    private lateinit var adapter: NotificationAdapter
    private var notificationsList: MutableList<Notification> = mutableListOf()

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_notificacoes)

        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        bottomNavigation = findViewById(R.id.bottom_navigation_bar)

        val recyclerView = findViewById<RecyclerView>(R.id.notifications_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = NotificationAdapter(
            notificationsList,
            onMarkAsRead = { notif -> markAsRead(notif) },
            onDelete = { notif -> deleteNotification(notif) }
        )
        recyclerView.adapter = adapter

        fetchUserNotifications()

        val markAllAsReadButton = findViewById<TextView>(R.id.mark_all_as_read)
        markAllAsReadButton.setOnClickListener {
            markAllAsRead()
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.nav_noticias

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_livraria -> {
                    startActivity(Intent(this, Tela_Central_Livraria::class.java))
                    finish()
                    true
                }
                R.id.nav_noticias -> {
                    true
                }
                R.id.nav_perfil -> {
                    startActivity(Intent(this, Tela_De_Perfil::class.java))
                    finish()
                    true
                }
                R.id.nav_chatbot -> {
                    true
                }
                else -> false
            }
        }
    }


    private fun fetchUserNotifications() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
            return
        }
        val userId = currentUser.uid

        db.collection("notifications")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    if (error.message?.contains("indexes") == true) {
                        Toast.makeText(this, "Índice do Firestore necessário. Verifique o Logcat para o link de criação.", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Erro ao carregar notificações: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val fetchedNotifications = snapshots.toObjects(Notification::class.java)
                    notificationsList.clear()
                    notificationsList.addAll(fetchedNotifications)
                    adapter.notifyDataSetChanged()
                }
            }
    }

    private fun markAsRead(notif: Notification) {
        if (!notif.isRead && notif.id.isNotEmpty()) {
            db.collection("notifications").document(notif.id)
                .update("isRead", true)
                .addOnSuccessListener {
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Falha ao marcar como lida.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun deleteNotification(notif: Notification) {
        if (notif.id.isNotEmpty()) {
            db.collection("notifications").document(notif.id)
                .delete()
                .addOnSuccessListener {
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Falha ao excluir notificação.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun markAllAsRead() {
        val batch = db.batch()
        notificationsList.forEach { notif ->
            if (!notif.isRead && notif.id.isNotEmpty()) {
                val docRef = db.collection("notifications").document(notif.id)
                batch.update(docRef, "isRead", true)
            }
        }

        batch.commit()
            .addOnSuccessListener {
                Toast.makeText(this, "Todas as notificações foram marcadas como lidas!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Falha ao marcar todas como lidas.", Toast.LENGTH_SHORT).show()
            }
    }
}
