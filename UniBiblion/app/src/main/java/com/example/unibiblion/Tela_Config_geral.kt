package com.example.unibiblion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.bottomnavigation.BottomNavigationView

class Tela_Config_geral : AppCompatActivity() {

    private lateinit var switchNotificacoes: SwitchCompat
    private lateinit var btnSobreOApp: MaterialButton
    private lateinit var btnSairDaConta: MaterialButton
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_config_geral)

        switchNotificacoes = findViewById(R.id.switch_notificacoes)
        btnSobreOApp = findViewById(R.id.btn_sobre_o_app)
        btnSairDaConta = findViewById(R.id.btn_sair_da_conta)
        bottomNavigation = findViewById(R.id.bottom_navigation_bar)

        switchNotificacoes.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Notificações Ativadas", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notificações Desativadas", Toast.LENGTH_SHORT).show()
            }
        }

        btnSobreOApp.setOnClickListener {
            val intent = Intent(this, Tela_Sobre_sistema::class.java)
            startActivity(intent)
        }

        btnSairDaConta.setOnClickListener {
            Toast.makeText(this, "Realizando Logout...", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, Tela_Login::class.java)

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        }

        bottomNavigation.selectedItemId = R.id.nav_chatbot

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_livraria -> {
                    startActivity(Intent(this, Tela_Central_Livraria::class.java))
                    finish()
                    true
                }

                R.id.nav_noticias -> {
                    startActivity(Intent(this, NoticiasActivity::class.java))
                    finish()
                    true
                }

                R.id.nav_perfil -> {
                    startActivity(Intent(this, Tela_De_Perfil::class.java))
                    finish()
                    true
                }

                R.id.nav_chatbot -> {
                    startActivity(Intent(this, Tela_Chat_Bot::class.java))
                    finish()
                    true
                }

                else -> false
            }
        }
    }
}