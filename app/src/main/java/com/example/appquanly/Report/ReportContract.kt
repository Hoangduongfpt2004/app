package com.example.appquanly.Report

interface ReportContract {

    interface View {
        fun showTotalToday(amount: Double)
        fun showTotalYesterday(amount: Double)
        fun showTotalThisWeek(amount: Double)
        fun showTotalThisMonth(amount: Double)
        fun showTotalThisYear(amount: Double)
        fun showError(message: String)
    }

    interface Presenter {
        fun loadReportData()
        fun loadToday()
        fun loadYesterday()
        fun loadThisWeek()
        fun loadThisMonth()
        fun loadThisYear()
        fun onDestroy()
    }
}
