package com.example.appquanly.SelectDishActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.appquanly.R
import com.example.appquanly.ThucDon.InventoryItem
import com.example.appquanly.ThucDon.InventoryItemRepository

class SelectDishActivity : AppCompatActivity(), SelectDishContract.View {

    private lateinit var presenter: SelectDishContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_dish)

        // Tạo repository
        val repository = InventoryItemRepository(this)

        // Truyền repository khi khởi tạo presenter
        presenter = SelectDishPresenter(this, repository)

        // Gọi presenter để tải dữ liệu
        presenter.loadDishList()
    }


    // Phương thức để hiển thị danh sách món ăn
    override fun showDishList(dishList: List<InventoryItem>) {


    }


    // Phương thức xử lý sự kiện khi chọn món
    override fun onDishSelected(dish: String) {
        // Thực hiện hành động khi món ăn được chọn (ví dụ: mở chi tiết món ăn)
    }
}
