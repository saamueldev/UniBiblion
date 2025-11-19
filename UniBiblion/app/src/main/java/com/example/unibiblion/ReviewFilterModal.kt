package com.example.unibiblion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// Interface para comunicar a escolha de volta para a ReviewsActivity
interface ReviewFilterListener {
    fun onFilterApplied(orderBy: FilterOption)
}

// Enum para representar as opções de ordenação
enum class FilterOption {
    RECENT,
    HIGHEST_RATING,
    LOWEST_RATING
}

class ReviewFilterModal : BottomSheetDialogFragment() {

    private var filterListener: ReviewFilterListener? = null

    // Método para anexar o listener (será chamado na ReviewsActivity)
    fun setFilterListener(listener: ReviewFilterListener) {
        this.filterListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.modal_review_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val radioGroup = view.findViewById<RadioGroup>(R.id.radio_group_order)
        val btnApply = view.findViewById<Button>(R.id.btn_apply_filter)

        btnApply.setOnClickListener {
            val selectedOption = when (radioGroup.checkedRadioButtonId) {
                R.id.radio_recent -> FilterOption.RECENT
                R.id.radio_highest_rating -> FilterOption.HIGHEST_RATING
                R.id.radio_lowest_rating -> FilterOption.LOWEST_RATING
                else -> FilterOption.RECENT // Opção de segurança
            }

            // Notifica a Activity com a opção escolhida
            filterListener?.onFilterApplied(selectedOption)
            dismiss() // Fecha o modal
        }
    }
}