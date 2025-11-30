package com.example.unibiblion

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MinhasReservasActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var auth: FirebaseAuth

    // Títulos das abas (Tabs)
    private val tabTitles = listOf("Reservas Ativas", "Histórico")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minhas_reservas)

        supportActionBar?.hide()

        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)

        auth = FirebaseAuth.getInstance() // Inicializar Auth

        // 🎯 LÓGICA DE AUTENTICAÇÃO REAL
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Se houver um usuário real (autenticado), configura a UI
            configurarUI(currentUser)
        } else {
            // Caso contrário, mostra um erro e encerra a Activity
            Toast.makeText(this, "Usuário não autenticado. Acesso negado.", Toast.LENGTH_LONG).show()
            finish()
        }

        // ⚠️ REMOVIDO: O bloco auth.signInWithEmailAndPassword temporário
    }

    /**
     * Configura o ViewPager e o TabLayout após a autenticação ser confirmada.
     */
    private fun configurarUI(currentUser: FirebaseUser) {
        // 1. Configurar o Adapter para o ViewPager2
        val adapter = ViewPagerAdapter(this) // ESTE ERA ONDE A REFERÊNCIA ESTAVA FALHANDO
        viewPager.adapter = adapter

        // 2. Conectar o TabLayout ao ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    // 🎯 CLASSE INTERNA: A DEFINIÇÃO QUE ESTAVA FALTANDO OU FOI PERDIDA
    // Adapter Interno para gerenciar os Fragments das abas
    private inner class ViewPagerAdapter(activity: AppCompatActivity) :
        FragmentStateAdapter(activity) {

        // Retorna o número de abas
        override fun getItemCount(): Int = tabTitles.size

        // Cria o Fragmento correto para cada posição da aba
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                // Posição 0: Reservas ATIVAS (Futuras e Canceláveis)
                0 -> ReservasListFragment.newInstance(StatusReserva.ATIVA)
                // Posição 1: Histórico (Reservas FINALIZADAS)
                1 -> ReservasListFragment.newInstance(StatusReserva.FINALIZADA)
                else -> throw IllegalStateException("Posição de fragmento inválida")
            }
        }
    }
}