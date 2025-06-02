package com.example.appquanly.Report

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.example.appquanly.Chart.ChartActivity
import com.example.appquanly.R
import com.example.appquanly.SalePutIn.BaseDrawerActivity
import kotlinx.coroutines.*

class ReportActivity : BaseDrawerActivity(), ReportContract.View {

    private lateinit var presenter: ReportContract.Presenter

    private lateinit var tvAmountHomNay: TextView
    private lateinit var tvAmountHomQua: TextView
    private lateinit var tvAmountTuanNay: TextView
    private lateinit var tvAmountThangNay: TextView
    private lateinit var tvAmountNamNay: TextView

    private lateinit var btnHomNay: LinearLayout
    private lateinit var btnHomQua: LinearLayout
    private lateinit var btnTuanNay: LinearLayout
    private lateinit var btnThangNay: LinearLayout
    private lateinit var btnNamNay: LinearLayout

    private var updateJob: Job? = null

    override fun getLayoutId(): Int = R.layout.activity_report

    override fun getNavigationMenuItemId(): Int = R.id.bao_cao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = ReportPresenter(this, this)

        tvAmountHomNay = findViewById(R.id.tvAmountHomNay)
        tvAmountHomQua = findViewById(R.id.tvAmountHomQua)
        tvAmountTuanNay = findViewById(R.id.tvAmountTuanNay)
        tvAmountThangNay = findViewById(R.id.tvAmountThangNay)
        tvAmountNamNay = findViewById(R.id.tvAmountNamNay)

        btnHomNay = findViewById(R.id.btnHomNay)
        btnHomQua = findViewById(R.id.btnHomQua)
        btnTuanNay = findViewById(R.id.btnTuanNay)
        btnThangNay = findViewById(R.id.btnThangNay)
        btnNamNay = findViewById(R.id.btnNamNay)

        setupToolbar()
        setupTimeFilterDialog()

        setupClickListeners()

        presenter.loadReportData()
    }

    override fun onResume() {
        super.onResume()
        presenter.loadReportData()
        startAutoUpdate()
    }

    override fun onPause() {
        super.onPause()
        stopAutoUpdate()
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

    private fun setupClickListeners() {
        btnHomNay.setOnClickListener {
            presenter.loadToday()
            navigateToChart("today")
        }
        btnHomQua.setOnClickListener {
            presenter.loadYesterday()
            navigateToChart("yesterday")
        }
        btnTuanNay.setOnClickListener {
            presenter.loadThisWeek()
            navigateToChart("week")
        }
        btnThangNay.setOnClickListener {
            presenter.loadThisMonth()
            navigateToChart("month")
        }
        btnNamNay.setOnClickListener {
            presenter.loadThisYear()
            navigateToChart("year")
        }
    }

    private fun navigateToChart(timeType: String) {
        val intent = Intent(this, ChartActivity::class.java)
        intent.putExtra("TIME_TYPE", timeType)
        startActivity(intent)
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
                when (which) {
                    0 -> navigateToChart("today") // Gần đây -> Hôm nay
                    1 -> navigateToChart("week") // Tuần này
                    2 -> navigateToChart("last_week") // Tuần trước
                    3 -> navigateToChart("last_month") // Tháng trước
                    4 -> navigateToChart("year") // Năm nay
                    5 -> navigateToChart("last_year") // Năm trước
                    6 -> showError("Chức năng chọn thời gian tùy chỉnh chưa được triển khai") // Khác
                }
                dialog.dismiss()
            }
            builder.show()
        }
    }

    private fun startAutoUpdate() {
        updateJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                presenter.loadReportData()
                delay(5000)
            }
        }
    }

    private fun stopAutoUpdate() {
        updateJob?.cancel()
        updateJob = null
    }

    private fun formatCurrency(amount: Double): String {
        return String.format("%,.0f", amount)
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

    override fun onDestroy() {
        stopAutoUpdate()
        presenter.onDestroy()
        super.onDestroy()
    }
}