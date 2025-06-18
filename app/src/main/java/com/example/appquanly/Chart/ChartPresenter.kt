package com.example.appquanly.Chart

import android.graphics.Color
import android.util.Log
import com.example.appquanly.data.sqlite.Local.SAInvoiceDetailRepository
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

        view.showTitleAndDate(title, dateText)

        val totalAmount = repository.getTotalAmountBetween(startTime, endTime)
        Log.d("ChartPresenter", "Total Amount: $totalAmount")
        view.showTotalAmount(totalAmount)

        var pieData = repository.getDetailsBetween(startTime, endTime).map {
            Triple(it.InventoryItemName, it.quantity, it.Amount)
        }
        var lineData = repository.getLineChartData(timeType)

        val customColors = listOf(
            Color.parseColor("#FF5733"),
            Color.parseColor("#33FF57"),
            Color.parseColor("#3357FF"),
            Color.parseColor("#FF33A1"),
            Color.parseColor("#FFD700"),
            Color.parseColor("#4B0082"),
            Color.parseColor("#00CED1")
        )
        val colors: List<Int>
        if (timeType == "today" || timeType == "yesterday") {
            if (pieData.size > 7) {
                val sortedData = pieData.sortedByDescending { it.third }
                val top7 = sortedData.take(7)
                val others = sortedData.drop(7)
                val othersQuantity = others.sumOf { it.second.toLong() }.toInt()
                val othersAmount = others.sumOf { it.third.toDouble() }.toFloat()
                pieData = top7 + Triple("Khác", othersQuantity, othersAmount)
                colors = customColors + Color.GRAY
            } else {
                pieData = pieData.sortedByDescending { it.third }
                colors = customColors.take(pieData.size)
            }
            view.showPieChartData(pieData, colors)
            view.showProductDetails(pieData, colors)
        } else {
            val details = when (timeType) {
                "week", "last_week" -> {
                    val days = mutableListOf<Triple<String, Int, Float>>()
                    val dayMap = listOf("Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7")
                    var tempCalendar = Calendar.getInstance().apply {
                        timeInMillis = startTime
                        firstDayOfWeek = Calendar.MONDAY
                        if (timeType == "last_week") add(Calendar.WEEK_OF_YEAR, -1)
                        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                    }
                    for (i in 0 until 6) {
                        tempCalendar.setStartOfDay()
                        val dayStart = tempCalendar.timeInMillis
                        tempCalendar.setEndOfDay()
                        val dayEnd = tempCalendar.timeInMillis
                        val dayData = repository.getDetailsBetween(dayStart, dayEnd)
                        val totalQuantity = dayData.sumOf { it.quantity.toLong() }.toInt()
                        val totalAmount = dayData.sumOf { it.Amount.toDouble() }.toFloat()
                        if (totalAmount > 0 || timeType == "week") {
                            days.add(Triple(dayMap[i], totalQuantity, totalAmount))
                        }
                        tempCalendar.add(Calendar.DAY_OF_YEAR, 1)
                    }
                    days.sortedBy { dayMap.indexOf(it.first) }
                }
                "month", "last_month" -> {
                    val days = mutableListOf<Triple<String, Int, Float>>()
                    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                    calendar.timeInMillis = startTime
                    for (i in 1..daysInMonth) {
                        calendar.set(Calendar.DAY_OF_MONTH, i)
                        calendar.setStartOfDay()
                        val dayStart = calendar.timeInMillis
                        calendar.setEndOfDay()
                        val dayEnd = calendar.timeInMillis
                        val dayData = repository.getDetailsBetween(dayStart, dayEnd)
                        val totalQuantity = dayData.sumOf { it.quantity.toLong() }.toInt()
                        val totalAmount = dayData.sumOf { it.Amount.toDouble() }.toFloat()
                        if (totalAmount > 0 || (timeType == "month" && i <= Calendar.getInstance().get(Calendar.DAY_OF_MONTH))) {
                            days.add(Triple("Ngày $i", totalQuantity, totalAmount))
                        }
                    }
                    days.sortedBy { it.first.replace("Ngày ", "").toIntOrNull() ?: Int.MAX_VALUE }
                }
                "year" -> {
                    val months = mutableListOf<Triple<String, Int, Float>>()
                    calendar.timeInMillis = startTime
                    val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
                    for (i in 1..currentMonth) {
                        calendar.set(Calendar.MONTH, i - 1)
                        calendar.set(Calendar.DAY_OF_MONTH, 1)
                        calendar.setStartOfDay()
                        val monthStart = calendar.timeInMillis
                        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                        calendar.setEndOfDay()
                        val monthEnd = calendar.timeInMillis
                        val monthData = repository.getDetailsBetween(monthStart, monthEnd)
                        val totalQuantity = monthData.sumOf { it.quantity.toLong() }.toInt()
                        val totalAmount = monthData.sumOf { it.Amount.toDouble() }.toFloat()
                        if (totalAmount > 0 || i <= currentMonth) {
                            months.add(Triple("Tháng $i", totalQuantity, totalAmount))
                        }
                    }
                    months.sortedBy { it.first.replace("Tháng ", "").toIntOrNull() ?: Int.MAX_VALUE }
                }
                "last_year" -> {
                    val months = mutableListOf<Triple<String, Int, Float>>()
                    calendar.timeInMillis = startTime
                    for (i in 1..12) {
                        calendar.set(Calendar.MONTH, i - 1)
                        calendar.set(Calendar.DAY_OF_MONTH, 1)
                        calendar.setStartOfDay()
                        val monthStart = calendar.timeInMillis
                        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                        calendar.setEndOfDay()
                        val monthEnd = calendar.timeInMillis
                        val monthData = repository.getDetailsBetween(monthStart, monthEnd)
                        val totalQuantity = monthData.sumOf { it.quantity.toLong() }.toInt()
                        val totalAmount = monthData.sumOf { it.Amount.toDouble() }.toFloat()
                        if (totalAmount > 0) {
                            months.add(Triple("Tháng $i", totalQuantity, totalAmount))
                        }
                    }
                    months.sortedBy { it.first.replace("Tháng ", "").toIntOrNull() ?: Int.MAX_VALUE }
                }
                else -> emptyList()
            }
            colors = List(details.size) { customColors[it % customColors.size] }
            if (details.isEmpty()) {
                view.showError("Không có dữ liệu để hiển thị biểu đồ")
            } else {
                view.showLineChartData(lineData)
                view.showProductDetails(details, colors)
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

    companion object {
        private const val DAY_IN_MILLIS = 24 * 60 * 60 * 1000L
    }
}