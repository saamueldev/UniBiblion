package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        // Configuração da Toolbar
        setSupportActionBar(findViewById(R.id.toolbar_admin))
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Habilita o botão Voltar

        // 1. Botão "Gerenciar Reviews" (RF04.02.09)
        findViewById<CardView>(R.id.card_manage_reviews).setOnClickListener {
            // Navega para a tela de gerenciamento de Reviews
            val intent = Intent(this, AdminReviewsActivity::class.java)
            startActivity(intent)
        }

        // Removed card_manage_cabines listener, as it was removed from the XML.
    }
}
