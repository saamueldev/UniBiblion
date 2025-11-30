package com.example.unibiblion

import android.app.Activity
import android.content.Intent

/**
 * Funções auxiliares para navegação baseada no tipo de usuário
 */
object NavigationHelper {
    
    /**
     * Navega para a tela de Livraria apropriada (Admin ou Comum)
     */
    fun navigateToLivraria(activity: Activity) {
        val intent = if (UserSessionManager.isUserAdmin()) {
            Intent(activity, Adm_Tela_Central_Livraria::class.java)
        } else {
            Intent(activity, Tela_Central_Livraria::class.java)
        }
        activity.startActivity(intent)
    }
    
    /**
     * Navega para a tela de Perfil apropriada (Admin ou Comum)
     */
    fun navigateToPerfil(activity: Activity) {
        val intent = if (UserSessionManager.isUserAdmin()) {
            Intent(activity, Adm_Tela_De_Perfil::class.java)
        } else {
            Intent(activity, Tela_De_Perfil::class.java)
        }
        activity.startActivity(intent)
    }
    
    /**
     * Navega para a tela de Notícias (comum para todos)
     */
    fun navigateToNoticias(activity: Activity) {
        val intent = Intent(activity, NoticiasActivity::class.java)
        activity.startActivity(intent)
    }
    
    /**
     * Navega para a tela de Chatbot (comum para todos)
     */
    fun navigateToChatbot(activity: Activity) {
        val intent = Intent(activity, Tela_Chat_Bot::class.java)
        activity.startActivity(intent)
    }
    
    /**
     * Configura a bottom navigation de forma padronizada
     * Deve ser chamada em todas as activities que têm navbar
     */
    fun setupBottomNavigation(
        activity: Activity,
        bottomNavigation: com.google.android.material.bottomnavigation.BottomNavigationView,
        currentTab: Int
    ) {
        bottomNavigation.selectedItemId = currentTab
        
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_livraria -> {
                    if (currentTab != R.id.nav_livraria) {
                        navigateToLivraria(activity)
                        activity.finish()
                    }
                    true
                }
                R.id.nav_noticias -> {
                    if (currentTab != R.id.nav_noticias) {
                        navigateToNoticias(activity)
                        activity.finish()
                    }
                    true
                }
                R.id.nav_chatbot -> {
                    if (currentTab != R.id.nav_chatbot) {
                        navigateToChatbot(activity)
                        activity.finish()
                    }
                    true
                }
                R.id.nav_perfil -> {
                    if (currentTab != R.id.nav_perfil) {
                        navigateToPerfil(activity)
                        activity.finish()
                    }
                    true
                }
                else -> false
            }
        }
    }
}
