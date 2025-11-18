package com.example.unibiblion

import com.google.firebase.Timestamp

data class Message(
    val role: String = "",
    val content: String = "",
    val createTime: Timestamp? = null

)