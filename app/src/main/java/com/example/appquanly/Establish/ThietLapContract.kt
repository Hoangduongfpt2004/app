package com.example.appquanly.Establish.contract

interface ThietLapContract {
    interface View {
        fun showRestoreDataDialog()
        fun showRestoreSuccess()
    }

    interface Presenter {
        fun onRestoreDataClicked()
    }
}
