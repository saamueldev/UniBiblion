package com.example.unibiblion

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Usuario(
    @DocumentId
    val uid: String = "",

    val nome: String = "",
    val email: String = "",

    @get:PropertyName("profileImageUrl") @set:PropertyName("profileImageUrl")
    var fotoUrl: String? = null,

    @get:PropertyName("admin") @set:PropertyName("admin")
    var isAdmin: Boolean = false
) {
    constructor() : this("", "", "", null, false)
}
