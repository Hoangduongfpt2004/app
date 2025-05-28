package com.example.appquanly.ChooseDish.presenter

import android.content.Context
import android.widget.Toast
import com.example.appquanly.ChooseDish.contract.ChooseDishContract
import com.example.appquanly.data.sqlite.Entity.InventoryItem
import com.example.appquanly.data.sqlite.Entity.SAInvoiceDetail
import com.example.appquanly.data.sqlite.Entity.SAInvoiceItem
import com.example.appquanly.data.sqlite.Local.InventoryItemRepository
import com.example.appquanly.data.sqlite.Local.SAInvoiceDetailRepository
import com.example.appquanly.data.sqlite.Local.SAInvoiceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class ChooseDishPresenter(
    private val view: ChooseDishContract.View,
    private val inventoryRepo: InventoryItemRepository
) : ChooseDishContract.Presenter {

    private val scope = CoroutineScope(Dispatchers.Main)
    private val context: Context = view.getContext()
    private val invoiceDetailRepo = SAInvoiceDetailRepository(context)
    private val invoiceRepo = SAInvoiceRepository(context)

    private var itemList: MutableList<InventoryItem> = mutableListOf()
    private var totalMoney = 0.0
    private var refID: String = UUID.randomUUID().toString()

    override fun loadItemsFromDB() {
        scope.launch {
            itemList = inventoryRepo.getAllInventoryItems().toMutableList()
            recalculateTotalMoney()
            view.updateInventoryList(itemList)
            view.updateTotalMoney(totalMoney)
        }
    }

    override fun goToInvoiceScreen(refId: String) {
        view.openInvoiceScreen(refId)
    }

    override fun onBackClick() {}
    override fun onSettingClick() {
        view.openCalculator()
    }

    override fun onAvatarClick() {
        view.openCalculator()
    }

    override fun addProduct(price: Double) {
        totalMoney += price
        view.updateTotalMoney(totalMoney)
    }

    override fun onIncreaseItem(item: InventoryItem) {
        val currentQty = item.quantity
        updateQuantityForItem(item, currentQty + 1)
    }

    override fun onDecreaseItem(item: InventoryItem) {
        val currentQty = item.quantity
        if (currentQty > 0) {
            updateQuantityForItem(item, currentQty - 1)
        }
    }

    override fun updateQuantityForItem(item: InventoryItem, quantity: Int) {
        scope.launch {
            item.quantity = quantity
            inventoryRepo.updateInventoryItem(item)
            recalculateTotalMoney()
            view.updateInventoryList(itemList)
            view.updateTotalMoney(totalMoney)
        }
    }

    private fun recalculateTotalMoney() {
        totalMoney = itemList.sumOf {
            val priceDouble = (it.Price ?: 0f).toDouble()
            priceDouble * it.quantity
        }
    }

    private fun getSelectedItems(): List<InventoryItem> {
        return itemList.filter { it.quantity > 0 }
    }

    override fun onCollectMoneyClick() {
        val selectedItems = getSelectedItems()
        if (selectedItems.isEmpty()) {
            view.showMessage("Bạn phải thêm sản phẩm trước khi thu tiền!")
            return
        }

        scope.launch {
            val invoiceDetails = getSelectedInvoiceDetails()
            invoiceDetailRepo.insertDetails(invoiceDetails)
            view.showMessage("Đã lưu hóa đơn thành công!")
        }
    }

    override fun onSaveClick() {
        onCollectMoneyClick()
    }



    override fun getSelectedInvoiceItems(): List<SAInvoiceItem> {
        return getSelectedItems().map {
            val amount = (it.Price ?: 0f).toDouble() * it.quantity
            SAInvoiceItem(
                refId = refID,
                refType = 1,
                refNo = "HD001",
                refDate = System.currentTimeMillis(),
                amount = amount,
                returnAmount = 0.0,
                receiveAmount = amount,
                remainAmount = 0.0,
                journalMemo = "",
                paymentStatus = 0,
                numberOfPeople = 0,
                tableName = "",
                listItemName = it.InventoryItemName ?: "",
                createdDate = System.currentTimeMillis(),
                createdBy = "",
                modifiedDate = null,
                modifiedBy = null,
                quantity = it.quantity
            )
        }
    }

    override fun getRefId(): String = refID

    override fun getSelectedInvoiceDetails(): List<SAInvoiceDetail> {
        return getSelectedItems().mapIndexed { index, item ->
            val quantity = item.quantity.toFloat()
            val price = item.Price ?: 0f
            val amount = quantity * price

            SAInvoiceDetail(
                RefDetailID = UUID.randomUUID().toString(),
                RefDetailType = 1,
                RefID = refID,
                InventoryItemID = item.InventoryItemID,
                InventoryItemName = item.InventoryItemName ?: "",
                UnitID = item.UnitID ?: "",
                UnitName = "", // Lấy từ bảng đơn vị nếu cần
                Quantity = quantity,
                UnitPrice = price,
                Amount = amount,
                Description = "",
                SortOrder = index + 1,
                CreatedDate = System.currentTimeMillis(),
                CreatedBy = "",
                ModifiedDate = null,
                ModifiedBy = ""
            )
        }
    }


    fun convertToInvoiceItems(details: List<SAInvoiceDetail>): List<SAInvoiceItem> {
        val groupedMap = mutableMapOf<String, SAInvoiceItem>()

        for (detail in details) {
            val key = detail.InventoryItemID

            val quantity = detail.Quantity.toInt()
            val amount = detail.Amount.toDouble()

            if (groupedMap.containsKey(key)) {
                val item = groupedMap[key]!!
                item.quantity += quantity
                item.amount += amount
            } else {
                groupedMap[key] = SAInvoiceItem(
                    refId = detail.RefID,
                    refType = 0, // hoặc refType phù hợp nếu có
                    refNo = "",
                    refDate = System.currentTimeMillis(),
                    amount = amount,
                    returnAmount = 0.0,
                    receiveAmount = 0.0,
                    remainAmount = 0.0,
                    journalMemo = null,
                    paymentStatus = 0,
                    numberOfPeople = 0,
                    tableName = null,
                    listItemName = detail.InventoryItemName,
                    createdDate = detail.CreatedDate,
                    createdBy = detail.CreatedBy,
                    modifiedDate = detail.ModifiedDate,
                    modifiedBy = detail.ModifiedBy,
                    quantity = quantity
                )
            }
        }

        return groupedMap.values.toList()
    }


    //  Hàm đã được sửa lại đúng cách
    fun saveInvoice(soBan: String, soKhach: String, tongTien: String, details: List<SAInvoiceDetail>) {
        scope.launch {
            val invoice = SAInvoiceItem(
                refId = refID,
                refType = 1,
                refNo = "HD001",
                refDate = System.currentTimeMillis(),
                amount = tongTien.toDoubleOrNull() ?: 0.0,
                returnAmount = 0.0,
                receiveAmount = tongTien.toDoubleOrNull() ?: 0.0,
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

            val success = invoiceRepo.insertInvoice(invoice)
            if (success) {
                // Gán refId cho từng detail (trong trường hợp refId mới)
                val updatedDetails = details.map {
                    it.copy(RefID = invoice.refId)
                }
                invoiceDetailRepo.insertDetails(updatedDetails)
                onInvoiceSavedSuccess()
            } else {
                onInvoiceSavedFailed("Không thể lưu hóa đơn.")
            }
        }
    }

    override fun submitInvoiceToSaleeActivity(tableName: String, numberOfPeople: Int) {
        val selectedItems = getSelectedItems()
        if (selectedItems.isEmpty()) {
            view.showMessage("Bạn chưa chọn sản phẩm nào!")
            return
        }

        val invoiceItems = getSelectedInvoiceItems().map {
            it.copy(tableName = tableName, numberOfPeople = numberOfPeople)
        }

        view.navigateToSaleeScreen(invoiceItems)

        val totalAmount = invoiceItems.sumOf { it.amount }
        val invoice = SAInvoiceItem(
            refId = refID,
            refType = 1,
            refNo = "HD001",
            refDate = System.currentTimeMillis(),
            amount = totalAmount,
            returnAmount = 0.0,
            receiveAmount = totalAmount,
            remainAmount = 0.0,
            journalMemo = "",
            paymentStatus = 0,
            numberOfPeople = numberOfPeople,
            tableName = tableName,
            createdDate = System.currentTimeMillis(),
            createdBy = "",
            modifiedDate = null,
            modifiedBy = null
        )

        scope.launch {
            val success = invoiceRepo.insertInvoice(invoice)
            if (success) {
                Toast.makeText(context, "Lưu hóa đơn thành công!", Toast.LENGTH_SHORT).show()
                onInvoiceSavedSuccess()
            } else {
                Toast.makeText(context, "Lưu hóa đơn thất bại!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onInvoiceSavedSuccess() {
        view.showMessage("Lưu hóa đơn thành công!")
    }

    override fun onInvoiceSavedFailed(error: String) {
        view.showMessage("Lưu hóa đơn thất bại: $error")
    }
}
