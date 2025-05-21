package com.example.appquanly.AddProduct

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appquanly.R

// Them_San_PhamActivity.kt
class Add_ProductActivity : AppCompatActivity(), Add_ProductView {

    private lateinit var presenter: Add_ProductPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        presenter = Add_ProductPresenter(this)

        // Ánh xạ các view
        val edtNewUnitName: EditText = findViewById(R.id.edtNewUnitName)
        val btnCancelAdd: Button = findViewById(R.id.btnCancelAdd)
        val btnSaveAdd: Button = findViewById(R.id.btnSaveAdd)
        val btnCloseAdd: ImageView = findViewById(R.id.btnCloseAdd)

        // Xử lý sự kiện nút Lưu
        btnSaveAdd.setOnClickListener {
            val unitName = edtNewUnitName.text.toString()
            presenter.onSaveButtonClick(unitName)
        }

        // Xử lý sự kiện nút Hủy
        btnCancelAdd.setOnClickListener {
            presenter.onCancelButtonClick()
        }

        // Xử lý sự kiện đóng dialog
        btnCloseAdd.setOnClickListener {
            presenter.onCancelButtonClick()
        }
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        finish() // Đóng Activity sau khi lưu thành công
    }

    override fun dismissDialog() {
        finish() // Đóng Activity
    }
}
