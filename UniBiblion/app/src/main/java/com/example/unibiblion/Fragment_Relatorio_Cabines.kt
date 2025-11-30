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

class Fragment_Relatorio_Cabines : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RelatorioCabineAdapter
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
        val view = inflater.inflate(R.layout.fragment_relatorio_cabines, container, false)
        
        db = FirebaseFirestore.getInstance()
        
        // Inicializa as views
        recyclerView = view.findViewById(R.id.recycler_view_cabines)
        barChart = view.findViewById(R.id.bar_chart_cabines)
        tvPeriodoSelecionado = view.findViewById(R.id.tv_periodo_selecionado)
        tvEmptyState = view.findViewById(R.id.tv_empty_state)
        
        // Configura o RecyclerView
        adapter = RelatorioCabineAdapter(emptyList())
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
        carregarDadosDoFirestore()
    }

    private fun filtrarPorMes() {
        val calendar = Calendar.getInstance()
        dataFim = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        dataInicio = calendar.time
        
        tvPeriodoSelecionado.text = "Período: Últimos 30 dias"
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
        
        Log.d("RelatorioCabines", "Buscando reservas entre ${sdf.format(dataInicio)} e ${sdf.format(dataFim)}")
        
        db.collection("reservas")
            .whereGreaterThanOrEqualTo("timestampCriacao", timestampInicio)
            .whereLessThanOrEqualTo("timestampCriacao", timestampFim)
            .get()
            .addOnSuccessListener { documents ->
                val listaRelatorios = mutableListOf<RelatorioCabine>()
                val usuariosIds = mutableSetOf<String>()
                
                // Coleta os IDs dos usuários
                for (doc in documents) {
                    val usuarioId = doc.getString("usuarioId") ?: ""
                    if (usuarioId.isNotEmpty()) {
                        usuariosIds.add(usuarioId)
                    }
                }
                
                // Se não há dados, mostra mensagem vazia
                if (documents.isEmpty) {
                    mostrarEstadoVazio(true)
                    configurarGrafico(emptyList())
                    return@addOnSuccessListener
                }
                
                // Busca os dados dos usuários
                buscarDadosUsuarios(usuariosIds) { mapUsuarios ->
                    for (doc in documents) {
                        val usuarioId = doc.getString("usuarioId") ?: ""
                        val nomeUsuario = mapUsuarios[usuarioId] ?: "Usuário desconhecido"
                        val dataReserva = doc.getString("dataReserva") ?: ""
                        val horaFim = doc.getString("horaFim") ?: ""
                        val statusOriginal = doc.getString("status") ?: "ATIVA"
                        
                        // Calcula o status real baseado na data/hora
                        val statusReal = calcularStatusReal(dataReserva, horaFim, statusOriginal)
                        
                        val relatorio = RelatorioCabine(
                            id = doc.id,
                            cabineNumero = doc.getString("cabineNumero") ?: "",
                            usuarioId = usuarioId,
                            nomeUsuario = nomeUsuario,
                            emailUsuario = "",
                            dataReserva = dataReserva,
                            horaInicio = doc.getString("horaInicio") ?: "",
                            horaFim = horaFim,
                            status = statusReal,
                            timestampCriacao = doc.getTimestamp("timestampCriacao")
                        )
                        listaRelatorios.add(relatorio)
                    }
                    
                    // Ordena por timestamp de criação (mais recente primeiro)
                    listaRelatorios.sortByDescending { it.timestampCriacao?.toDate() }
                    
                    // Atualiza a lista e o gráfico
                    adapter.atualizarLista(listaRelatorios)
                    configurarGrafico(listaRelatorios)
                    mostrarEstadoVazio(false)
                    
                    Log.d("RelatorioCabines", "Carregadas ${listaRelatorios.size} reservas")
                }
            }
            .addOnFailureListener { e ->
                Log.e("RelatorioCabines", "Erro ao carregar dados", e)
                Toast.makeText(requireContext(), "Erro ao carregar relatórios", Toast.LENGTH_SHORT).show()
                mostrarEstadoVazio(true)
            }
    }

    private fun buscarDadosUsuarios(usuariosIds: Set<String>, callback: (Map<String, String>) -> Unit) {
        if (usuariosIds.isEmpty()) {
            callback(emptyMap())
            return
        }
        
        val mapUsuarios = mutableMapOf<String, String>()
        var contador = 0
        
        for (userId in usuariosIds) {
            db.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        mapUsuarios[userId] = doc.getString("nome") ?: "Usuário desconhecido"
                    }
                    contador++
                    if (contador == usuariosIds.size) {
                        callback(mapUsuarios)
                    }
                }
                .addOnFailureListener {
                    contador++
                    if (contador == usuariosIds.size) {
                        callback(mapUsuarios)
                    }
                }
        }
    }

    private fun configurarGrafico(lista: List<RelatorioCabine>) {
        if (lista.isEmpty()) {
            barChart.clear()
            barChart.setNoDataText("Nenhum dado para exibir")
            return
        }
        
        // Agrupa por data da reserva
        val mapPorData = mutableMapOf<String, Int>()
        val sdfInput = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sdfGrafico = SimpleDateFormat("dd/MM", Locale.getDefault())
        
        for (relatorio in lista) {
            try {
                val data = sdfInput.parse(relatorio.dataReserva)
                val dataStr = data?.let { sdfGrafico.format(it) } ?: relatorio.dataReserva
                mapPorData[dataStr] = (mapPorData[dataStr] ?: 0) + 1
            } catch (e: Exception) {
                Log.e("RelatorioCabines", "Erro ao parsear data: ${relatorio.dataReserva}", e)
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
        val dataSet = BarDataSet(entries, "Quantidade de Reservas")
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

    /**
     * Calcula o status real da reserva baseado na data, hora e status original.
     * Se a reserva está ATIVA mas a data/hora já passou, retorna FINALIZADA.
     */
    private fun calcularStatusReal(dataReserva: String, horaFim: String, statusOriginal: String): String {
        // Se já está CANCELADA, mantém o status
        if (statusOriginal == "CANCELADA") {
            return statusOriginal
        }
        
        try {
            // Formato da data: "yyyy-MM-dd" (ex: "2025-11-19")
            // Formato da hora: "HH:mm" (ex: "16:00")
            val sdfDataHora = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val dataHoraReserva = sdfDataHora.parse("$dataReserva $horaFim")
            
            val agora = Date()
            
            // Se a data/hora de fim já passou, marca como FINALIZADA
            if (dataHoraReserva != null && dataHoraReserva.before(agora)) {
                return "FINALIZADA"
            }
            
            // Caso contrário, mantém o status original (ATIVA)
            return statusOriginal
            
        } catch (e: Exception) {
            Log.e("RelatorioCabines", "Erro ao calcular status real: ${e.message}")
            // Em caso de erro no parse, mantém o status original
            return statusOriginal
        }
    }

    private fun setupExportPdfButton(view: View) {
        view.findViewById<MaterialButton>(R.id.btn_exportar_pdf).setOnClickListener {
            exportarPdfCabines()
        }
    }

    private fun exportarPdfCabines() {
        val listaAtual = adapter.getListaCabines()
        
        if (listaAtual.isEmpty()) {
            Toast.makeText(requireContext(), "Nenhuma reserva para exportar", Toast.LENGTH_SHORT).show()
            return
        }
        
        val arquivo = PdfExporter.gerarPdfCabines(requireContext(), listaAtual)
        
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
