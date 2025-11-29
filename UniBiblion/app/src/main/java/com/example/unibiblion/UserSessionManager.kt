package com.example.unibiblion

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Singleton para gerenciar o estado do usuário atual (Admin ou Comum)
 * Deve ser inicializado no login e consultado em toda navegação
 */
object UserSessionManager {
    
    private var isAdmin: Boolean = false
    private var isInitialized: Boolean = false
    
    /**
     * Verifica no Firestore se o usuário atual é administrador
     * Deve ser chamado após o login bem-sucedido
     */
    fun checkAndSetUserType(onComplete: (Boolean) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        
        if (currentUser == null) {
            isAdmin = false
            isInitialized = true
            onComplete(false)
            return
        }
        
        // Verifica na coleção de administradores
        FirebaseFirestore.getInstance()
            .collection("administradores")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->
                isAdmin = document.exists()
                isInitialized = true
                onComplete(isAdmin)
            }
            .addOnFailureListener {
                isAdmin = false
                isInitialized = true
                onComplete(false)
            }
    }
    
    /**
     * Retorna true se o usuário atual é administrador
     */
    fun isUserAdmin(): Boolean = isAdmin
    
    /**
     * Retorna true se a verificação já foi realizada
     */
    fun isSessionInitialized(): Boolean = isInitialized
    
    /**
     * Limpa a sessão (usar no logout)
     */
    fun clearSession() {
        isAdmin = false
        isInitialized = false
    }
    
    /**
     * Define manualmente o tipo de usuário (usar quando já souber o tipo)
     */
    fun setUserType(isAdministrator: Boolean) {
        isAdmin = isAdministrator
        isInitialized = true
    }
}
