package com.example.unibiblion

data class Notification(
    val id: Int,
    val title: String,
    var isRead: Boolean = false
)