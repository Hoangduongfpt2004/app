// ThucDonPresenter.kt
package com.example.appquanly.ThucDon

class ThucDonPresenter(
    private val view: ThucDonContract.View,
    private val repository: InventoryItemRepository
) : ThucDonContract.Presenter {

    override fun loadThucDon() {
        try {
            val items = repository.getAllInventoryItems()
            view.showThucDon(items)
        } catch (e: Exception) {
            view.showError("Lỗi khi tải thực đơn: ${e.message}")
        }
    }
}