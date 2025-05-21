package com.example.appquanly.Setup.contract

interface Set_upContract {
    interface View {
        fun showRestoreDataDialog()
        fun showRestoreSuccess()
    }

    interface Presenter {
        fun onRestoreDataClicked()
    }
}
