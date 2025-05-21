package com.example.appquanly.Sychronize

interface Sync_DataContract {
    interface View {
        fun navigateToSaleScreen()
        fun showToast(message: String)
    }

    interface Presenter {
        fun onSyncClicked(code: String)
    }
}
