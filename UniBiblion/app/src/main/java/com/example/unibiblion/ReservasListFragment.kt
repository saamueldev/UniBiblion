package com.example.unibiblion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder // Import para o AlertDialog
import java.util.Calendar
import java.util.Locale

class ReservasListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReservasAdapter
    private val reservas = mutableListOf<Reserva>()

    private lateinit var statusFilter: StatusReserva

    // ... (Companion Object e newInstance() permanecem iguais) ...
    companion object {
        private const val ARG_STATUS = "status_filtro"
        fun newInstance(status: StatusReserva): ReservasListFragment {
            val fragment = ReservasListFragment()
            val args = Bundle()
            args.putSerializable(ARG_STATUS, status)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            statusFilter = it.getSerializable(ARG_STATUS) as StatusReserva
        } ?: run {
            statusFilter = StatusReserva.ATIVA
        }
        reservas.addAll(criarDadosDeExemplo(statusFilter))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reservas_list, container, false)
        recyclerView = view.findViewById(R.id.recycler_reservas)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // NOVO: Aqui passamos o que fazer quando o botão CANCELAR for clicado
        adapter = ReservasAdapter(reservas) { reserva ->
            // Ação de clique: Mostra o diálogo de confirmação
            mostrarDialogoCancelamento(reserva)
        }
        recyclerView.adapter = adapter

        return view
    }

    /**
     * Exibe um AlertDialog para confirmar o cancelamento da reserva.
     */
    private fun mostrarDialogoCancelamento(reserva: Reserva) {
        // Usamos requireContext() porque estamos em um Fragment
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirmar Cancelamento")
            .setMessage("Tem certeza que deseja cancelar sua reserva da Cabine ${reserva.numeroCabine}?")
            // Botão POSITIVO (SIM) - Executa o cancelamento
            .setPositiveButton("Sim, Cancelar") { dialog, which ->
                // Chamamos a função real de cancelamento
                cancelarReserva(reserva)
                dialog.dismiss()
            }
            // Botão NEGATIVO (NÃO) - Apenas fecha o diálogo
            .setNegativeButton("Manter Reserva") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Executa a lógica de cancelamento (simulada).
     */
    private fun cancelarReserva(reserva: Reserva) {
        // 1. Simulação de Lógica de Negócio:
        // Na vida real, aqui você faria uma chamada API para o backend.

        // 2. Atualizar a UI:

        // Localiza a reserva na lista pelo ID (ou posição)
        val index = reservas.indexOfFirst { it.id == reserva.id }

        if (index != -1) {
            // Remove a reserva da lista ATIVA
            reservas.removeAt(index)

            // Notifica o Adapter sobre a mudança para atualizar a RecyclerView
            adapter.notifyItemRemoved(index)

            // Exibe mensagem de sucesso
            Toast.makeText(context, "Reserva ${reserva.numeroCabine} cancelada com sucesso!", Toast.LENGTH_LONG).show()

            // NOTA: Se tivéssemos a lista de HISTÓRICO carregada,
            // a reserva cancelada seria movida para lá (como StatusReserva.CANCELADA)
            // mas, como estamos em Fragments separados, isso exigiria um ViewModel.
        } else {
            Toast.makeText(context, "Erro ao localizar reserva.", Toast.LENGTH_SHORT).show()
        }
    }

    // ... (Função criarDadosDeExemplo permanece igual) ...
    private fun criarDadosDeExemplo(status: StatusReserva): List<Reserva> {
        // ... (Seu código da simulação) ...
        val lista = mutableListOf<Reserva>()
        val baseCalendar = Calendar.getInstance()

        if (status == StatusReserva.ATIVA) {
            // Reserva Ativa (Daqui a 3 dias)
            val startTimeAtiva = baseCalendar.clone() as Calendar
            startTimeAtiva.add(Calendar.DAY_OF_YEAR, 3)
            startTimeAtiva.set(Calendar.HOUR_OF_DAY, 16)
            startTimeAtiva.set(Calendar.MINUTE, 0)

            val endTimeAtiva = startTimeAtiva.clone() as Calendar
            endTimeAtiva.add(Calendar.HOUR_OF_DAY, 2)

            // Adicionando mais de uma ativa para teste
            lista.add(Reserva("A1", "06", startTimeAtiva, endTimeAtiva, StatusReserva.ATIVA))

            val startTimeAtiva2 = baseCalendar.clone() as Calendar
            startTimeAtiva2.add(Calendar.DAY_OF_YEAR, 5)
            startTimeAtiva2.set(Calendar.HOUR_OF_DAY, 10)
            startTimeAtiva2.set(Calendar.MINUTE, 0)

            val endTimeAtiva2 = startTimeAtiva2.clone() as Calendar
            endTimeAtiva2.add(Calendar.HOUR_OF_DAY, 3)

            lista.add(Reserva("A2", "12", startTimeAtiva2, endTimeAtiva2, StatusReserva.ATIVA))

        } else if (status == StatusReserva.CONCLUIDA) {
            // Reserva Histórica (Há 3 dias)
            val startTimeConcluida = baseCalendar.clone() as Calendar
            startTimeConcluida.add(Calendar.DAY_OF_YEAR, -3)
            startTimeConcluida.set(Calendar.HOUR_OF_DAY, 13)
            startTimeConcluida.set(Calendar.MINUTE, 0)

            val endTimeConcluida = startTimeConcluida.clone() as Calendar
            endTimeConcluida.add(Calendar.HOUR_OF_DAY, 2)

            lista.add(Reserva("H1", "08", startTimeConcluida, endTimeConcluida, StatusReserva.CONCLUIDA))
        }

        return lista
    }
}