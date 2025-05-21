package com.example.appquanly.ChooseDish.contract

import com.example.appquanly.data.sqlite.Entity.InventoryItem

interface ChooseDishContract {
    interface View {
        fun showMessage(msg: String)
        fun openCalculator()
        fun updateTotalMoney(total: Double)
        fun updateInventoryList(list: List<InventoryItem>)
        fun navigateToInvoice(selectedItems: List<InventoryItem>)

    }

    interface Presenter {
        fun onBackClick()
        fun onCollectMoneyClick()
        fun onSettingClick()
        fun onAvatarClick()
        fun addProduct(price: Double)

        fun loadItemsFromDB()
        fun onIncreaseItem(item: InventoryItem)
        fun onDecreaseItem(item: InventoryItem)

    }
}
