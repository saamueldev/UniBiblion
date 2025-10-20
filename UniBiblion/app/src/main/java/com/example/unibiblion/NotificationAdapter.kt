package com.example.unibiblion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class NotificationAdapter(
    private val notifications: MutableList<Notification>,
    private val onDeleteClicked: (Notification, Int) -> Unit,
    private val onMarkAsReadClicked: (Notification, Int) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layout: ConstraintLayout = itemView.findViewById(R.id.notification_item_layout)
        val title: TextView = itemView.findViewById(R.id.notification_title)
        val markAsReadText: TextView = itemView.findViewById(R.id.mark_as_read_text)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = notifications[position]
        holder.title.text = item.title

        if (item.isRead) {
            holder.title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.gray_100))
            holder.deleteButton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.gray_100))
            holder.markAsReadText.visibility = View.GONE
        } else {
            holder.title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
            holder.deleteButton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.black))
            holder.markAsReadText.visibility = View.VISIBLE
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClicked(item, position)
        }

        if (!item.isRead) {
            holder.markAsReadText.setOnClickListener {
                onMarkAsReadClicked(item, position)
            }
        }
    }

    override fun getItemCount(): Int = notifications.size

    fun removeItem(position: Int) {
        notifications.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, notifications.size)
    }

    fun updateItem(position: Int) {
        notifyItemChanged(position)
    }
}