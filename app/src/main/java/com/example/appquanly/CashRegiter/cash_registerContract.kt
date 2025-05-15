package com.example.appquanly.CashRegiter.contract

interface CashRegisterContract {
    interface View {
        fun updateDisplay(value: String)
        fun finishWithResult(result: String)
    }

    interface Presenter {
        fun onNumberClick(number: String)
        fun onClearClick()
        fun onBackspaceClick()
        fun onDoneClick()
        fun onChangeValue(delta: Int)
    }
}
