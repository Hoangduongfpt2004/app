package com.example.appquanly.MenuCategory

import com.example.appquanly.data.sqlite.Local.InventoryItemRepository

class Menu_CategoryPresenter(
    private val view: Menu_CategoryContract.View,
    private val repository: InventoryItemRepository
) : Menu_CategoryContract.Presenter {

    override fun loadThucDon() {
        try {
            val items = repository.getAllInventoryItems()
            view.showThucDon(items)
        } catch (e: Exception) {
            view.showError("Lỗi khi tải thực đơn: ${e.message}")
        }
    }
}
