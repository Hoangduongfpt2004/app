package com.example.appquanly.ChooseDish.presenter

import com.example.appquanly.ChooseDish.contract.ChooseDishContract
import com.example.appquanly.data.sqlite.Entity.InventoryItem
import com.example.appquanly.data.sqlite.Entity.SAInvoiceDetail
import com.example.appquanly.data.sqlite.Local.InventoryItemRepository
import com.example.appquanly.data.sqlite.Local.SAInvoiceDetailRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.time.times

class ChooseDishPresenter(
    private val view: ChooseDishContract.View,
    private val inventoryRepo: InventoryItemRepository
) : ChooseDishContract.Presenter {

    private val scope = CoroutineScope(Dispatchers.Main)
    private val invoiceDetailRepo = SAInvoiceDetailRepository(view.getContext())
    private var itemList: MutableList<InventoryItem> = mutableListOf()
    private var totalMoney = 0.0

    override fun loadItemsFromDB() {
        scope.launch {
            itemList = inventoryRepo.getAllInventoryItems().toMutableList()
            recalculateTotalMoney()
            view.updateInventoryList(itemList)
            view.updateTotalMoney(totalMoney)
        }
    }

    override fun onBackClick() {
        // Có thể thêm xử lý nếu cần
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
        if (currentQty > 0) updateQuantityForItem(item, currentQty - 1)
    }

    override fun updateQuantityForItem(item: InventoryItem, quantity: Int) {
        scope.launch {
            item.quantity = quantity
            // Cập nhật database
            inventoryRepo.updateInventoryItem(item)
            // Tính lại tổng tiền
            recalculateTotalMoney()
            // Cập nhật lại danh sách và tổng tiền trên UI
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
            val refID = UUID.randomUUID().toString()
            val invoiceDetails = selectedItems.mapIndexed { index, item ->
                val quantity = item.quantity.toFloat() // convert Int -> Float
                val price = (item.Price ?: 0f)
                val amount = quantity * price

                SAInvoiceDetail(
                    RefDetailID = UUID.randomUUID().toString(),  // đúng tên trường (dù có lỗi chính tả)
                    RefDetailType = 1,
                    RefID = refID,
                    InventoryItemID = item.InventoryItemID,
                    InventoryItemName = item.InventoryItemName ?: "",
                    UnitID = item.UnitID ?: "",
                    UnitName = "", // bạn chưa có dữ liệu UnitName, để trống hoặc lấy nếu có
                    Quantity = quantity,
                    UnitPrice = price,
                    Amount = amount,
                    Description = "", // để trống nếu chưa có
                    SortOrder = index + 1,
                    CreatedDate = System.currentTimeMillis(),
                    CreatedBy = "", // để trống hoặc lấy user hiện tại
                    ModifiedDate = null,
                    ModifiedBy = ""
                )
            }
            view.navigateToInvoice(invoiceDetails)

        }


        }

    }


