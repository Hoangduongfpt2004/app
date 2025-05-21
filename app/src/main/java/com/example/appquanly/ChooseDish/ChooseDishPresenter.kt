package com.example.appquanly.ChooseDish.presenter

import com.example.appquanly.ChooseDish.contract.ChooseDishContract
import com.example.appquanly.data.sqlite.Entity.InventoryItem
import com.example.appquanly.data.sqlite.Local.InventoryItemRepository

class ChooseDishPresenter(
    private val view: ChooseDishContract.View,
    private val repository: InventoryItemRepository
) : ChooseDishContract.Presenter {

    private var totalMoney = 0.0
    private var itemList: MutableList<InventoryItem> = mutableListOf()

    override fun loadItemsFromDB() {
        itemList = repository.getAllInventoryItems().toMutableList()
        recalculateTotalMoney()
        view.updateInventoryList(itemList)
        view.updateTotalMoney(totalMoney)
    }

    override fun onBackClick() {
        // Xử lý nếu cần khi bấm back
    }

    override fun onCollectMoneyClick() {
        val selectedItems = getSelectedItems()
        if (selectedItems.isNotEmpty()) {
            view.navigateToInvoice(selectedItems)
        } else {
            view.showMessage("Bạn phải thêm sản phẩm trước khi thu tiền!")
        }
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
        val currentQty = item.quantity ?: 0
        item.quantity = currentQty + 1
        totalMoney += item.Price?.toDouble() ?: 0.0
        view.updateTotalMoney(totalMoney)
        view.updateInventoryList(itemList)
    }

    override fun onDecreaseItem(item: InventoryItem) {
        val currentQty = item.quantity ?: 0
        if (currentQty > 0) {
            item.quantity = currentQty - 1
            totalMoney -= item.Price?.toDouble() ?: 0.0
            if (totalMoney < 0) totalMoney = 0.0
            view.updateTotalMoney(totalMoney)
            view.updateInventoryList(itemList)
        }
    }

    private fun recalculateTotalMoney() {
        totalMoney = itemList.sumOf { (it.Price?.toDouble() ?: 0.0) * (it.quantity ?: 0) }
    }

    fun getSelectedItems(): List<InventoryItem> {
        return itemList.filter { (it.quantity ?: 0) > 0 }
    }
}
