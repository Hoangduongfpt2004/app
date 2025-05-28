package com.example.appquanly.salee

import com.example.appquanly.data.sqlite.Entity.SAInvoiceDetail
import com.example.appquanly.data.sqlite.Entity.SAInvoiceItem

interface SaleeContract {

    interface View {
        fun showNoOrders()
        fun openOrderScreen()
        fun showMenu()
        fun navigateTo(screen: MenuScreen)
        fun onMenuItemClicked(itemId: Int)
        fun showInvoiceItems(items: List<SAInvoiceItem>)
        fun showMessage(message: String)
    }

    interface Presenter {
        fun onHintClicked()
        fun onAddButtonClicked()
        fun onMenuItemClicked(itemId: Int)
        fun loadInvoiceItems()
        fun saveInvoiceItem(item: SAInvoiceItem)
        fun saveInvoiceFull(
            soBan: String,
            soKhach: String,
            tongTien: String,
            invoiceItems: List<SAInvoiceItem>,
            invoiceDetails: List<SAInvoiceDetail>
        )
        fun saveInvoice(
            soBan: String,
            soKhach: String,
            tongTien: String,
            details: List<SAInvoiceDetail>
        )
    }

    enum class MenuScreen {
        BAN_HANG, THUC_DON, BAO_CAO, DONG_BO_DU_LIEU, THIET_LAP, LIEN_KET_TAI_KHOAN,
        THONG_BAO, GIOI_THIEU_CHO_BAN, DANH_GIA_UNG_DUNG, GOP_Y_VOI_NHA_PHAT_TRIEN,
        THONG_TIN_SAN_PHAM, DAT_MAT_KHAU, DANG_XUAT
    }
}
