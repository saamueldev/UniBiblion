package com.example.unibiblion

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.button.MaterialButton
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Fragment_Relatorio_Livros : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RelatorioLivroAdapter
    private lateinit var barChart: BarChart
    private lateinit var tvPeriodoSelecionado: TextView
    private lateinit var tvEmptyState: TextView
    
    private var dataInicio: Date = Date()
    private var dataFim: Date = Date()
    private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_relatorio_livros, container, false)
        
        db = FirebaseFirestore.getInstance()
        
        // Inicializa as views
        recyclerView = view.findViewById(R.id.recycler_view_livros)
        barChart = view.findViewById(R.id.bar_chart_livros)
        tvPeriodoSelecionado = view.findViewById(R.id.tv_periodo_selecionado)
        tvEmptyState = view.findViewById(R.id.tv_empty_state)
        
        // Configura o RecyclerView
        adapter = RelatorioLivroAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        
        // Configura os botões de filtro
        setupFilterButtons(view)
        
        // Configura botão de exportar PDF
        setupExportPdfButton(view)
        
        // Carrega dados da última semana por padrão
        filtrarPorSemana()
        
        return view
    }

    private fun setupFilterButtons(view: View) {
        view.findViewById<MaterialButton>(R.id.btn_filter_week).setOnClickListener {
            filtrarPorSemana()
        }
        
        view.findViewById<MaterialButton>(R.id.btn_filter_month).setOnClickListener {
            filtrarPorMes()
        }
        
        view.findViewById<MaterialButton>(R.id.btn_filter_custom).setOnClickListener {
            mostrarSeletorPeriodoPersonalizado()
        }
    }

    private fun filtrarPorSemana() {
        val calendar = Calendar.getInstance()
        dataFim = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        dataInicio = calendar.time
        
        tvPeriodoSelecionado.text = "Período: Últimos 7 dias"
        Log.d("RelatorioLivros", "Filtrando por semana: ${sdf.format(dataInicio)} até ${sdf.format(dataFim)}")
        carregarDadosDoFirestore()
    }

    private fun filtrarPorMes() {
        val calendar = Calendar.getInstance()
        dataFim = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        dataInicio = calendar.time
        
        tvPeriodoSelecionado.text = "Período: Últimos 30 dias"
        Log.d("RelatorioLivros", "Filtrando por mês: ${sdf.format(dataInicio)} até ${sdf.format(dataFim)}")
        carregarDadosDoFirestore()
    }

    private fun mostrarSeletorPeriodoPersonalizado() {
        val calendar = Calendar.getInstance()
        
        // DatePicker para data inicial
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val calInicio = Calendar.getInstance()
                calInicio.set(year, month, day, 0, 0, 0)
                dataInicio = calInicio.time
                
                // DatePicker para data final
                DatePickerDialog(
                    requireContext(),
                    { _, yearFim, monthFim, dayFim ->
                        val calFim = Calendar.getInstance()
                        calFim.set(yearFim, monthFim, dayFim, 23, 59, 59)
                        dataFim = calFim.time
                        
                        tvPeriodoSelecionado.text = "Período: ${sdf.format(dataInicio)} - ${sdf.format(dataFim)}"
                        carregarDadosDoFirestore()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun carregarDadosDoFirestore() {
        val timestampInicio = Timestamp(dataInicio)
        val timestampFim = Timestamp(dataFim)
        
        Log.d("RelatorioLivros", "=== INICIANDO BUSCA ===")
        Log.d("RelatorioLivros", "Período: ${sdf.format(dataInicio)} até ${sdf.format(dataFim)}")
        Log.d("RelatorioLivros", "Timestamp Início: $timestampInicio")
        Log.d("RelatorioLivros", "Timestamp Fim: $timestampFim")
        
        // Primeiro, vamos buscar TODOS os documentos para debug
        db.collection("livrosalugados")
            .get()
            .addOnSuccessListener { allDocs ->
                Log.d("RelatorioLivros", "Total de documentos na coleção: ${allDocs.size()}")
                
                // Mostra os primeiros 3 documentos para debug
                allDocs.documents.take(3).forEachIndexed { index, doc ->
                    Log.d("RelatorioLivros", "Doc $index - timestampCriacao: ${doc.getTimestamp("timestampCriacao")}")
                    Log.d("RelatorioLivros", "Doc $index - dataDevolucao: ${doc.getTimestamp("dataDevolucao")}")
                    Log.d("RelatorioLivros", "Doc $index - dataRetirada: ${doc.getString("dataRetirada")}")
                    Log.d("RelatorioLivros", "Doc $index - titulo: ${doc.getString("titulo")}")
                }
                
                // Filtra manualmente os documentos por dataRetirada
                val listaRelatorios = mutableListOf<RelatorioLivro>()
                val usuariosIds = mutableSetOf<String>()
                val sdfData = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                
                for (doc in allDocs.documents) {
                    val dataRetiradaStr = doc.getString("dataRetirada") ?: ""
                    
                    try {
                        // Converte dataRetirada (string "dd/MM/yyyy") para Date
                        val dataRetiradaDate = sdfData.parse(dataRetiradaStr)
                        
                        // Verifica se a data de retirada está no período
                        if (dataRetiradaDate != null && 
                            dataRetiradaDate.time >= dataInicio.time && 
                            dataRetiradaDate.time <= dataFim.time) {
                            
                            val usuarioId = doc.getString("usuarioId") ?: ""
                            if (usuarioId.isNotEmpty()) {
                                usuariosIds.add(usuarioId)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("RelatorioLivros", "Erro ao parsear data: $dataRetiradaStr", e)
                    }
                }
                
                Log.d("RelatorioLivros", "Documentos encontrados no período (por data de retirada): ${usuariosIds.size}")
                
                // Se não há dados, mostra mensagem vazia
                if (usuariosIds.isEmpty()) {
                    Log.w("RelatorioLivros", "Nenhum documento encontrado no período especificado")
                    mostrarEstadoVazio(true)
                    configurarGrafico(emptyList())
                    return@addOnSuccessListener
                }
                
                // Busca os dados dos usuários
                buscarDadosUsuarios(usuariosIds) { mapUsuarios ->
                    for (doc in allDocs.documents) {
                        val dataRetiradaStr = doc.getString("dataRetirada") ?: ""
                        
                        try {
                            val dataRetiradaDate = sdfData.parse(dataRetiradaStr)
                            
                            if (dataRetiradaDate != null && 
                                dataRetiradaDate.time >= dataInicio.time && 
                                dataRetiradaDate.time <= dataFim.time) {
                                
                                val usuarioId = doc.getString("usuarioId") ?: ""
                                val nomeUsuario = mapUsuarios[usuarioId] ?: "Usuário desconhecido"
                                val devolvido = doc.getBoolean("devolvido") ?: false
                                
                                val relatorio = RelatorioLivro(
                                    id = doc.id,
                                    livroId = doc.getString("livroId") ?: "",
                                    tituloLivro = doc.getString("titulo") ?: "Título não disponível",
                                    capaURL = doc.getString("capaURL") ?: "",
                                    usuarioId = usuarioId,
                                    nomeUsuario = nomeUsuario,
                                    emailUsuario = "",
                                    dataRetirada = dataRetiradaStr,
                                    horarioRetirada = doc.getString("horarioRetirada") ?: "",
                                    dataDevolucao = doc.getTimestamp("dataDevolucao"),
                                    renovado = doc.getBoolean("renovado") ?: false,
                                    timestampCriacao = doc.getTimestamp("timestampCriacao"),
                                    devolvido = devolvido,
                                    dataRealDevolucao = doc.getTimestamp("dataRealDevolucao")
                                )
                                listaRelatorios.add(relatorio)
                            }
                        } catch (e: Exception) {
                            Log.e("RelatorioLivros", "Erro ao processar documento ${doc.id}", e)
                        }
                    }
                    
                    // Ordena por data de retirada (mais recente primeiro)
                    listaRelatorios.sortByDescending { 
                        try {
                            sdfData.parse(it.dataRetirada)?.time ?: 0
                        } catch (e: Exception) {
                            0
                        }
                    }
                    
                    // Atualiza a lista e o gráfico
                    adapter.atualizarLista(listaRelatorios)
                    configurarGrafico(listaRelatorios)
                    mostrarEstadoVazio(false)
                    
                    Log.d("RelatorioLivros", "Carregados ${listaRelatorios.size} aluguéis")
                }
            }
            .addOnFailureListener { e ->
                Log.e("RelatorioLivros", "Erro ao buscar todos os documentos", e)
                Toast.makeText(requireContext(), "Erro ao carregar relatórios: ${e.message}", Toast.LENGTH_SHORT).show()
                mostrarEstadoVazio(true)
            }
    }

    private fun buscarDadosUsuarios(usuariosIds: Set<String>, callback: (Map<String, String>) -> Unit) {
        if (usuariosIds.isEmpty()) {
            Log.w("RelatorioLivros", "Nenhum usuário para buscar!")
            callback(emptyMap())
            return
        }
        
        Log.d("RelatorioLivros", "Buscando dados de ${usuariosIds.size} usuários: $usuariosIds")
        
        val mapUsuarios = mutableMapOf<String, String>()
        var contador = 0
        
        for (userId in usuariosIds) {
            Log.d("RelatorioLivros", "Buscando usuário com ID: $userId")
            
            db.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        val nome = doc.getString("nome") ?: "Usuário desconhecido"
                        mapUsuarios[userId] = nome
                        Log.d("RelatorioLivros", "Usuário encontrado: $userId = $nome")
                    } else {
                        Log.w("RelatorioLivros", "Documento do usuário não existe: $userId")
                    }
                    contador++
                    if (contador == usuariosIds.size) {
                        Log.d("RelatorioLivros", "Busca de usuários concluída. Total: ${mapUsuarios.size}")
                        callback(mapUsuarios)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("RelatorioLivros", "Erro ao buscar usuário $userId", e)
                    contador++
                    if (contador == usuariosIds.size) {
                        callback(mapUsuarios)
                    }
                }
        }
    }

    private fun configurarGrafico(lista: List<RelatorioLivro>) {
        if (lista.isEmpty()) {
            barChart.clear()
            barChart.setNoDataText("Nenhum dado para exibir")
            return
        }
        
        // Agrupa por data
        val mapPorData = mutableMapOf<String, Int>()
        val sdfGrafico = SimpleDateFormat("dd/MM", Locale.getDefault())
        
        for (relatorio in lista) {
            relatorio.dataDevolucao?.let { timestamp ->
                val dataStr = sdfGrafico.format(timestamp.toDate())
                mapPorData[dataStr] = (mapPorData[dataStr] ?: 0) + 1
            }
        }
        
        // Ordena por data
        val datasOrdenadas = mapPorData.keys.toList()
        
        // Cria as entradas do gráfico
        val entries = mutableListOf<BarEntry>()
        datasOrdenadas.forEachIndexed { index, data ->
            entries.add(BarEntry(index.toFloat(), mapPorData[data]?.toFloat() ?: 0f))
        }
        
        // Configura o dataset
        val dataSet = BarDataSet(entries, "Quantidade de Aluguéis")
        dataSet.color = Color.parseColor("#7644D4") // roxo_institucional
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 10f
        
        val barData = BarData(dataSet)
        barChart.data = barData
        
        // Configurações visuais do gráfico
        barChart.description.isEnabled = false
        barChart.setDrawGridBackground(false)
        barChart.animateY(500)
        
        // Configura o eixo X
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(datasOrdenadas)
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        
        // Configura o eixo Y
        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.isEnabled = false
        
        barChart.invalidate() // Refresh
    }

    private fun mostrarEstadoVazio(vazio: Boolean) {
        if (vazio) {
            tvEmptyState.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvEmptyState.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun setupExportPdfButton(view: View) {
        view.findViewById<MaterialButton>(R.id.btn_exportar_pdf).setOnClickListener {
            exportarPdfLivros()
        }
    }

    private fun exportarPdfLivros() {
        val listaAtual = adapter.getListaLivros()
        
        if (listaAtual.isEmpty()) {
            Toast.makeText(requireContext(), "Nenhum aluguel para exportar", Toast.LENGTH_SHORT).show()
            return
        }
        
        val arquivo = PdfExporter.gerarPdfLivros(requireContext(), listaAtual)
        
        if (arquivo != null) {
            Toast.makeText(
                requireContext(),
                "PDF gerado com sucesso!\n${arquivo.absolutePath}",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(requireContext(), "Erro ao gerar PDF", Toast.LENGTH_SHORT).show()
        }
    }
}
