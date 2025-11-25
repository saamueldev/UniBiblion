package com.example.unibiblion

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.lang.ref.WeakReference

/**
 * Um TextWatcher que aplica uma máscara de formato de horário (HH:MM) a um EditText.
 * Limita a entrada a 4 dígitos e insere os dois pontos (:) automaticamente.
 */
class TimeMask(editText: EditText) : TextWatcher {

    private val editTextRef: WeakReference<EditText> = WeakReference(editText)
    private var isUpdating = false
    private var oldText = ""

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        // Não é necessário implementar.
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val editText = editTextRef.get() ?: return
        val cleanString = s.toString().filter { it.isDigit() }

        if (isUpdating || cleanString == oldText) {
            return
        }

        isUpdating = true

        val formattedString: String
        // Limita o total de dígitos a 4 (HHMM)
        if (cleanString.length > 4) {
            formattedString = oldText
        } else if (cleanString.length >= 3) {
            // Formata os minutos: 12:34
            formattedString = "${cleanString.substring(0, 2)}:${cleanString.substring(2)}"
        } else if (cleanString.isNotEmpty()) {
            // Formata a hora: 12
            formattedString = cleanString
        } else {
            formattedString = ""
        }

        // Atualiza o texto e a posição do cursor
        editText.setText(formattedString)
        editText.setSelection(formattedString.length)

        oldText = formattedString
        isUpdating = false
    }

    override fun afterTextChanged(s: Editable) {
        // Não é necessário implementar.
    }
}
