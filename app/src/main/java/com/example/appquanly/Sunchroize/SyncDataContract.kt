package com.example.appquanly.Sunchroize

interface SyncDataContract {
    interface View {
        fun navigateToSaleScreen()
        fun showToast(message: String)
    }

    interface Presenter {
        fun onSyncClicked(code: String)
    }
}
