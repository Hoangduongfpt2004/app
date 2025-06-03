package com.example.appquanly.Chart

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanly.R
import com.example.appquanly.data.sqlite.Local.SAInvoiceDetailRepository
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.appbar.MaterialToolbar
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ChartActivity : AppCompatActivity(), ChartContract.View {

    private lateinit var tvTitle: TextView
    private lateinit var tvDate: TextView
    private lateinit var pieChart: PieChart
    private lateinit var lineChart: LineChart
    private lateinit var recyclerView: RecyclerView
    private lateinit var txtTimeFilter: TextView
    private lateinit var adapter: ProductDetailAdapter
    private lateinit var presenter: ChartContract.Presenter
    private var timeType: String? = null

    // View cho giao diện "Không có dữ liệu"
    private lateinit var mainContentLayout: View
    private lateinit var noRevenueLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_back)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Ánh xạ view
        mainContentLayout = findViewById(R.id.mainContentLayout)
        noRevenueLayout = findViewById(R.id.noRevenueLayout)
        tvTitle = findViewById(R.id.tvTitle)
        tvDate = findViewById(R.id.tvDate)
        pieChart = findViewById(R.id.pieChart)
        lineChart = findViewById(R.id.lineChart)
        recyclerView = findViewById(R.id.recyclerViewDetail)
        txtTimeFilter = findViewById(R.id.txtTimeFilter)

        // Ẩn giao diện "Không có dữ liệu" ban đầu
        noRevenueLayout.visibility = View.GONE

        timeType = intent.getStringExtra("TIME_TYPE") ?: "today"

        presenter = ChartPresenter(this, SAInvoiceDetailRepository(this))

        setupRecyclerView()
        setupPieChart()
        setupLineChart()

        txtTimeFilter.text = getTimeFilterLabel(timeType ?: "today")
        txtTimeFilter.setOnClickListener {
            showTimeFilterPopup()
        }

        // Cập nhật ngày hiện tại
        tvDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        presenter.loadChartData(timeType ?: "today")
    }

    private fun getTimeFilterLabel(timeType: String): String {
        return when (timeType) {
            "today" -> "Gần đây"
            "yesterday" -> "Hôm qua"
            "week" -> "Tuần này"
            "last_week" -> "Tuần trước"
            "month" -> "Tháng này"
            "last_month" -> "Tháng trước"
            "year" -> "Năm nay"
            "last_year" -> "Năm trước"
            "other" -> "Khác"
            else -> "Gần đây"
        }
    }

    private fun showTimeFilterPopup() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_time_filter, null)
        val alertDialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val itemNearby = dialogView.findViewById<TextView>(R.id.item_nearby)
        val itemYesterday = dialogView.findViewById<TextView>(R.id.item_yesterday)
        val itemThisWeek = dialogView.findViewById<TextView>(R.id.item_this_week)
        val itemThisMonth = dialogView.findViewById<TextView>(R.id.item_this_month)
        val itemThisYear = dialogView.findViewById<TextView>(R.id.item_this_year)
        val itemOther = dialogView.findViewById<TextView>(R.id.item_other)
        val btnClose = dialogView.findViewById<Button>(R.id.btn_close)

        val currentLabel = getTimeFilterLabel(timeType ?: "today")
        itemNearby.setCompoundDrawablesWithIntrinsicBounds(0, 0, if (currentLabel == "Gần đây") R.drawable.ic_check_filter else 0, 0)
        itemYesterday.setCompoundDrawablesWithIntrinsicBounds(0, 0, if (currentLabel == "Hôm qua") R.drawable.ic_check_filter else 0, 0)
        itemThisWeek.setCompoundDrawablesWithIntrinsicBounds(0, 0, if (currentLabel == "Tuần này") R.drawable.ic_check_filter else 0, 0)
        itemThisMonth.setCompoundDrawablesWithIntrinsicBounds(0, 0, if (currentLabel == "Tháng này") R.drawable.ic_check_filter else 0, 0)
        itemThisYear.setCompoundDrawablesWithIntrinsicBounds(0, 0, if (currentLabel == "Năm nay") R.drawable.ic_check_filter else 0, 0)
        itemOther.setCompoundDrawablesWithIntrinsicBounds(0, 0, if (currentLabel == "Khác") R.drawable.ic_check_filter else 0, 0)

        val onItemClick = { newTimeType: String, label: String ->
            timeType = newTimeType
            txtTimeFilter.text = label
            alertDialog.dismiss()
            presenter.loadChartData(newTimeType)
        }

        itemNearby.setOnClickListener { onItemClick("today", "Gần đây") }
        itemYesterday.setOnClickListener { onItemClick("yesterday", "Hôm qua") }
        itemThisWeek.setOnClickListener { onItemClick("week", "Tuần này") }
        itemThisMonth.setOnClickListener { onItemClick("month", "Tháng này") }
        itemThisYear.setOnClickListener { onItemClick("year", "Năm nay") }
        itemOther.setOnClickListener { onItemClick("other", "Khác") }

        btnClose.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ProductDetailAdapter(
            emptyList(),
            timeType ?: "week",
            emptyList()
        ) { period, timeType ->
            handleItemClick(period, timeType)
        }
        recyclerView.adapter = adapter
    }

    private fun setupPieChart() {
        pieChart.apply {
            setUsePercentValues(true)
            setDrawHoleEnabled(true)
            holeRadius = 45f
            transparentCircleRadius = 50f
            setHoleColor(android.R.color.transparent)
            description.isEnabled = false
            legend.isEnabled = false
            setDrawEntryLabels(false)
            setExtraOffsets(30f, 10f, 30f, 10f)
            setDrawSliceText(false)
            setRotationEnabled(false)
        }
    }

    private fun setupLineChart() {
        lineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            setPinchZoom(true)
            legend.isEnabled = true
            axisRight.isEnabled = false
            setDrawGridBackground(false)
            setDrawBorders(false)

            xAxis.apply {
                granularity = 1f
                setDrawGridLines(false)
                position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
            }

            axisLeft.apply {
                axisMinimum = 0f
                setDrawGridLines(true)
            }
        }
    }

    override fun showPieChartData(data: List<Pair<String, Float>>, colors: List<Int>) {
        if (data.isEmpty()) {
            showNoRevenueLayout()
            return
        }

        hideNoRevenueLayout()

        pieChart.visibility = View.VISIBLE
        lineChart.visibility = View.GONE

        val total = data.sumOf { it.second.toDouble() }
        val entries = data.map { PieEntry(it.second, it.first) }

        val dataSet = PieDataSet(entries, "").apply {
            this.colors = colors
            valueTextSize = 12f
            sliceSpace = 2f
            selectionShift = 5f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return String.format(Locale("vi", "VN"), "%.2f %%", value)
                }
            }
            yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            valueLinePart1Length = 0.3f
            valueLinePart2Length = 0.4f
            valueLineColor = Color.BLACK
        }

        val pieData = PieData(dataSet)
        pieChart.data = pieData

        val numberFormat = NumberFormat.getNumberInstance(Locale("vi", "VN"))
        val formattedTotal = numberFormat.format(total)
        pieChart.centerText = "Tổng doanh thu\n$formattedTotal đ"
        pieChart.setCenterTextSize(16f)
        pieChart.setCenterTextColor(Color.BLACK)

        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.setHoleRadius(70f)
        pieChart.setTransparentCircleRadius(75f)
        pieChart.setDrawEntryLabels(false)
        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setHoleColor(Color.TRANSPARENT)

        pieChart.invalidate()
    }

    override fun showLineChartData(data: List<Pair<String, Float>>) {
        if (data.isEmpty()) {
            showNoRevenueLayout()
            return
        }

        hideNoRevenueLayout()

        pieChart.visibility = View.GONE
        lineChart.visibility = View.VISIBLE

        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        val filteredData = if (timeType == "year" || timeType == "last_year") {
            data.filter { pair ->
                val month = pair.first.replace("Tháng ", "").toIntOrNull() ?: 1
                month <= currentMonth || pair.second > 0
            }
        } else {
            data
        }

        if (filteredData.isEmpty()) {
            showNoRevenueLayout()
            return
        }

        val entries = filteredData.mapIndexed { index, pair ->
            Entry(index.toFloat(), pair.second)
        }

        val dataSet = LineDataSet(entries, "Doanh thu").apply {
            color = ColorTemplate.MATERIAL_COLORS[0]
            valueTextSize = 10f
            setDrawFilled(false)
            setDrawCircles(true)
            circleRadius = 4f
            circleColors = listOf(Color.GREEN)
            setDrawCircleHole(false)
            lineWidth = 2f
            mode = LineDataSet.Mode.LINEAR
            setDrawValues(true)
        }

        val lineData = LineData(dataSet)
        lineChart.data = lineData

        lineChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()

                if (index !in filteredData.indices) return ""

                val dateParts = filteredData[index].first.split("-")
                if (dateParts.size < 3) return ""

                return when (timeType) {
                    "week", "last_week" -> {
                        val dayMap = mapOf(
                            "01" to "Thứ 2", "02" to "Thứ 2", "03" to "Thứ 3",
                            "04" to "Thứ 4", "05" to "Thứ 5", "06" to "Thứ 6",
                            "07" to "Thứ 7"
                        )
                        val day = dateParts[2].padStart(2, '0').substring(0, 2)
                        dayMap[day] ?: "?"
                    }

                    "month", "last_month" -> {
                        val day = dateParts[2].padStart(2, '0')
                        "Ngày $day"
                    }

                    "year", "last_year" -> filteredData[index].first

                    else -> ""
                }
            }
        }


        lineChart.invalidate()
    }

    override fun showProductDetails(details: List<Pair<String, Float>>, colors: List<Int>) {
        if (details.isEmpty()) {
            showNoRevenueLayout()
            return
        }

        hideNoRevenueLayout()
        adapter.updateData(details, colors)
    }

    override fun showTitleAndDate(title: String, date: String) {
        tvTitle.text = title
        tvDate.text = date
    }

    override fun showTotalAmount(amount: Double) {
        if (amount == 0.0) {
            showNoRevenueLayout()
            return
        }

        hideNoRevenueLayout()
        val formatted = String.format("Tổng doanh thu\n%,.0fđ", amount)
        pieChart.centerText = formatted
        pieChart.setCenterTextSize(14f)
        pieChart.setCenterTextColor(android.R.color.black)
        pieChart.invalidate()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showInvoiceDetails(details: List<Triple<String, Int, Float>>) {
        if (details.isEmpty()) {
            showNoRevenueLayout()
            return
        }

        hideNoRevenueLayout()
        val formattedDetails = details.map { Pair("${it.first} (SL: ${it.second}, Giá: %,.0fđ".format(it.third), it.third) }
        val colors = List(formattedDetails.size) { ColorTemplate.MATERIAL_COLORS[it % ColorTemplate.MATERIAL_COLORS.size] }
        adapter.updateData(formattedDetails, colors)
        tvTitle.text = "Chi tiết doanh thu - ${details.firstOrNull()?.first?.split(" ")?.firstOrNull() ?: "N/A"}"
        tvDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    private fun showNoRevenueLayout() {
        noRevenueLayout.visibility = View.VISIBLE
        mainContentLayout.visibility = View.GONE
    }

    private fun hideNoRevenueLayout() {
        noRevenueLayout.visibility = View.GONE
        mainContentLayout.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    private fun handleItemClick(period: String, timeType: String) {
        val calendar = Calendar.getInstance()
        calendar.time = Date()

        when (timeType) {
            "today" -> {
                calendar.setStartOfDay()
                val startTime = calendar.timeInMillis
                calendar.setEndOfDay()
                val endTime = calendar.timeInMillis
                presenter.loadInvoiceDetails(startTime, endTime)
            }
            "yesterday" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                calendar.setStartOfDay()
                val startTime = calendar.timeInMillis
                calendar.setEndOfDay()
                val endTime = calendar.timeInMillis
                presenter.loadInvoiceDetails(startTime, endTime)
            }
            "week", "last_week" -> {
                val dayOfWeek = when (period) {
                    "Thứ 2" -> Calendar.MONDAY
                    "Thứ 3" -> Calendar.TUESDAY
                    "Thứ 4" -> Calendar.WEDNESDAY
                    "Thứ 5" -> Calendar.THURSDAY
                    "Thứ 6" -> Calendar.FRIDAY
                    "Thứ 7" -> Calendar.SATURDAY
                    else -> Calendar.SUNDAY
                }
                if (timeType == "last_week") calendar.add(Calendar.WEEK_OF_YEAR, -1)
                calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
                val selectedDate = calendar.time
                val isToday = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate) == SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                val newTimeType = if (isToday) "today" else "yesterday"
                val intent = Intent(this, ChartActivity::class.java).apply {
                    putExtra("TIME_TYPE", newTimeType)
                }
                startActivity(intent)
            }
            "month", "last_month" -> {
                val day = period.replace("Ngày ", "").toIntOrNull() ?: 1
                if (timeType == "last_month") calendar.add(Calendar.MONTH, -1)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                val selectedDate = calendar.time
                val isToday = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate) == SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                val newTimeType = if (isToday) "today" else "yesterday"
                val intent = Intent(this, ChartActivity::class.java).apply {
                    putExtra("TIME_TYPE", newTimeType)
                }
                startActivity(intent)
            }
            "year", "last_year" -> {
                val month = period.replace("Tháng ", "").toIntOrNull() ?: 1
                if (timeType == "last_year") calendar.add(Calendar.YEAR, -1)
                calendar.set(Calendar.MONTH, month - 1)
                val isCurrentMonth = calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR) &&
                        calendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)
                val newTimeType = if (isCurrentMonth) "month" else "last_month"
                val intent = Intent(this, ChartActivity::class.java).apply {
                    putExtra("TIME_TYPE", newTimeType)
                }
                startActivity(intent)
            }
        }
    }

    private fun Calendar.setStartOfDay() {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    private fun Calendar.setEndOfDay() {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }
}