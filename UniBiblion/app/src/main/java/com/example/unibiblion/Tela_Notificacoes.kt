package com.example.unibiblion

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Tela_Notificacoes : AppCompatActivity() {

    private lateinit var notificationsList: MutableList<Notification>
    private lateinit var adapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_notificacoes)

        notificationsList = mutableListOf(
            Notification(1, "Notificação 1", false),
            Notification(2, "Notificação 2", false),
            Notification(3, "Notificação 3", true),
            Notification(4, "Notificação 4", true)
        )

        val recyclerView = findViewById<RecyclerView>(R.id.notifications_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = NotificationAdapter(
            notificationsList,
            onMarkAsRead = { notif -> markAsRead(notif) },
            onDelete = { notif -> deleteNotification(notif) }
        )
        recyclerView.adapter = adapter

        val markAllAsReadButton = findViewById<TextView>(R.id.mark_all_as_read)
        markAllAsReadButton.setOnClickListener {
            markAllAsRead()
            Toast.makeText(this, "Todas as notificações marcadas como lidas!", Toast.LENGTH_SHORT).show()
        }

    }

    private fun markAsRead(notif: Notification) {
        if (!notif.isRead) {
            notif.isRead = true
            adapter.updateData()
            Toast.makeText(this, "${notif.title} marcada como lida.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteNotification(notif: Notification) {
        val position = notificationsList.indexOf(notif)
        if (position != -1) {
            notificationsList.removeAt(position)
            adapter.notifyItemRemoved(position)
            Toast.makeText(this, "${notif.title} excluída.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun markAllAsRead() {
        notificationsList.forEach { it.isRead = true }
        adapter.updateData()
    }
}