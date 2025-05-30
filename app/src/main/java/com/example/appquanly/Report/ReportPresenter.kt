package com.example.appquanly.Report

import android.content.Context
import com.example.appquanly.data.sqlite.Local.SAInvoiceDetailRepository
import kotlinx.coroutines.*

class ReportPresenter(
    private val context: Context,
    private var view: ReportContract.View?
) : ReportContract.Presenter {

    private val repository = SAInvoiceDetailRepository(context)
    private val presenterScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun loadReportData() {
        loadToday()
        loadYesterday()
        loadThisWeek()
        loadThisMonth()
        loadThisYear()
    }

    override fun loadToday() {
        presenterScope.launch {
            try {
                val total = withContext(Dispatchers.IO) { repository.getTotalAmountToday() }
                view?.showTotalToday(total)
            } catch (e: Exception) {
                e.printStackTrace()
                view?.showError("Lỗi tải dữ liệu hôm nay")
            }
        }
    }

    override fun loadYesterday() {
        presenterScope.launch {
            try {
                val total = withContext(Dispatchers.IO) { repository.getTotalAmountYesterday() }
                view?.showTotalYesterday(total)
            } catch (e: Exception) {
                e.printStackTrace()
                view?.showError("Lỗi tải dữ liệu hôm qua")
            }
        }
    }

    override fun loadThisWeek() {
        presenterScope.launch {
            try {
                val total = withContext(Dispatchers.IO) { repository.getTotalAmountThisWeek() }
                view?.showTotalThisWeek(total)
            } catch (e: Exception) {
                e.printStackTrace()
                view?.showError("Lỗi tải dữ liệu tuần này")
            }
        }
    }

    override fun loadThisMonth() {
        presenterScope.launch {
            try {
                val total = withContext(Dispatchers.IO) { repository.getTotalAmountThisMonth() }
                view?.showTotalThisMonth(total)
            } catch (e: Exception) {
                e.printStackTrace()
                view?.showError("Lỗi tải dữ liệu tháng này")
            }
        }
    }

    override fun loadThisYear() {
        presenterScope.launch {
            try {
                val total = withContext(Dispatchers.IO) { repository.getTotalAmountThisYear() }
                view?.showTotalThisYear(total)
            } catch (e: Exception) {
                e.printStackTrace()
                view?.showError("Lỗi tải dữ liệu năm nay")
            }
        }
    }

    override fun onDestroy() {
        presenterScope.cancel()
        view = null
    }
}
