package com.example.appquanly.SelectDishActivity

interface SelectDishContract {
    interface View {
        fun showDishList(dishList: List<String>)  // Hiển thị danh sách món ăn
        fun onDishSelected(dish: String)  // Xử lý khi món ăn được chọn
    }

    interface Presenter {
        fun loadDishList()  // Tải danh sách món ăn
        fun onDishClicked(dish: String)  // Xử lý khi món ăn được chọn
    }
}
