package com.example.unibiblion

import android.os.Bundle
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity

class CabinesIndividuaisActivity : AppCompatActivity() {

    // Lista de cabines que será preenchida e usada pelo Adaptador
    private lateinit var listaCabines: MutableList<Cabine>
    private lateinit var cabinesAdapter: CabinesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cabines_individuais)

        // 1. Criar os dados de exemplo
        listaCabines = criarDadosDeExemplo()

        // 2. Obter a referência ao GridView
        val gridCabines: GridView = findViewById(R.id.grid_cabines)

        // 3. Criar e conectar o Adaptador
        cabinesAdapter = CabinesAdapter(this, listaCabines)
        gridCabines.adapter = cabinesAdapter

        // 4. Configurar o clique do usuário
        gridCabines.setOnItemClickListener { parent, view, position, id ->
            // Inverte o estado de seleção da cabine clicada e redesenha
            cabinesAdapter.toggleSelection(position)
        }

        // Opcional: Configuração da barra superior e outros componentes
    }

    // Função para criar dados de teste (Cabines 1 a 25)
    private fun criarDadosDeExemplo(): MutableList<Cabine> {
        val cabines = mutableListOf<Cabine>()

        // Cria 25 cabines
        for (i in 1..25) {
            val numeroStr = String.format("%02d", i) // Formata para "01", "02", etc.

            // Define o estado de ocupação alternando entre Livre e Ocupado
            val estado = if (i % 3 == 0) Cabine.ESTADO_OCUPADO else Cabine.ESTADO_LIVRE

            cabines.add(Cabine(numeroStr, estado))
        }
        return cabines
    }
}