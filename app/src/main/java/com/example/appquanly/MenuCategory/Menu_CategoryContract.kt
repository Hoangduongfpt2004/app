// ThucDonContract.kt
package com.example.appquanly.MenuCategory

import com.example.appquanly.data.sqlite.Entity.InventoryItem

interface Menu_CategoryContract {
    interface View {
        fun showThucDon(items: List<InventoryItem>)
        fun showError(message: String)
    }

    interface Presenter {
        fun loadThucDon()
    }
}