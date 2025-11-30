package com.example.unibiblion

import android.content.Context
import android.os.Environment
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfExporter {

    /**
     * Gera um PDF com os relatórios de livros
     */
    fun gerarPdfLivros(context: Context, livros: List<RelatorioLivro>): File? {
        return try {
            val document = Document()
            val fileName = "Relatorio_Livros_${System.currentTimeMillis()}.pdf"
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )

            PdfWriter.getInstance(document, FileOutputStream(file))
            document.open()

            // Título
            val titulo = Paragraph("RELATÓRIO DE LIVROS", Font(Font.FontFamily.HELVETICA, 18f, Font.BOLD))
            titulo.alignment = Element.ALIGN_CENTER
            document.add(titulo)
            document.add(Paragraph("\n"))

            // Data do relatório
            val dataAtual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR")).format(Date())
            val dataParagrafo = Paragraph("Gerado em: $dataAtual", Font(Font.FontFamily.HELVETICA, 10f))
            dataParagrafo.alignment = Element.ALIGN_RIGHT
            document.add(dataParagrafo)
            document.add(Paragraph("\n"))

            if (livros.isEmpty()) {
                document.add(Paragraph("Nenhum livro encontrado no relatório."))
            } else {
                // Tabela
                val table = PdfPTable(5).apply {
                    widthPercentage = 100f
                    setWidths(floatArrayOf(1.5f, 2f, 2f, 2f, 1.5f))
                }

                // Cabeçalho
                adicionarCellCabecalho(table, "Livro")
                adicionarCellCabecalho(table, "Usuário")
                adicionarCellCabecalho(table, "Data Retirada")
                adicionarCellCabecalho(table, "Data Devolução")
                adicionarCellCabecalho(table, "Status")

                // Dados
                livros.forEach { livro ->
                    table.addCell(criarCell(livro.tituloLivro))
                    table.addCell(criarCell(livro.nomeUsuario))
                    table.addCell(criarCell(livro.dataRetirada))

                    val dataDev = if (livro.devolvido) {
                        formatarTimestamp(livro.dataRealDevolucao)
                    } else {
                        "Pendente"
                    }
                    table.addCell(criarCell(dataDev))

                    val status = if (livro.devolvido) "Devolvido" else "Alugado"
                    table.addCell(criarCell(status))
                }

                document.add(table)
            }

            document.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Gera um PDF com os relatórios de cabines
     */
    fun gerarPdfCabines(context: Context, cabines: List<RelatorioCabine>): File? {
        return try {
            val document = Document()
            val fileName = "Relatorio_Cabines_${System.currentTimeMillis()}.pdf"
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )

            PdfWriter.getInstance(document, FileOutputStream(file))
            document.open()

            // Título
            val titulo = Paragraph("RELATÓRIO DE CABINES", Font(Font.FontFamily.HELVETICA, 18f, Font.BOLD))
            titulo.alignment = Element.ALIGN_CENTER
            document.add(titulo)
            document.add(Paragraph("\n"))

            // Data do relatório
            val dataAtual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR")).format(Date())
            val dataParagrafo = Paragraph("Gerado em: $dataAtual", Font(Font.FontFamily.HELVETICA, 10f))
            dataParagrafo.alignment = Element.ALIGN_RIGHT
            document.add(dataParagrafo)
            document.add(Paragraph("\n"))

            if (cabines.isEmpty()) {
                document.add(Paragraph("Nenhuma cabine encontrada no relatório."))
            } else {
                // Tabela
                val table = PdfPTable(5).apply {
                    widthPercentage = 100f
                    setWidths(floatArrayOf(1f, 2f, 2f, 2f, 1.5f))
                }

                // Cabeçalho
                adicionarCellCabecalho(table, "Cabine")
                adicionarCellCabecalho(table, "Usuário")
                adicionarCellCabecalho(table, "Data")
                adicionarCellCabecalho(table, "Horário")
                adicionarCellCabecalho(table, "Status")

                // Dados
                cabines.forEach { cabine ->
                    table.addCell(criarCell(cabine.cabineNumero))
                    table.addCell(criarCell(cabine.nomeUsuario))
                    table.addCell(criarCell(cabine.dataReserva))
                    table.addCell(criarCell("${cabine.horaInicio} - ${cabine.horaFim}"))
                    table.addCell(criarCell(cabine.status))
                }

                document.add(table)
            }

            document.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Funções auxiliares
    private fun adicionarCellCabecalho(table: PdfPTable, texto: String) {
        val cell = PdfPCell(Phrase(texto, Font(Font.FontFamily.HELVETICA, 11f, Font.BOLD, BaseColor.WHITE)))
        cell.backgroundColor = BaseColor(102, 51, 153) // roxo
        cell.horizontalAlignment = Element.ALIGN_CENTER
        table.addCell(cell)
    }

    private fun criarCell(texto: String): PdfPCell {
        val cell = PdfPCell(Phrase(texto, Font(Font.FontFamily.HELVETICA, 10f)))
        cell.setPadding(8f)
        cell.horizontalAlignment = Element.ALIGN_CENTER
        return cell
    }

    private fun formatarTimestamp(timestamp: com.google.firebase.Timestamp?): String {
        return if (timestamp != null) {
            val date = timestamp.toDate()
            SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(date)
        } else {
            "-"
        }
    }
}