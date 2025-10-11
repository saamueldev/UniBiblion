package com.example.unibiblion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MinhasReservasActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    // Títulos das abas (Tabs)
    private val tabTitles = listOf("Reservas Ativas", "Histórico")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minhas_reservas)

        supportActionBar?.hide()

        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)

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
                0 -> ReservasListFragment.newInstance(StatusReserva.ATIVA)
                1 -> ReservasListFragment.newInstance(StatusReserva.CONCLUIDA)
                else -> throw IllegalStateException("Posição de fragmento inválida")
            }
        }
    }

    // TO-DO (Próximo passo): Função para carregar os dados reais, substituir a função criarDadosDeExemplo
}