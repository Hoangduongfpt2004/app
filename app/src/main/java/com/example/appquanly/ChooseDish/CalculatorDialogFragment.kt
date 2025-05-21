package com.example.appquanly.CashRegister

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.appquanly.R

class CalculatorDialogFragment(
    private val onResult: (String) -> Unit
) : DialogFragment() {

    private lateinit var edtDisplay: EditText
    private lateinit var btnBackspace: ImageButton
    private lateinit var btnDone: Button
    private lateinit var gridButtons: GridLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_cash_register, container, false)

        edtDisplay = view.findViewById(R.id.edtDisplay)
        btnBackspace = view.findViewById(R.id.btnBackspace)
        btnDone = view.findViewById(R.id.btnDone)
        gridButtons = view.findViewById(R.id.gridButtons)

        edtDisplay.setText("0")

        btnBackspace.setOnClickListener {
            val text = edtDisplay.text.toString()
            if (text.isNotEmpty()) {
                edtDisplay.setText(text.dropLast(1).ifEmpty { "0" })
                edtDisplay.setSelection(edtDisplay.text.length)
            }
        }

        btnDone.setOnClickListener {
            onResult(edtDisplay.text.toString())
            dismiss()
        }

        // Gán sự kiện cho các nút còn lại trong GridLayout
        for (i in 0 until gridButtons.childCount) {
            val child = gridButtons.getChildAt(i)
            if (child is Button && child.id != R.id.btnDone) {
                child.setOnClickListener {
                    onButtonClick(child.text.toString())
                }
            }
        }

        // Không cho bàn phím hệ thống hiện lên
        edtDisplay.showSoftInputOnFocus = false

        return view
    }

    private fun onButtonClick(value: String) {
        var currentText = edtDisplay.text.toString()

        when (value) {
            "C" -> {
                edtDisplay.setText("0")
            }
            "Giảm" -> {
                // Ví dụ giảm 1 đơn vị nếu là số nguyên
                val num = currentText.toIntOrNull() ?: 0
                edtDisplay.setText((num - 1).coerceAtLeast(0).toString())
            }
            "Tăng" -> {
                val num = currentText.toIntOrNull() ?: 0
                edtDisplay.setText((num + 1).toString())
            }
            else -> {
                // Các nút số 0-9
                if (currentText == "0") currentText = ""
                edtDisplay.setText(currentText + value)
            }
        }
        // Đặt con trỏ về cuối
        edtDisplay.setSelection(edtDisplay.text.length)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
