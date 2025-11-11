package com.example.unibiblion

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotificationAdapter(
    private val notifications: MutableList<Notification>,
    private val onMarkAsRead: (Notification) -> Unit,
    private val onDelete: (Notification) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.notification_title)
        val markAsReadLink: TextView = itemView.findViewById(R.id.mark_as_read_text)
        val deleteIcon: ImageButton = itemView.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notif = notifications[position]

        holder.title.text = notif.title

        if (notif.isRead) {
            holder.title.setTextColor(Color.DKGRAY)
            holder.title.setTypeface(null, Typeface.NORMAL)
            holder.markAsReadLink.visibility = View.GONE
        } else {
            holder.title.setTextColor(Color.BLACK)
            holder.title.setTypeface(null, Typeface.BOLD)
            holder.markAsReadLink.visibility = View.VISIBLE
        }

        holder.markAsReadLink.setOnClickListener {
            onMarkAsRead(notif)
        }

        holder.deleteIcon.setOnClickListener {
            onDelete(notif)
        }

        holder.itemView.setOnClickListener {
            if (!notif.isRead) {
                onMarkAsRead(notif)
            }
        }
    }

    override fun getItemCount() = notifications.size

    fun updateData() {
        notifyDataSetChanged()
    }
}