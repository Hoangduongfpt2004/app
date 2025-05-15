package com.example.appquanly.CashRegiter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.appquanly.R
import com.example.appquanly.CashRegiter.contract.CashRegisterContract
import com.example.appquanly.CashRegiter.presenter.CashRegisterPresenter

class CashRegisterActivity : AppCompatActivity(), CashRegisterContract.View {

    private lateinit var edtDisplay: EditText
    private lateinit var btnBackspace: ImageButton
    private lateinit var presenter: CashRegisterContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cash_register) // đổi thành tên layout xml của bạn

        presenter = CashRegisterPresenter(this)

        edtDisplay = findViewById(R.id.edtDisplay)
        btnBackspace = findViewById(R.id.btnBackspace)

        val gridLayout = findViewById<GridLayout>(R.id.gridButtons)

        for (i in 0 until gridLayout.childCount) {
            val view = gridLayout.getChildAt(i)
            if (view is Button) {
                val text = view.text.toString()
                view.setOnClickListener {
                    when (text) {
                        "C" -> presenter.onClearClick()
                        "Xong" -> presenter.onDoneClick()
                        "Giảm" -> presenter.onChangeValue(-1)
                        "Tăng" -> presenter.onChangeValue(1)
                        else -> presenter.onNumberClick(text)
                    }
                }
            }
        }

        btnBackspace.setOnClickListener {
            presenter.onBackspaceClick()
        }
    }

    override fun updateDisplay(value: String) {
        edtDisplay.setText(value)
        edtDisplay.setSelection(value.length)
    }

    override fun finishWithResult(result: String) {
        val intent = Intent()
        intent.putExtra("result", result)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
