package com.example.unibiblion

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.lang.ref.WeakReference

/**
 * Máscara de Data (DD/MM/AAAA) para um EditText.
 * (VERSÃO CORRIGIDA E ROBUSTA)
 */
class DateMask(editText: EditText) : TextWatcher {

    private val editTextRef: WeakReference<EditText> = WeakReference(editText)
    private var isUpdating = false
    private var oldText = ""

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Não é necessário.
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val editText = editTextRef.get() ?: return
        val cleanString = s.toString().replace(Regex("[^\\d]"), "")

        if (isUpdating || cleanString == oldText) {
            return
        }

        isUpdating = true

        val formattedString = buildString {
            // Limita o total de dígitos a 8 (DDMMAAAA)
            val textToFormat = if (cleanString.length > 8) {
                cleanString.substring(0, 8)
            } else {
                cleanString
            }

            var textIndex = 0
            if (textToFormat.length >= 3) {
                // Adiciona DD/
                append(textToFormat.substring(0, 2)).append("/")
                textIndex = 2
            }

            if (textToFormat.length >= 5) {
                // Adiciona MM/
                append(textToFormat.substring(2, 4)).append("/")
                textIndex = 4
            }

            // Adiciona o restante do texto (dia, mês parcial ou ano)
            if (textIndex < textToFormat.length) {
                append(textToFormat.substring(textIndex))
            }
        }

        editText.setText(formattedString)
        editText.setSelection(formattedString.length)

        oldText = cleanString // Armazena o texto limpo, não o formatado
        isUpdating = false
    }

    override fun afterTextChanged(s: Editable?) {
        // Não é necessário.
    }
}
