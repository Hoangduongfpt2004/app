package com.example.appquanly.Chart

interface ChartContract {
    interface View {
        fun showPieChartData(data: List<Pair<String, Float>>, colors: List<Int>)
        fun showLineChartData(data: List<Pair<String, Float>>)
        fun showProductDetails(details: List<Pair<String, Float>>, colors: List<Int>) // Thêm colors
        fun showTitleAndDate(title: String, date: String)
        fun showTotalAmount(amount: Double)
        fun showError(message: String)
    }

    interface Presenter {
        fun loadChartData(timeType: String)
        fun onDestroy()
    }
}

// Data class để lưu sản phẩm với màu sắc
data class Quadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)