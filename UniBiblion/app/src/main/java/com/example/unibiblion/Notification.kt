package com.example.unibiblion

import com.google.firebase.firestore.DocumentId

data class Notification(
    @DocumentId val id: String = "",
    val title: String = "",
    var isRead: Boolean = false,
    val userId: String = ""
)
