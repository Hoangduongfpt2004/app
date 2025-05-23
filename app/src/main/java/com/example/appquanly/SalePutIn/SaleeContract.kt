package com.example.appquanly.SalePutIn

import com.example.appquanly.data.sqlite.Entity.SAInvoiceDetail


interface SaleeContract {
    interface View {
        fun showProducts(products: List<SaleeModel>)
        fun showMessage(message: String)
    }

    interface Presenter {
        fun loadProducts()
        fun loadProducts(products: List<SAInvoiceDetail>)  // thêm hàm này
        fun onCancelClicked(product: SaleeModel)
        fun onPayClicked(product: SaleeModel)
    }
}



