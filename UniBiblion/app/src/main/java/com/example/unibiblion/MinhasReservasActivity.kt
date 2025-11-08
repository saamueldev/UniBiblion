package com.example.unibiblion

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth // Importar

class MinhasReservasActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var auth: FirebaseAuth // Variável para o Firebase Auth

    // Títulos das abas (Tabs)
    private val tabTitles = listOf("Reservas Ativas", "Histórico")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minhas_reservas)

        supportActionBar?.hide()

        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)

        auth = FirebaseAuth.getInstance() // Inicializar Auth

        // ==========================================================
        // ⚠️ AUTENTICAÇÃO TEMPORÁRIA PARA TESTE ⚠️
        // Faz o login do usuário de teste antes de configurar a UI
        // ==========================================================
        auth.signInWithEmailAndPassword("teste@unibiblion.com", "senha12345")
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login de teste bem-sucedido. Continua a configuração.
                    configurarUI()
                } else {
                    Toast.makeText(this, "ERRO: Falha no login de teste. Verifique credenciais.", Toast.LENGTH_LONG).show()
                }
            }
    }

    /**
     * Configura o ViewPager e o TabLayout após o login ser confirmado.
     */
    private fun configurarUI() {
        // 1. Configurar o Adapter para o ViewPager2
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        // 2. Conectar o TabLayout ao ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

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
                // Posição 1: Histórico (Reservas CONCLUIDAS)
                1 -> ReservasListFragment.newInstance(StatusReserva.CONCLUIDA)
                else -> throw IllegalStateException("Posição de fragmento inválida")
            }
        }
    }
}