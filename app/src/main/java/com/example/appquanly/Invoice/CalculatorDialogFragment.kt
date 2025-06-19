package com.example.appquanly.Invoice

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.appquanly.R

class CalculatorDialogFragment(
    private val billAmount: Long,
    private val onResult: (Long) -> Unit
) : DialogFragment() {

    private lateinit var tvAmount: TextView
    private lateinit var gridKeyboard: GridLayout
    private lateinit var gridSuggestion: GridLayout
    private var currentInput: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.activity_dialog_calculator, null)

        tvAmount = view.findViewById(R.id.tvAmount)
        gridKeyboard = view.findViewById(R.id.gridKeyboard)
        gridSuggestion = view.findViewById(R.id.gridSuggestion)

        setupKeyboard()
        setupSuggestionButtons()

        view.findViewById<Button>(R.id.btnAccept).setOnClickListener {
            val amount = currentInput.replace(".", "").toLongOrNull() ?: 0L
            onResult(amount)
            dismiss()
        }

        builder.setView(view)
        return builder.create()
    }

    private fun setupKeyboard() {
        val buttons = listOf("7", "8", "9", "⌫", "4", "5", "6", "C", "1", "2", "3", ",", "0", "000", "ĐỒNG Ý")

        for (label in buttons) {
            val btn = Button(requireContext()).apply {
                text = label
                textSize = 18f
                setOnClickListener { handleKey(label) }

                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(8, 8, 8, 8)
                }

                if (label == "ĐỒNG Ý") {
                    setBackgroundColor(Color.parseColor("#2196F3"))
                    setTextColor(Color.WHITE)
                } else {
                    background = ContextCompat.getDrawable(requireContext(),
                        R.drawable.rounded_button_gray
                    )
                }
            }
            gridKeyboard.addView(btn)
        }
    }


    private fun handleKey(label: String) {
        when (label) {
            "C" -> {
                currentInput = ""
            }
            "⌫" -> {
                if (currentInput.isNotEmpty()) {
                    currentInput = currentInput.dropLast(1)
                }
            }
            "ĐỒNG Ý" -> {
                val amount = currentInput.replace(".", "").toLongOrNull() ?: 0L
                onResult(amount)
                dismiss()
            }
            else -> {
                currentInput += label
            }
        }

        // Hiển thị lại số đã nhập có dấu chấm
        tvAmount.text = formatAmount(currentInput)
    }

    private fun formatAmount(raw: String): String {
        val num = raw.replace(".", "").toLongOrNull() ?: 0L
        return String.format("%,d", num).replace(",", ".")
    }

    private fun setupSuggestionButtons() {
        val suggestions = listOf(billAmount, billAmount + 1000, billAmount + 4000, billAmount + 14000)

        for (suggest in suggestions) {
            val btn = Button(requireContext()).apply {
                text = String.format("%,d", suggest).replace(",", ".")
                setBackgroundColor(Color.parseColor("#4CAF50"))
                setTextColor(Color.WHITE)
                textSize = 16f

                // Quan trọng: layoutParams set weight = 1f để chia đều
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(4, 4, 4, 4) // margin mỏng thôi để khít nhau
                }

                setOnClickListener {
                    currentInput = suggest.toString()
                    tvAmount.text = formatAmount(currentInput)
                }
            }
            gridSuggestion.addView(btn)
        }
    }

}
