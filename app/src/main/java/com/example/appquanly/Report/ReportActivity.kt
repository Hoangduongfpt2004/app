package com.example.appquanly.Report

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.widget.Toolbar
import com.example.appquanly.R
import com.example.appquanly.SalePutIn.BaseDrawerActivity

class ReportActivity : BaseDrawerActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_report
    }

    override fun getNavigationMenuItemId(): Int {
        return R.id.bao_cao // menu item id báo cáo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Hiển thị icon menu hamburger trên toolbar và xử lý mở drawer
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.baseline_menu_24) // icon menu bạn chuẩn bị sẵn
        }

        // Xử lý bấm icon menu để mở Drawer
        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Xử lý nút "Gần đây" hiển thị popup chọn thời gian
        val txtTimeFilter = findViewById<TextView>(R.id.txtTimeFilter)
        txtTimeFilter.setOnClickListener {
            val options = arrayOf("Gần đây", "Tuần này", "Tuần trước", "Tháng trước", "Năm nay", "Năm trước", "Khác")

            val builder = AlertDialog.Builder(this)

// Tạo TextView làm tiêu đề custom
            val title = TextView(this).apply {
                text = "Thời gian"
                setBackgroundColor(resources.getColor(R.color.blue))  // màu nền xanh (đổi màu theo bạn)
                setTextColor(resources.getColor(android.R.color.white))  // text trắng
                textSize = 20f
                setPadding(20, 40, 20, 40)
                gravity = Gravity.CENTER
                typeface = Typeface.DEFAULT_BOLD
            }

// Gán tiêu đề custom
            builder.setCustomTitle(title)

            builder.setItems(options) { dialog, which ->
                val selected = options[which]
                // update UI hay xử lý sau khi chọn
                dialog.dismiss()
            }

            builder.show()

        }
    }
}
