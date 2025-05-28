package com.example.appquanly.salee

import android.content.Context
import com.example.appquanly.R
import com.example.appquanly.data.sqlite.Entity.SAInvoiceDetail
import com.example.appquanly.data.sqlite.Entity.SAInvoiceItem
import com.example.appquanly.data.sqlite.Local.SAInvoiceDetailRepository
import com.example.appquanly.data.sqlite.Local.SAInvoiceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class SaleePresenter(
    private val context: Context,
    private val view: SaleeContract.View
) : SaleeContract.Presenter {

    private val invoiceRepo = SAInvoiceRepository(context)
    private val invoiceDetailRepo = SAInvoiceDetailRepository(context)
    private val scopeMain = CoroutineScope(Dispatchers.Main)
    private val scopeIO = CoroutineScope(Dispatchers.IO)

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
        val items = invoiceRepo.getAllInvoices()
        if (items.isEmpty()) {
            view.showNoOrders()
        } else {
            view.showInvoiceItems(items)
        }
    }

    override fun saveInvoiceItem(item: SAInvoiceItem) {
        invoiceRepo.insertInvoice(item)
        loadInvoiceItems()
    }

    override fun saveInvoiceFull(
        soBan: String,
        soKhach: String,
        tongTien: String,
        invoiceItems: List<SAInvoiceItem>,
        invoiceDetails: List<SAInvoiceDetail>
    ) {
        scopeMain.launch {
            val refId = invoiceItems.firstOrNull()?.refId ?: UUID.randomUUID().toString()
            val refDate = System.currentTimeMillis()
            val amount = tongTien.toDoubleOrNull() ?: 0.0
            val numberOfPeople = soKhach.toIntOrNull() ?: 0

            val mainInvoice = SAInvoiceItem(
                refId = refId,
                refType = 1,
                refNo = "HD001",
                refDate = refDate,
                amount = amount,
                returnAmount = 0.0,
                receiveAmount = amount,
                remainAmount = 0.0,
                journalMemo = "",
                paymentStatus = 0,
                numberOfPeople = numberOfPeople,
                tableName = soBan,
                createdDate = refDate,
                createdBy = "",
                modifiedDate = null,
                modifiedBy = null
            )

            invoiceRepo.insertInvoice(mainInvoice)
            invoiceItems.forEach { invoiceRepo.insertInvoice(it.copy(refId = refId)) }
            invoiceDetails.forEach { invoiceDetailRepo.insertDetail(it.copy(RefID = refId)) }

            view.showMessage("Lưu hóa đơn thành công!")
        }
    }

    override fun saveInvoice(
        soBan: String,
        soKhach: String,
        tongTien: String,
        details: List<SAInvoiceDetail>
    ) {
        scopeIO.launch {
            val refId = UUID.randomUUID().toString()
            val amount = tongTien.toDoubleOrNull() ?: 0.0

            val invoice = SAInvoiceItem(
                refId = refId,
                refType = 1,
                refNo = "HD001",
                refDate = System.currentTimeMillis(),
                amount = amount,
                returnAmount = 0.0,
                receiveAmount = amount,
                remainAmount = 0.0,
                journalMemo = "",
                paymentStatus = 0,
                numberOfPeople = soKhach.toIntOrNull() ?: 0,
                tableName = soBan,
                createdDate = System.currentTimeMillis(),
                createdBy = "",
                modifiedDate = null,
                modifiedBy = null
            )

            invoiceRepo.insertInvoice(invoice)

            details.mapIndexed { index, detail ->
                detail.copy(
                    RefDetailID = UUID.randomUUID().toString(),
                    RefID = refId,
                    SortOrder = index + 1
                )
            }.forEach { invoiceDetailRepo.insertDetail(it) }
        }
    }
}
