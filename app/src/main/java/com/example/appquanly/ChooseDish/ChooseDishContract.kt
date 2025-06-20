package com.example.appquanly.ChooseDish.contract

import android.content.Context
import com.example.appquanly.data.sqlite.Entity.InventoryItem
import com.example.appquanly.data.sqlite.Entity.SAInvoiceDetail
import com.example.appquanly.data.sqlite.Entity.SAInvoiceItem

interface ChooseDishContract {

    interface View {
        fun updateInventoryList(items: List<InventoryItem>)
        fun updateTotalMoney(total: Double)
        fun navigateToInvoice(selectedDetails: List<SAInvoiceDetail>, invoiceItem: SAInvoiceItem)
        fun showMessage(message: String)
        fun openCalculator()
        fun getContext(): Context
        fun openInvoiceScreen(refId: String)
        fun navigateToSaleeScreen(invoiceItems: List<SAInvoiceItem>)
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
        fun getSelectedInvoiceDetails(): List<SAInvoiceDetail>
        fun submitInvoiceToSaleeActivity(tableName: String, numberOfPeople: Int)
        fun onInvoiceSavedSuccess()
        fun onInvoiceSavedFailed(error: String)
    }
}