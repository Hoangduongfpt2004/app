package com.example.appquanly.ChooseDish.presenter

import com.example.appquanly.ChooseDish.contract.ChooseDishContract
import com.example.appquanly.data.sqlite.Entity.InventoryItem
import com.example.appquanly.data.sqlite.Entity.SAInvoiceDetail
import com.example.appquanly.data.sqlite.Entity.SAInvoiceItem
import com.example.appquanly.data.sqlite.Local.InventoryItemRepository
import com.example.appquanly.data.sqlite.Local.SAInvoiceDetailRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class ChooseDishPresenter(
    private val view: ChooseDishContract.View,
    private val inventoryRepo: InventoryItemRepository
) : ChooseDishContract.Presenter {

    private val scope = CoroutineScope(Dispatchers.Main)
    private val invoiceDetailRepo = SAInvoiceDetailRepository(view.getContext())
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


    override fun onBackClick() {
        // Xử lý khi bấm nút Back nếu cần
    }

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
            val invoiceDetails = selectedItems.mapIndexed { index, item ->
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
                    UnitName = "",
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

            // Lưu chi tiết hóa đơn vào database
            invoiceDetailRepo.insertDetails(invoiceDetails)

            view.showMessage("Đã lưu hóa đơn thành công!")
        }
    }

    override fun onSaveClick() {
        // Gọi lại xử lý lưu khi bấm nút Lưu
        onCollectMoneyClick()
    }

    override fun getSelectedInvoiceItems(): List<SAInvoiceItem> {
        return getSelectedItems().map {
            SAInvoiceItem(
                refId = refID,
                refType = 1,
                refNo = "HD001",
                refDate = System.currentTimeMillis(),
                amount = (it.Price ?: 0f).toDouble() * it.quantity,
                returnAmount = 0.0,
                receiveAmount = (it.Price ?: 0f).toDouble() * it.quantity,
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

    override fun getRefId(): String {
        return refID
    }
}
