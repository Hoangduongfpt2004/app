// ThucDonContract.kt
package com.example.appquanly.ThucDon

interface ThucDonContract {
    interface View {
        fun showThucDon(items: List<InventoryItem>)
        fun showError(message: String)
    }

    interface Presenter {
        fun loadThucDon()
    }
}