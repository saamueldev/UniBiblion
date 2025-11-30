package com.example.unibiblion

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class NotificationAdapter(
    private val notifications: List<Notification>,
    private val onMarkAsRead: (Notification) -> Unit,
    private val onDelete: (Notification) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val layout: ConstraintLayout = itemView.findViewById(R.id.notification_item_layout)
        private val titleTextView: TextView = itemView.findViewById(R.id.notification_title)
        private val markAsReadTextView: TextView = itemView.findViewById(R.id.mark_as_read_text)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)

        fun bind(notification: Notification) {
            titleTextView.text = notification.title

            if (notification.isRead) {
                layout.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.cinza_fundo_edittext))
                titleTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.cinza_borda_edittext))
                markAsReadTextView.visibility = View.GONE
            } else {
                layout.setBackgroundColor(Color.WHITE)
                titleTextView.setTextColor(Color.BLACK)
                markAsReadTextView.visibility = View.VISIBLE
            }

            markAsReadTextView.setOnClickListener {
                onMarkAsRead(notification)
            }

            deleteButton.setOnClickListener {
                onDelete(notification)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    override fun getItemCount(): Int = notifications.size
}
