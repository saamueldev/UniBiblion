// File: AdmNotificationAdapter.kt
package com.example.unibiblion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// O adapter agora recebe a sua classe Notification
class AdmNotificationAdapter(private val notifications: List<Notification>) :
    RecyclerView.Adapter<AdmNotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification_adm, parent, false) // Usa o layout do item que criamos
        return NotificationViewHolder(view)
    }

    override fun getItemCount(): Int = notifications.size

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.bind(notification)
    }

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.item_notification_title)
        // O TextView para o 'body' ainda pode existir no XML, mas não será usado aqui.
        private val bodyTextView: TextView = itemView.findViewById(R.id.item_notification_body)

        fun bind(notification: Notification) {
            // Conecta o 'title' da sua classe ao TextView
            titleTextView.text = notification.title

            // Esconde o TextView do corpo, já que não há dados para ele
            bodyTextView.visibility = View.GONE
        }
    }
}
