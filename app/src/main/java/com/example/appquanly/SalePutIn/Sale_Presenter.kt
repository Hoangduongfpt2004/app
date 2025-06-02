package com.example.appquanly.salee

import android.content.Context
import com.example.appquanly.R
import com.example.appquanly.SalePutIn.InvoiceWithDetails
import com.example.appquanly.data.sqlite.Entity.SAInvoiceDetail
import com.example.appquanly.data.sqlite.Entity.SAInvoiceItem
import com.example.appquanly.data.sqlite.Local.SAInvoiceDetailRepository
import com.example.appquanly.data.sqlite.Local.SAInvoiceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class SaleePresenter(
    private val context: Context,
    private val view: SaleeContract.View
) : SaleeContract.Presenter {

    private val invoiceRepo = SAInvoiceRepository(context)
    private val invoiceDetailRepo = SAInvoiceDetailRepository(context)
    private val scopeMain = CoroutineScope(Dispatchers.Main)

    override fun onHintClicked() {
        view.showMenu()
    }

    override fun onAddButtonClicked() {
        view.openOrderScreen()
    }

    override fun onMenuItemClicked(itemId: Int) {
        when (itemId) {
            R.id.ban_hang -> view.navigateTo(SaleeContract.MenuScreen.BAN_HANG)
            R.id.thuc_don -> view.navigateTo(SaleeContract.MenuScreen.THUC_DON)
            R.id.bao_cao -> view.navigateTo(SaleeContract.MenuScreen.BAO_CAO)
            R.id.dong_bo_du_lieu -> view.navigateTo(SaleeContract.MenuScreen.DONG_BO_DU_LIEU)
            R.id.thiet_lap -> view.navigateTo(SaleeContract.MenuScreen.THIET_LAP)
            R.id.lien_ket_tai_khoan -> view.navigateTo(SaleeContract.MenuScreen.LIEN_KET_TAI_KHOAN)
            R.id.thong_bao -> view.navigateTo(SaleeContract.MenuScreen.THONG_BAO)
            R.id.gioi_thieu -> view.navigateTo(SaleeContract.MenuScreen.GIOI_THIEU_CHO_BAN)
            R.id.danh_gia_ung_dung -> view.navigateTo(SaleeContract.MenuScreen.DANH_GIA_UNG_DUNG)
            R.id.gop_y -> view.navigateTo(SaleeContract.MenuScreen.GOP_Y_VOI_NHA_PHAT_TRIEN)
            R.id.thong_tin_san_pham -> view.navigateTo(SaleeContract.MenuScreen.THONG_TIN_SAN_PHAM)
            R.id.dat_mat_khau -> view.navigateTo(SaleeContract.MenuScreen.DAT_MAT_KHAU)
            R.id.dang_xuat -> view.navigateTo(SaleeContract.MenuScreen.DANG_XUAT)
        }
    }

    override fun loadInvoiceItems() {
        scopeMain.launch {
            val invoices = withContext(Dispatchers.IO) { invoiceRepo.getAllInvoices() }
            if (invoices.isEmpty()) {
                view.showNoOrders()
                return@launch
            }

            val invoicesWithDetails = withContext(Dispatchers.IO) {
                invoices.map { invoice ->
                    val details = invoiceDetailRepo.getDetailsByRefID(invoice.refId)
                    InvoiceWithDetails(invoice, details)
                }
            }

            view.showInvoiceItems(invoicesWithDetails)
        }
    }

    override fun saveInvoiceItem(item: SAInvoiceItem) {
        scopeMain.launch {
            withContext(Dispatchers.IO) {
                invoiceRepo.insertInvoice(item)
            }
            loadInvoiceItems()
        }
    }

    override fun saveInvoiceFull(
        soBan: String,
        soKhach: String,
        tongTien: String,
        invoiceItems: List<SAInvoiceItem>,
        invoiceDetails: List<SAInvoiceDetail>
    ) {
        scopeMain.launch {
            val currentTime = System.currentTimeMillis()
            val refId = invoiceItems.firstOrNull()?.refId ?: UUID.randomUUID().toString()
            val amount = tongTien.toDoubleOrNull() ?: 0.0
            val numberOfPeople = soKhach.toIntOrNull() ?: 0

            val mainInvoice = SAInvoiceItem(
                refId = refId,
                refType = 1,
                refNo = "HD001", // Có thể sinh số hóa đơn tùy ý
                refDate = currentTime,
                amount = amount,
                returnAmount = 0.0,
                receiveAmount = amount,
                remainAmount = 0.0,
                journalMemo = "",
                paymentStatus = 0,
                numberOfPeople = numberOfPeople,
                tableName = soBan,
                createdDate = currentTime,
                createdBy = "",
                modifiedDate = null,
                modifiedBy = null
            )

            withContext(Dispatchers.IO) {
                invoiceRepo.insertInvoice(mainInvoice)
                invoiceItems.forEach { invoiceRepo.insertInvoice(it.copy(refId = refId)) }
                val now = System.currentTimeMillis()
                invoiceDetails.forEach {
                    invoiceDetailRepo.insertDetail(it.copy(RefID = refId, CreatedDate = now))
                }

            }

            view.showMessage("Lưu hóa đơn thành công!")
            loadInvoiceItems()
        }
    }

    override fun saveInvoice(
        soBan: String,
        soKhach: String,
        tongTien: String,
        details: List<SAInvoiceDetail>
    ) {
        scopeMain.launch {
            val currentTime = System.currentTimeMillis()
            val refId = UUID.randomUUID().toString()
            val amount = tongTien.toDoubleOrNull() ?: 0.0
            val numberOfPeople = soKhach.toIntOrNull() ?: 0

            val mainInvoice = SAInvoiceItem(
                refId = refId,
                refType = 1,
                refNo = "HD001", // Có thể tùy biến số hóa đơn
                refDate = currentTime,
                amount = amount,
                returnAmount = 0.0,
                receiveAmount = amount,
                remainAmount = 0.0,
                journalMemo = "",
                paymentStatus = 0,
                numberOfPeople = numberOfPeople,
                tableName = soBan,
                createdDate = currentTime,
                createdBy = "",
                modifiedDate = null,
                modifiedBy = null
            )

            withContext(Dispatchers.IO) {
                invoiceRepo.insertInvoice(mainInvoice)
                details.forEach { invoiceDetailRepo.insertDetail(it.copy(RefID = refId)) }
            }

            view.showMessage("Lưu hóa đơn thành công!")
            loadInvoiceItems()
        }
    }
}
