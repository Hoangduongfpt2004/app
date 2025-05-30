package com.example.appquanly.Report

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.example.appquanly.R
import com.example.appquanly.SalePutIn.BaseDrawerActivity


class ReportActivity : BaseDrawerActivity(), ReportContract.View {

    private lateinit var presenter: ReportContract.Presenter

    // TextView hiển thị doanh thu
    private lateinit var tvAmountHomNay: TextView
    private lateinit var tvAmountHomQua: TextView
    private lateinit var tvAmountTuanNay: TextView
    private lateinit var tvAmountThangNay: TextView
    private lateinit var tvAmountNamNay: TextView

    // LinearLayout làm nút bấm cho từng khoảng thời gian
    private lateinit var btnHomNay: LinearLayout
    private lateinit var btnHomQua: LinearLayout
    private lateinit var btnTuanNay: LinearLayout
    private lateinit var btnThangNay: LinearLayout
    private lateinit var btnNamNay: LinearLayout

    override fun getLayoutId(): Int = R.layout.activity_report

    override fun getNavigationMenuItemId(): Int = R.id.bao_cao

    override fun onResume() {
        super.onResume()
        presenter.loadReportData()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = ReportPresenter(this, this)

        // Ánh xạ các TextView
        tvAmountHomNay = findViewById(R.id.tvAmountHomNay)
        tvAmountHomQua = findViewById(R.id.tvAmountHomQua)
        tvAmountTuanNay = findViewById(R.id.tvAmountTuanNay)
        tvAmountThangNay = findViewById(R.id.tvAmountThangNay)
        tvAmountNamNay = findViewById(R.id.tvAmountNamNay)

        // Ánh xạ các nút LinearLayout
        btnHomNay = findViewById(R.id.btnHomNay)
        btnHomQua = findViewById(R.id.btnHomQua)
        btnTuanNay = findViewById(R.id.btnTuanNay)
        btnThangNay = findViewById(R.id.btnThangNay)
        btnNamNay = findViewById(R.id.btnNamNay)

        setupToolbar()
        setupTimeFilterDialog()

        presenter.loadReportData()

        // Sự kiện click cho các nút
        btnHomNay.setOnClickListener {
            presenter.loadToday()
        }

        btnHomQua.setOnClickListener {
            presenter.loadYesterday()
        }

        btnTuanNay.setOnClickListener {
            presenter.loadThisWeek()
        }

        btnThangNay.setOnClickListener {
            presenter.loadThisMonth()
        }

        btnNamNay.setOnClickListener {
            presenter.loadThisYear()
        }

    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_menu_24)

        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun setupTimeFilterDialog() {
        val txtTimeFilter = findViewById<TextView>(R.id.txtTimeFilter)
        txtTimeFilter.setOnClickListener {
            val options = arrayOf("Gần đây", "Tuần này", "Tuần trước", "Tháng trước", "Năm nay", "Năm trước", "Khác")
            val builder = AlertDialog.Builder(this)
            val title = TextView(this).apply {
                text = "Thời gian"
                setBackgroundColor(resources.getColor(R.color.blue))
                setTextColor(resources.getColor(android.R.color.white))
                textSize = 20f
                setPadding(20, 40, 20, 40)
                gravity = Gravity.CENTER
                typeface = Typeface.DEFAULT_BOLD
            }
            builder.setCustomTitle(title)
            builder.setItems(options) { dialog, which ->
                txtTimeFilter.text = options[which]
                dialog.dismiss()
            }
            builder.show()
        }
    }


    override fun showTotalToday(amount: Double) {
        tvAmountHomNay.text = formatCurrency(amount)
    }

    override fun showTotalYesterday(amount: Double) {
        tvAmountHomQua.text = formatCurrency(amount)
    }

    override fun showTotalThisWeek(amount: Double) {
        tvAmountTuanNay.text = formatCurrency(amount)
    }

    override fun showTotalThisMonth(amount: Double) {
        tvAmountThangNay.text = formatCurrency(amount)
    }

    override fun showTotalThisYear(amount: Double) {
        tvAmountNamNay.text = formatCurrency(amount)
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun formatCurrency(amount: Double): String {
        return String.format("%,.0f ", amount)
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}
