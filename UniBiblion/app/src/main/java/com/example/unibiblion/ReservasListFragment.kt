package com.example.unibiblion

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

// IMPORTS DO FIREBASE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

// IMPORTS PARA A LÓGICA DE TEMPO
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ReservasListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReservasAdapter
    // Usamos 'var' para poder reatribuir a lista quando os dados do Firebase chegarem
    private var reservas = mutableListOf<Reserva>()

    private lateinit var statusFilter: StatusReserva

    // VARIÁVEIS DO FIREBASE
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // Formatadores (necessários para a lógica de tempo)
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

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

        // INICIALIZAÇÃO DO FIREBASE
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        arguments?.let {
            statusFilter = it.getSerializable(ARG_STATUS) as StatusReserva
        } ?: run {
            statusFilter = StatusReserva.ATIVA
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reservas_list, container, false)
        recyclerView = view.findViewById(R.id.recycler_reservas)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // O Adapter precisa de uma lista mutável para refletir as mudanças (como cancelamento)
        adapter = ReservasAdapter(reservas) { reserva ->
            mostrarDialogoCancelamento(reserva)
        }
        recyclerView.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        carregarReservasDoFirebase()
    }


    /**
     * Busca as reservas do usuário no Firestore, filtrando por status e ordenando.
     */
    private fun carregarReservasDoFirebase() {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            Toast.makeText(context, "Erro: Usuário não autenticado. O login falhou na Activity.", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("reservas")
            .whereEqualTo("usuarioId", userId)
            .whereEqualTo("status", statusFilter.name)
            .orderBy("dataReserva", Query.Direction.ASCENDING)
            .orderBy("horaInicio", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->

                val reservasEncontradas = result.toObjects(Reserva::class.java).toMutableList()

                // 🎯 INJEÇÃO DA LÓGICA DE CONCLUSÃO AUTOMÁTICA
                if (statusFilter == StatusReserva.ATIVA) {
                    // A lista 'reservasEncontradas' é modificada aqui (remoção dos concluídos)
                    checarEAtualizarReservasVencidas(reservasEncontradas)
                }

                // Limpa e adiciona a lista final (já limpa, se for o caso)
                reservas.clear()
                reservas.addAll(reservasEncontradas)

                adapter.notifyDataSetChanged() // Atualiza a RecyclerView
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Erro ao carregar reservas: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }

    // ==========================================================
    // LÓGICA DE CONCLUSÃO AUTOMÁTICA
    // ==========================================================

    /**
     * Checa se as reservas ATIVAS já deveriam ter terminado e, em caso positivo,
     * as move para o status CONCLUIDA no Firebase e as remove da lista UI ATIVA.
     */
    private fun checarEAtualizarReservasVencidas(listaReservasAtivas: MutableList<Reserva>) {

        val agora = Calendar.getInstance()
        val reservasParaRemoverDaUI = mutableListOf<Reserva>()

        for (reserva in listaReservasAtivas) {

            // REVERTIDO: Lendo os campos de data e hora FIM diretamente, sem .replace("\"", "")
            val dataStr = reserva.dataReserva
            val horaFimStr = reserva.horaFim
            val reservaId = reserva.id

            if (dataStr == null || horaFimStr == null || reservaId == null) continue

            try {
                // Monta o Calendar de TÉRMINO da reserva
                val dataFim: Date = dateFormat.parse(dataStr) ?: continue
                val horaFim: Date = timeFormat.parse(horaFimStr) ?: continue

                val calendarFim = Calendar.getInstance()
                calendarFim.time = dataFim

                // Transfere hora/minuto da horaFim para o CalendarFim
                @Suppress("DEPRECATION")
                calendarFim.set(Calendar.HOUR_OF_DAY, horaFim.hours)
                @Suppress("DEPRECATION")
                calendarFim.set(Calendar.MINUTE, horaFim.minutes)
                calendarFim.set(Calendar.SECOND, 0)
                calendarFim.set(Calendar.MILLISECOND, 0)

                // SE: A hora de término da reserva é ANTES do momento ATUAL
                if (calendarFim.before(agora)) {

                    // 1. Marca para remover da lista ATIVA no UI
                    reservasParaRemoverDaUI.add(reserva)

                    // 2. ATUALIZA O STATUS no Firestore (ATIVA -> CONCLUIDA)
                    db.collection("reservas")
                        .document(reservaId)
                        .update("status", StatusReserva.CONCLUIDA.name)
                }
            } catch (e: Exception) {
                Log.e("ReservasVencidas", "Erro ao processar reserva ID $reservaId: ${e.localizedMessage}")
                continue
            }
        }

        // 3. ATUALIZAÇÃO DO UI: Remove as concluídas da lista ATIVA
        listaReservasAtivas.removeAll(reservasParaRemoverDaUI)
    }

    /**
     * Exibe um AlertDialog para confirmar o cancelamento da reserva.
     */
    private fun mostrarDialogoCancelamento(reserva: Reserva) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirmar Cancelamento")
            // REVERTIDO: Lendo cabineNumero diretamente
            .setMessage("Tem certeza que deseja cancelar sua reserva da Cabine ${reserva.cabineNumero}?")
            .setPositiveButton("Sim, Cancelar") { dialog, _ ->
                cancelarReserva(reserva)
                dialog.dismiss()
            }
            .setNegativeButton("Manter Reserva") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Atualiza o status da reserva no Firestore e remove da lista (se for ATIVA).
     */
    private fun cancelarReserva(reserva: Reserva) {
        val reservaId = reserva.id

        if (reservaId == null) {
            Toast.makeText(context, "Erro: ID da reserva inválido.", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Atualizar o status no Firestore
        db.collection("reservas").document(reservaId)
            .update("status", StatusReserva.CANCELADA.name) // Define o novo status como CANCELADA
            .addOnSuccessListener {

                // 2. SUCESSO na remoção do DB: Atualiza a UI (Remove da lista ATIVA)
                val index = reservas.indexOfFirst { it.id == reservaId }
                if (index != -1) {
                    reservas.removeAt(index)
                    adapter.notifyItemRemoved(index)
                }

                // REVERTIDO: Lendo cabineNumero diretamente
                Toast.makeText(context, "Reserva ${reserva.cabineNumero} cancelada com sucesso!", Toast.LENGTH_LONG).show()

            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Erro ao cancelar: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }
}