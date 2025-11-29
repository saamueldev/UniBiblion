package com.example.unibiblion

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Classe utilitária para migração de dados antigos.
 * EXECUTAR APENAS UMA VEZ para corrigir documentos sem usuarioId.
 */
object MigracaoUsuarioId {
    
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    /**
     * Atualiza todos os documentos em 'livrosalugados' que não possuem usuarioId,
     * adicionando o ID do usuário atual.
     */
    fun corrigirDocumentosSemUsuarioId(callback: (sucesso: Boolean, mensagem: String) -> Unit) {
        val usuarioAtual = auth.currentUser?.uid
        
        if (usuarioAtual == null) {
            callback(false, "Nenhum usuário logado")
            return
        }
        
        Log.d("Migracao", "=== INICIANDO MIGRAÇÃO ===")
        Log.d("Migracao", "Usuário atual: $usuarioAtual")
        
        db.collection("livrosalugados")
            .get()
            .addOnSuccessListener { snapshot ->
                var total = 0
                var atualizados = 0
                var jaCorretos = 0
                var erros = 0
                
                total = snapshot.documents.size
                Log.d("Migracao", "Total de documentos: $total")
                
                if (snapshot.documents.isEmpty()) {
                    callback(true, "Nenhum documento para migrar")
                    return@addOnSuccessListener
                }
                
                for (doc in snapshot.documents) {
                    val usuarioId = doc.getString("usuarioId")
                    
                    if (usuarioId.isNullOrEmpty()) {
                        // Documento sem usuarioId - precisa ser corrigido
                        Log.d("Migracao", "Corrigindo documento: ${doc.id}")
                        
                        doc.reference.update("usuarioId", usuarioAtual)
                            .addOnSuccessListener {
                                atualizados++
                                Log.d("Migracao", "✓ Documento ${doc.id} atualizado")
                                
                                if (atualizados + jaCorretos + erros == total) {
                                    val msg = "Migração concluída!\nTotal: $total\nAtualizados: $atualizados\nJá corretos: $jaCorretos\nErros: $erros"
                                    Log.d("Migracao", msg)
                                    callback(true, msg)
                                }
                            }
                            .addOnFailureListener { e ->
                                erros++
                                Log.e("Migracao", "✗ Erro ao atualizar ${doc.id}", e)
                                
                                if (atualizados + jaCorretos + erros == total) {
                                    val msg = "Migração concluída com erros!\nTotal: $total\nAtualizados: $atualizados\nJá corretos: $jaCorretos\nErros: $erros"
                                    Log.e("Migracao", msg)
                                    callback(false, msg)
                                }
                            }
                    } else {
                        // Documento já tem usuarioId
                        jaCorretos++
                        Log.d("Migracao", "✓ Documento ${doc.id} já possui usuarioId: $usuarioId")
                        
                        if (atualizados + jaCorretos + erros == total) {
                            val msg = "Migração concluída!\nTotal: $total\nAtualizados: $atualizados\nJá corretos: $jaCorretos\nErros: $erros"
                            Log.d("Migracao", msg)
                            callback(true, msg)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Migracao", "Erro ao buscar documentos", e)
                callback(false, "Erro: ${e.message}")
            }
    }
}
