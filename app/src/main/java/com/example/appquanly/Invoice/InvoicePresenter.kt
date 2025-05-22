package com.example.appquanly.Invoice

import android.content.Context
import com.example.appquanly.data.sqlite.Entity.InventoryItem
import com.example.appquanly.data.sqlite.Entity.SAInvoiceItem
import com.example.appquanly.data.sqlite.Local.InventoryItemRepository
import com.example.appquanly.data.sqlite.Local.SAInvoiceRepository
import java.text.SimpleDateFormat
import java.util.*

class InvoicePresenter(
    private val context: Context,
    private val view: InvoiceContract.View,
    private val inventoryRepository: InventoryItemRepository,
    private val invoiceRepository: SAInvoiceRepository
) : InvoiceContract.Presenter {

    private var itemList: List<InventoryItem> = listOf()
    private var totalAmount: Double = 0.0
    private var refNo: String = ""
    private var refDate: Long = 0L

    override fun setSelectedItems(items: List<InventoryItem>) {
        itemList = items
    }

    override fun loadInvoiceData() {
        totalAmount = itemList.sumOf { (it.Price ?: 0f).toDouble() * it.UseCount.toDouble() }
        view.showInvoiceData(itemList)
        view.showTotalAmount(totalAmount)

        refNo = "HD-${System.currentTimeMillis() % 100000}"
        refDate = System.currentTimeMillis()

        val dateStr = SimpleDateFormat("dd/MM/yyyy (HH:mm)", Locale.getDefault()).format(Date(refDate))
        view.showInvoiceInfo(refNo, dateStr)
    }

    override fun calculateInvoice(receiveAmount: Double) {
        val remain = receiveAmount - totalAmount
        view.showReceiveAmount(receiveAmount)
        view.showRemainAmount(if (remain >= 0) remain else 0.0)
    }

    override fun onDoneClick(receiveAmount: Double) {
        val remain = receiveAmount - totalAmount
        val listItemName = itemList.joinToString(", ") { it.InventoryItemName ?: "" }

        val invoice = SAInvoiceItem(
            refId = UUID.randomUUID().toString(),
            refType = 1,
            refNo = refNo,
            refDate = refDate,
            amount = totalAmount,
            returnAmount = 0.0,
            receiveAmount = receiveAmount,
            remainAmount = remain,
            journalMemo = "Hóa đơn bán hàng",
            paymentStatus = 1,
            numberOfPeople = 1,
            tableName = null,
            listItemName = listItemName,
            createdDate = refDate,
            createdBy = "admin",
            modifiedDate = null,
            modifiedBy = null
        )

        val result = invoiceRepository.insertInvoice(invoice)
        if (result) {
            view.showToast("Đã lưu hóa đơn thành công!")
        } else {
            view.showToast("Lỗi khi lưu hóa đơn.")
        }

        view.showRemainAmount(remain)
    }
}
