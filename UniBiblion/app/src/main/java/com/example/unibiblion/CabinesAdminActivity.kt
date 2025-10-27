package com.example.unibiblion

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Button
import java.util.Calendar
import android.content.Intent
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.android.material.bottomnavigation.BottomNavigationView // Importação necessária

// ESTA É A VERSÃO DO ADMINISTRADOR
class CabinesAdminActivity : AppCompatActivity() {

    private lateinit var dataHoraSelecionada: Calendar
    private lateinit var dateSelectorTextView: TextView
    private lateinit var listaCabines: MutableList<Cabine>
    private lateinit var cabinesAdapter: CabinesAdapter
    private lateinit var btnReservarCabine: Button
    private lateinit var btnMinhasReservas: Button

    // 1. DECLARAÇÃO: Variável para a Bottom Navigation
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cabines_individuais)

        // 2. OBTÉM AS REFERÊNCIAS
        bottomNavigation = findViewById(R.id.bottom_navigation) // Inicializa a Bottom Nav
        dateSelectorTextView = findViewById(R.id.date_selector)
        btnReservarCabine = findViewById(R.id.btn_reservar_cabine)
        btnMinhasReservas = findViewById(R.id.btn_minhas_reservas)
        val gridCabines: GridView = findViewById(R.id.grid_cabines)

        // Configuração de Título (Opcional, para indicar que é a tela do Admin)
        supportActionBar?.title = "Mapa de Cabines - ADMIN"

        dataHoraSelecionada = Calendar.getInstance()

        // ** AÇÃO 1: ESCONDER BOTÕES DE USUÁRIO **
        btnReservarCabine.visibility = View.GONE
        btnMinhasReservas.visibility = View.GONE

        // 3. Inicializa dados e Grid
        listaCabines = criarDadosDeExemplo()

        // 4. Configura Adapters
        cabinesAdapter = CabinesAdapter(this, listaCabines)
        gridCabines.adapter = cabinesAdapter

        // 5. Configurar o clique do Grid
        gridCabines.setOnItemClickListener { parent, view, position, id ->
            val cabineClicada = listaCabines[position]
            navigateToAdminEdit(cabineClicada.numero)
        }

        // 6. Atualiza o texto do seletor (necessário para inicialização)
        atualizarTextoSeletorData(dataHoraSelecionada)

        // 7. Opcional: Desabilitar o clique no seletor de data para o Admin
        dateSelectorTextView.setOnClickListener {
            Toast.makeText(this, "Seletor desabilitado no modo Admin.", Toast.LENGTH_SHORT).show()
        }

        // 8. CONFIGURAÇÃO DA BOTTOM NAVIGATION
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_livraria -> {
                    // Item: ic_book (Livraria/Home Admin)
                    val intent = Intent(this, Adm_Tela_Central_Livraria::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }

                R.id.nav_noticias -> {
                    val intent = Intent(this, Adm_Tela_Mural_Noticias_Eventos::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_chatbot -> {
                    val intent = Intent(this, Tela_Chat_Bot::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_perfil -> {
                    val intent = Intent(this, Adm_Tela_De_Perfil::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    // 9. SOLUÇÃO: Mover a lógica de seleção para onResume()
    override fun onResume() {
        super.onResume()
        // Força a seleção do ícone de Livraria (o fluxo desta tela) sempre que a tela é retomada.
        bottomNavigation.menu.findItem(R.id.nav_livraria).isChecked = true
    }

    /**
     * Função para navegar para a tela de Edição do Administrador.
     */
    private fun navigateToAdminEdit(cabineId: String) {
        val intent = Intent(this, CabineAdminEditActivity::class.java).apply {
            putExtra("CABINE_ID", cabineId)
        }
        startActivity(intent)
    }

    // Funções de dados e formatação (sem alterações)
    private fun criarDadosDeExemplo(): MutableList<Cabine> {
        val cabines = mutableListOf<Cabine>()
        for (i in 1..25) {
            val numeroStr = String.format("%02d", i)
            val estado = if (i % 3 == 0) Cabine.ESTADO_OCUPADO else Cabine.ESTADO_LIVRE
            cabines.add(Cabine(numeroStr, estado))
        }
        return cabines
    }

    private fun atualizarTextoSeletorData(calendar: Calendar) {
        val dateFormat = SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", Locale("pt", "BR"))
        val dataFormatada = dateFormat.format(calendar.time)

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val horaFormatada = timeFormat.format(calendar.time)

        val textoFinal = "$dataFormatada às $horaFormatada"
        dateSelectorTextView.text = textoFinal
    }
}
