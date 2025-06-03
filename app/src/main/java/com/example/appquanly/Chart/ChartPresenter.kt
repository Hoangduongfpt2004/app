package com.example.appquanly.Chart

import android.util.Log
import com.example.appquanly.data.sqlite.Local.SAInvoiceDetailRepository
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.*

class ChartPresenter(
    private val view: ChartContract.View,
    private val repository: SAInvoiceDetailRepository
) : ChartContract.Presenter {

    override fun loadChartData(timeType: String) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()

        val (startTime, endTime, title, dateText) = when (timeType) {
            "today" -> {
                calendar.setStartOfDay()
                val start = calendar.timeInMillis
                val end = start + DAY_IN_MILLIS - 1
                val title = "Thống kê hôm nay"
                val dateText = sdf.format(calendar.time)
                Quadruple(start, end, title, dateText)
            }
            "yesterday" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                calendar.setStartOfDay()
                val start = calendar.timeInMillis
                val end = start + DAY_IN_MILLIS - 1
                val title = "Thống kê hôm qua"
                val dateText = sdf.format(calendar.time)
                Quadruple(start, end, title, dateText)
            }
            "week" -> {
                calendar.firstDayOfWeek = Calendar.MONDAY
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                calendar.setStartOfDay()
                val start = calendar.timeInMillis
                val startText = sdf.format(calendar.time)

                calendar.add(Calendar.DAY_OF_YEAR, 6)
                val endText = sdf.format(calendar.time)
                calendar.setEndOfDay()
                val end = calendar.timeInMillis

                val title = "Thống kê tuần này"
                val dateText = "Từ $startText đến $endText"
                Quadruple(start, end, title, dateText)
            }
            "last_week" -> {
                calendar.firstDayOfWeek = Calendar.MONDAY
                calendar.add(Calendar.WEEK_OF_YEAR, -1)
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                calendar.setStartOfDay()
                val start = calendar.timeInMillis
                val startText = sdf.format(calendar.time)

                calendar.add(Calendar.DAY_OF_YEAR, 6)
                val endText = sdf.format(calendar.time)
                calendar.setEndOfDay()
                val end = calendar.timeInMillis

                val title = "Thống kê tuần trước"
                val dateText = "Từ $startText đến $endText"
                Quadruple(start, end, title, dateText)
            }
            "month" -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.setStartOfDay()
                val start = calendar.timeInMillis
                val startText = sdf.format(calendar.time)

                calendar.add(Calendar.MONTH, 1)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                calendar.setEndOfDay()
                val end = calendar.timeInMillis
                val endText = sdf.format(calendar.time)

                val title = "Thống kê tháng này"
                val dateText = "Từ $startText đến $endText"
                Quadruple(start, end, title, dateText)
            }
            "last_month" -> {
                calendar.add(Calendar.MONTH, -1)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.setStartOfDay()
                val start = calendar.timeInMillis
                val startText = sdf.format(calendar.time)

                calendar.add(Calendar.MONTH, 1)
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                calendar.setEndOfDay()
                val end = calendar.timeInMillis
                val endText = sdf.format(calendar.time)

                val title = "Thống kê tháng trước"
                val dateText = "Từ $startText đến $endText"
                Quadruple(start, end, title, dateText)
            }
            "year" -> {
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                calendar.setStartOfDay()
                val start = calendar.timeInMillis
                val startText = sdf.format(calendar.time)

                calendar.set(Calendar.MONTH, 11)
                calendar.set(Calendar.DAY_OF_MONTH, 31)
                calendar.setEndOfDay()
                val end = calendar.timeInMillis
                val endText = sdf.format(calendar.time)

                val title = "Thống kê năm nay"
                val dateText = "Từ $startText đến $endText"
                Quadruple(start, end, title, dateText)
            }
            "last_year" -> {
                calendar.add(Calendar.YEAR, -1)
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                calendar.setStartOfDay()
                val start = calendar.timeInMillis
                val startText = sdf.format(calendar.time)

                calendar.set(Calendar.MONTH, 11)
                calendar.set(Calendar.DAY_OF_MONTH, 31)
                calendar.setEndOfDay()
                val end = calendar.timeInMillis
                val endText = sdf.format(calendar.time)

                val title = "Thống kê năm trước"
                val dateText = "Từ $startText đến $endText"
                Quadruple(start, end, title, dateText)
            }
            else -> {
                view.showError("Loại thời gian không hợp lệ")
                return
            }
        }

        // Hiển thị tiêu đề và ngày
        view.showTitleAndDate(title, dateText)

        // Lấy tổng doanh thu trong khoảng thời gian
        val totalAmount = repository.getTotalAmountBetween(startTime, endTime)
        Log.d("ChartPresenter", "Total Amount: $totalAmount")
        view.showTotalAmount(totalAmount)

        // Lấy dữ liệu cho biểu đồ và danh sách chi tiết
        val pieData = repository.getPieChartData(startTime, endTime)
        val lineData = repository.getLineChartData(timeType)

        // Tạo danh sách màu sắc cho biểu đồ tròn và danh sách chi tiết
        val baseColors = ColorTemplate.MATERIAL_COLORS.toList()
        val colors = when (timeType) {
            "today", "yesterday" -> List(pieData.size) { index -> baseColors[index % baseColors.size] }
            else -> List(lineData.size) { index -> baseColors[index % baseColors.size] }
        }

        // Hiển thị dữ liệu
        if (timeType == "today" || timeType == "yesterday") {
            if (pieData.isEmpty()) {
                view.showError("Không có dữ liệu để hiển thị biểu đồ")
            } else {
                view.showPieChartData(pieData, colors)
                view.showProductDetails(pieData, colors)
            }
        } else {
            if (lineData.isEmpty()) {
                view.showError("Không có dữ liệu để hiển thị biểu đồ đường")
            } else {
                view.showLineChartData(lineData)
                view.showProductDetails(lineData, colors)
            }
        }
    }

    override fun loadInvoiceDetails(startTime: Long, endTime: Long) {
        val details = repository.getDetailsBetween(startTime, endTime).map {
            Triple(it.InventoryItemName, it.quantity, it.Amount)
        }
        if (details.isEmpty()) {
            view.showError("Không có chi tiết doanh thu cho ngày này")
        } else {
            view.showInvoiceDetails(details)
        }
    }

    override fun onDestroy() {
        // Xử lý giải phóng tài nguyên nếu cần
    }

    // Extension function cho Calendar để đặt thời gian bắt đầu ngày
    private fun Calendar.setStartOfDay() {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    // Extension function cho Calendar để đặt thời gian kết thúc ngày
    private fun Calendar.setEndOfDay() {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }

    companion object {
        private const val DAY_IN_MILLIS = 24 * 60 * 60 * 1000L
    }
}