package com.example.appquanly.CashRegister.presenter

import com.example.appquanly.CashRegister.contract.CashRegisterContract

class CashRegisterPresenter(private val view: CashRegisterContract.View) : CashRegisterContract.Presenter {

    private var currentValue: String = "0"

    override fun onNumberClick(number: String) {
        currentValue = if (currentValue == "0") number else currentValue + number
        view.updateDisplay(currentValue)
    }

    override fun onClearClick() {
        currentValue = "0"
        view.updateDisplay(currentValue)
    }

    override fun onBackspaceClick() {
        currentValue = currentValue.dropLast(1)
        if (currentValue.isEmpty()) currentValue = "0"
        view.updateDisplay(currentValue)
    }

    override fun onDoneClick() {
        view.finishWithResult(currentValue)
    }

    override fun onChangeValue(delta: Int) {
        val number = currentValue.toDoubleOrNull() ?: 0.0
        val result = number + delta
        currentValue = if (result % 1 == 0.0) result.toInt().toString() else result.toString()
        view.updateDisplay(currentValue)
    }
}
