package com.example.unibiblion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import android.widget.PopupMenu
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class Tela_De_Perfil : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_de_perfil)

        setupHeaderClicks()
        setupBottomNavigation()
        setupContentLists()
    }

    private fun setupHeaderClicks() {
        findViewById<ImageView>(R.id.icon_bell).setOnClickListener {
            startActivity(Intent(this, Tela_Notificacoes::class.java))
        }
        val menuIcon = findViewById<ImageView>(R.id.icon_menu)
        menuIcon.setOnClickListener { view ->
            showPopupMenu(view)
        }

        findViewById<ImageView>(R.id.profile_image).setOnClickListener {
            Toast.makeText(this, "Abrir editor de Perfil", Toast.LENGTH_SHORT).show()
        }
    }
    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.menu_perfil_opcoes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_editar_perfil -> {
                    startActivity(Intent(this, Tela_De_Perfil_Dados::class.java))
                    true
                }
                R.id.action_acessibilidade -> {
                    startActivity(Intent(this, Tela_Acessibilidade::class.java))
                    true
                }
                R.id.action_configuracoes_gerais -> {
                    startActivity(Intent(this, Tela_Config_geral::class.java))
                    true
                }
                else -> false
            }
        }

        popup.show()
    }
    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_bar)
        bottomNavigationView.selectedItemId = R.id.nav_perfil

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_livraria -> {
                    startActivity(Intent(this, Tela_Central_Livraria ::class.java))
                    true
                }
                R.id.nav_noticias -> {
                    startActivity(Intent(this, NoticiasActivity::class.java))
                    true
                }
                R.id.nav_chatbot -> {
                    startActivity(Intent(this, Tela_Chat_Bot::class.java))
                    true
                }
                R.id.nav_perfil -> {
                    true
                }
                else -> false
            }
        }
    }

    private fun setupContentLists() {

        val onBookClicked: (Book) -> Unit = { book ->
            Toast.makeText(this, "Abrindo página do livro: ${book.title}", Toast.LENGTH_SHORT).show()
        }

        val favoriteBooks = listOf(
            Book("fav1", "Livro A", "url1"),
            Book("fav2", "Livro B", "url2"),
            Book("fav3", "Livro C", "url3"),
            Book("fav4", "Livro D", "url4"),
            Book("fav5", "Livro E", "url5"),
            Book("fav6", "Livro F", "url6")
        )

        val favoritesRecyclerView = findViewById<RecyclerView>(R.id.favorites_recycler_view)
        favoritesRecyclerView.layoutManager = GridLayoutManager(this, 4)
        favoritesRecyclerView.adapter = BookAdapter(favoriteBooks, onBookClicked)

        val rentedList = favoriteBooks.subList(0, 3)
        val rentedBooksRecyclerView = findViewById<RecyclerView>(R.id.rented_books_recycler_view)
        rentedBooksRecyclerView.layoutManager = GridLayoutManager(this, 4)
        rentedBooksRecyclerView.adapter = BookAdapter(rentedList, onBookClicked)

        val historyList = favoriteBooks.subList(0, 6)
        val historyRecyclerView = findViewById<RecyclerView>(R.id.history_recycler_view)
        historyRecyclerView.layoutManager = GridLayoutManager(this, 4)
        historyRecyclerView.adapter = BookAdapter(historyList, onBookClicked)
    }
}

