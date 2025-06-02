package com.example.appquanly.Chart

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
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
import java.text.NumberFormat
import java.util.*

class ChartActivity : AppCompatActivity(), ChartContract.View {

    private lateinit var tvTitle: TextView
    private lateinit var tvDate: TextView
    private lateinit var pieChart: PieChart
    private lateinit var lineChart: LineChart
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductDetailAdapter
    private lateinit var presenter: ChartContract.Presenter
    private var timeType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        // Ánh xạ view
        tvTitle = findViewById(R.id.tvTitle)
        tvDate = findViewById(R.id.tvDate)
        pieChart = findViewById(R.id.pieChart)
        lineChart = findViewById(R.id.lineChart)
        recyclerView = findViewById(R.id.recyclerViewDetail)

        timeType = intent.getStringExtra("TIME_TYPE")

        presenter = ChartPresenter(this, SAInvoiceDetailRepository(this))

        setupRecyclerView()
        setupPieChart()
        setupLineChart()

        timeType?.let {
            presenter.loadChartData(it)
        } ?: showError("Không nhận được loại thời gian")
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ProductDetailAdapter(emptyList(), timeType ?: "week", emptyList())
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

            xAxis.apply {
                granularity = 1f
                setDrawGridLines(false)
                position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return when (timeType) {
                            "week", "last_week" -> {
                                val days = arrayOf("T2", "T3", "T4", "T5", "T6", "T7")
                                val index = value.toInt()
                                if (index in days.indices) days[index] else ""
                            }
                            "month", "last_month" -> value.toInt().toString()
                            "year", "last_year" -> "Th ${value.toInt() + 1}"
                            else -> ""
                        }
                    }
                }
            }

            axisLeft.apply {
                axisMinimum = 0f
                setDrawGridLines(true)
            }
        }
    }

    override fun showPieChartData(data: List<Pair<String, Float>>, colors: List<Int>) {
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
        pieChart.visibility = View.GONE
        lineChart.visibility = View.VISIBLE

        if (data.size < 2) {
            Log.w("ChartActivity", "Không đủ dữ liệu để vẽ đường, số điểm: ${data.size}")
            showError("Cần ít nhất 2 điểm dữ liệu để vẽ biểu đồ đường")
            lineChart.data = null
            lineChart.invalidate()
            return
        }

        Log.d("ChartActivity", "LineChart data: $data")
        val entries = data.mapIndexed { index, pair ->
            Entry(index.toFloat(), pair.second)
        }

        val dataSet = LineDataSet(entries, "Doanh thu").apply {
            color = ColorTemplate.MATERIAL_COLORS[0]
            valueTextSize = 10f
            setDrawFilled(true)
            fillAlpha = 50
            fillColor = ColorTemplate.MATERIAL_COLORS[0]
            setDrawCircles(false) // Tắt chấm
            setDrawCircleHole(false) // Không vẽ lỗ trong chấm
            lineWidth = 3f // Tăng độ dày đường
            mode = LineDataSet.Mode.CUBIC_BEZIER // Đường cong mượt
            setDrawValues(true) // Hiển thị giá trị trên đường
        }

        val lineData = LineData(dataSet)
        lineChart.data = lineData
        lineChart.invalidate()
    }

    override fun showProductDetails(details: List<Pair<String, Float>>, colors: List<Int>) {
        adapter.updateData(details, colors)
    }

    override fun showTitleAndDate(title: String, date: String) {
        tvTitle.text = title
        tvDate.text = date
    }

    override fun showTotalAmount(amount: Double) {
        val formatted = String.format("Tổng doanh thu\n%,.0fđ", amount)
        pieChart.centerText = formatted
        pieChart.setCenterTextSize(14f)
        pieChart.setCenterTextColor(android.R.color.black)
        pieChart.invalidate()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}