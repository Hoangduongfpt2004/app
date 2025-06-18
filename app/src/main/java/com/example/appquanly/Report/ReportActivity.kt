package com.example.appquanly.Report

import android.app.DatePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.example.appquanly.Chart.ChartActivity
import com.example.appquanly.R
import com.example.appquanly.SalePutIn.BaseDrawerActivity
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

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

    private val refreshReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("ReportActivity", "Received refresh broadcast")
            presenter.loadReportData()
        }
    }

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

        // Đăng ký BroadcastReceiver với RECEIVER_NOT_EXPORTED
        val intentFilter = IntentFilter("com.example.appquanly.REFRESH_REPORT")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(refreshReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(refreshReceiver, intentFilter)
        }

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

    override fun onDestroy() {
        super.onDestroy()
        stopAutoUpdate()
        presenter.onDestroy()
        // Hủy đăng ký BroadcastReceiver
        unregisterReceiver(refreshReceiver)
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

    private fun navigateToChart(timeType: String, fromDate: String? = null, toDate: String? = null) {
        val intent = Intent(this, ChartActivity::class.java)
        intent.putExtra("TIME_TYPE", timeType)
        if (fromDate != null && toDate != null) {
            intent.putExtra("FROM_DATE", fromDate)
            intent.putExtra("TO_DATE", toDate)
        }
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
                    0 -> navigateToChart("today")
                    1 -> navigateToChart("week")
                    2 -> navigateToChart("last_week")
                    3 -> navigateToChart("last_month")
                    4 -> navigateToChart("year")
                    5 -> navigateToChart("last_year")
                    6 -> showCustomDatePickerDialog()
                }
                dialog.dismiss()
            }
            builder.show()
        }
    }

    private fun showCustomDatePickerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom_date, null)
        val tvFromDate = dialogView.findViewById<TextView>(R.id.tvFromDate)
        val tvToDate = dialogView.findViewById<TextView>(R.id.tvToDate)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirm)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        tvFromDate.text = dateFormat.format(calendar.time)
        tvToDate.text = dateFormat.format(calendar.time)

        tvFromDate.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                tvFromDate.text = dateFormat.format(calendar.time)
            }, year, month, day).show()
        }

        tvToDate.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                tvToDate.text = dateFormat.format(calendar.time)
            }, year, month, day).show()
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnConfirm.setOnClickListener {
            val fromDate = tvFromDate.text.toString()
            val toDate = tvToDate.text.toString()

            if (fromDate > toDate) {
                Toast.makeText(this, "Từ ngày phải nhỏ hơn hoặc bằng Đến ngày", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            dialog.dismiss()
            navigateToChart("custom", fromDate, toDate)
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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
}