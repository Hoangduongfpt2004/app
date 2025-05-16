package com.example.appquanly.SelectDishActivity

import com.example.appquanly.ThucDon.InventoryItemRepository

class SelectDishPresenter(private val view: SelectDishContract.View, val repository: InventoryItemRepository) : SelectDishContract.Presenter {

    override fun loadDishList() {
        // Logic để tải danh sách món ăn từ Model (hoặc API, database, ...)

        val items = repository.getAllInventoryItems()

        // Gọi View để hiển thị danh sách
        view.showDishList(items)
    }

    override fun onDishClicked(dish: String) {
        // Logic xử lý khi món ăn được chọn
        view.onDishSelected(dish)
    }
}
