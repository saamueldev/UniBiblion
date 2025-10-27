package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Adm_Tela_Mural_Noticias_Eventos : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var noticiasAdapter: NoticiasAdapter

    // Flag que indica que esta Activity é de Administração
    private val isAdminUser = true // TRUE AQUI ATIVA O LÁPIS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_adm_mural_noticias_eventos)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // --- CONFIGURAÇÃO INICIAL ---

        recyclerView = findViewById(R.id.recycler_view_noticias)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // CORREÇÃO: Inicializa o Adapter passando a flag 'isAdminUser = true'
        // ISSO VAI FAZER O LÁPIS APARECER
        noticiasAdapter = NoticiasAdapter(mutableListOf(), isAdminUser)
        recyclerView.adapter = noticiasAdapter

        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_noticias

        val fabCreate: FloatingActionButton = findViewById(R.id.fab_create_announcement)
        fabCreate.setOnClickListener {
            val targetActivity = Adm_Tela_Criacao_Anuncio_Eventos::class.java
            val intent = Intent(this, targetActivity)
            intent.putExtra("EXTRA_MODE_EDIT", false)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        val dadosAtualizados = NoticiasRepositorySimulado.getAllNoticias()
        noticiasAdapter.updateList(dadosAtualizados)

    }
}