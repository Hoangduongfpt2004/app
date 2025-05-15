package com.example.appquanly.SelectDishActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.appquanly.R

class SelectDishActivity : AppCompatActivity(), SelectDishContract.View {

    private lateinit var presenter: SelectDishContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_dish)

        // Khởi tạo Presenter
        presenter = SelectDishPresenter(this)

        // Gọi Presenter để chuẩn bị dữ liệu
        presenter.loadDishList()
    }

    // Phương thức để hiển thị danh sách món ăn
    override fun showDishList(dishList: List<String>) {
        // Cập nhật giao diện với dữ liệu món ăn
        // Ví dụ: sử dụng RecyclerView hoặc ListView để hiển thị
    }

    // Phương thức xử lý sự kiện khi chọn món
    override fun onDishSelected(dish: String) {
        // Thực hiện hành động khi món ăn được chọn (ví dụ: mở chi tiết món ăn)
    }
}
