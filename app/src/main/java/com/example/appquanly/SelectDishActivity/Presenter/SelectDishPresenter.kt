package com.example.appquanly.banhang.presenter

import com.example.appquanly.banhang.contract.SelectDishContract

class SelectDishPresenter(private val view: SelectDishContract.View) : SelectDishContract.Presenter {

    override fun loadDishList() {
        // Logic để tải danh sách món ăn từ Model (hoặc API, database, ...)
        val dishList = listOf("Món 1", "Món 2", "Món 3")  // Ví dụ danh sách món ăn

        // Gọi View để hiển thị danh sách
        view.showDishList(dishList)
    }

    override fun onDishClicked(dish: String) {
        // Logic xử lý khi món ăn được chọn
        view.onDishSelected(dish)
    }
}
