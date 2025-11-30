// File: UserAdapter.kt
package com.example.unibiblion

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class UserAdapter(private var userList: List<Usuario>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_usuario, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int = userList.size

    fun updateUsers(newList: List<Usuario>) {
        userList = newList
        notifyDataSetChanged()
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.text_view_user_name)
        private val emailTextView: TextView = itemView.findViewById(R.id.text_view_user_email)
        private val profileImageView: ImageView = itemView.findViewById(R.id.image_view_profile)

        fun bind(user: Usuario) {
            nameTextView.text = user.nome
            emailTextView.text = user.email

            Glide.with(itemView.context)
                .load(user.fotoUrl)
                .placeholder(R.drawable.ic_profile)
                .circleCrop()
                .into(profileImageView)

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, Adm_Tela_Perfil_Usuario::class.java).apply {
                    putExtra("USER_ID", user.uid)
                }
                context.startActivity(intent)
            }
        }
    }
}
