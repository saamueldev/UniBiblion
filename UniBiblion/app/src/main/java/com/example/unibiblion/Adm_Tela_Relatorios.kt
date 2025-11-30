package com.example.unibiblion

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class Adm_Tela_Relatorios : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adm_relatorios)

        // Inicializa as views
        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)
        bottomNavigation = findViewById(R.id.bottom_navigation)

        // Configura o ViewPager2 com o adapter
        setupViewPager()

        // Conecta o TabLayout com o ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "📚 Livros"
                1 -> "🏢 Cabines"
                else -> "Tab $position"
            }
        }.attach()

        // Configura a bottom navigation
        setupBottomNavigation(bottomNavigation)
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter
    }

    /**
     * Configura a navegação da bottom navigation bar
     */
    private fun setupBottomNavigation(bottomNav: BottomNavigationView) {
        NavigationHelper.setupBottomNavigation(this, bottomNav, R.id.nav_livraria)
    }

    /**
     * Adapter para o ViewPager2 que gerencia os fragments
     */
    private inner class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> Fragment_Relatorio_Livros()
                1 -> Fragment_Relatorio_Cabines()
                else -> Fragment_Relatorio_Livros()
            }
        }
    }
}
