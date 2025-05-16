package com.example.appquanly.SelectDishActivity

import com.example.appquanly.ThucDon.InventoryItem

interface SelectDishContract {
    interface View {
        fun showDishList(dishList: List<InventoryItem>)  // Hiển thị danh sách món ăn
        fun onDishSelected(dish: String)  // Xử lý khi món ăn được chọn
    }

    interface Presenter {
        fun loadDishList()  // Tải danh sách món ăn
        fun onDishClicked(dish: String)  // Xử lý khi món ăn được chọn
    }
}
