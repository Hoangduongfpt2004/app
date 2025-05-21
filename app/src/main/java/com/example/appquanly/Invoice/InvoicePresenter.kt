package com.example.appquanly.Invoice

import android.content.Context
import com.example.appquanly.data.sqlite.Entity.InventoryItem
import com.example.appquanly.data.sqlite.Entity.SAInvoiceItem
import java.text.SimpleDateFormat
import java.util.*

class InvoicePresenter(
    private val context: Context,
    private val view: InvoiceContract.View
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

        refNo = "HD-${System.currentTimeMillis() % 10000}"
        refDate = System.currentTimeMillis()

        val dateStr = SimpleDateFormat("dd/MM/yyyy (HH:mm)", Locale.getDefault()).format(Date(refDate))
        view.showInvoiceInfo(refNo, dateStr)
    }

    override fun calculateInvoice(receiveAmount: Double) {
        val remain = receiveAmount - totalAmount
        view.showReceiveAmount(receiveAmount)
        view.showRemainAmount(remain)
    }

    override fun onTienKhachDuaChanged(amount: Double) {
        val remain = amount - totalAmount
        view.showRemainAmount(if (remain >= 0) remain else 0.0)
    }

    override fun onDoneClick(receiveAmount: Double) {
        val saInvoice = SAInvoiceItem(
            refId = UUID.randomUUID().toString(),
            refType = 1,
            refNo = refNo,
            refDate = refDate,
            amount = totalAmount,
            returnAmount = 0.0,
            receiveAmount = receiveAmount,
            remainAmount = receiveAmount - totalAmount,
            journalMemo = null,
            paymentStatus = 1,
            numberOfPeople = 0,
            tableName = null,
            listItemName = itemList.joinToString { it.InventoryItemName ?: "" },
            createdDate = System.currentTimeMillis(),
            createdBy = "admin",
            modifiedDate = null,
            modifiedBy = null,
            quantity = itemList.sumOf { it.UseCount }
        )

        // TODO: Lưu saInvoice vào database nếu cần
        // Ví dụ:
        // val invoiceRepo = SAInvoiceRepository(context)
        // invoiceRepo.insertInvoice(saInvoice)
    }
}
