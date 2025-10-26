package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Adm_Tela_Central_Livraria : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_adm_tela_central_livraria)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonGerenciarCatalogo = findViewById<Button>(R.id.buttonGerenciarCatalogo)

        buttonGerenciarCatalogo.setOnClickListener {

            val intent = Intent(this, Adm_Tela_Acervo_Livros::class.java)

            startActivity(intent)
        }

        val buttonGerenciarCabines = findViewById<Button>(R.id.buttonGerenciarCabines)

        buttonGerenciarCabines.setOnClickListener {

            val intent = Intent(this, CabineAdminEditActivity::class.java)

            startActivity(intent)
        }

        val buttonGerenciamentoGeral = findViewById<Button>(R.id.buttonGerenciamentoGeral)

        buttonGerenciamentoGeral.setOnClickListener {

            val intent = Intent(this, AdminDashboardActivity::class.java)

            startActivity(intent)
        }
    }
}