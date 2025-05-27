package com.example.appquanly.ChooseDish.contract

import android.content.Context
import com.example.appquanly.data.sqlite.Entity.InventoryItem
import com.example.appquanly.data.sqlite.Entity.SAInvoiceDetail
import com.example.appquanly.data.sqlite.Entity.SAInvoiceItem

interface ChooseDishContract {

    interface View {
        fun updateInventoryList(items: List<InventoryItem>)
        fun updateTotalMoney(total: Double)
        fun navigateToInvoice(selectedDetails: List<SAInvoiceDetail>)
        fun showMessage(message: String)
        fun openCalculator()
        fun getContext(): Context
        fun openInvoiceScreen(refId: String)






    }

    interface Presenter {
        fun loadItemsFromDB()
        fun onBackClick()
        fun onCollectMoneyClick()
        fun onSettingClick()
        fun onAvatarClick()
        fun addProduct(price: Double)
        fun onIncreaseItem(item: InventoryItem)
        fun onDecreaseItem(item: InventoryItem)
        fun updateQuantityForItem(item: InventoryItem, quantity: Int)
        fun getSelectedInvoiceItems(): List<SAInvoiceItem>
        fun onSaveClick()
        fun getRefId(): String
        fun goToInvoiceScreen(refId: String)
        fun getSelectedInvoiceDetails(): List<SAInvoiceDetail>






    }
}
