package com.example.appquanly.SalePutIn


interface SaleeContract {
    interface View {
        fun showProducts(products: List<SaleeModel>)
        fun showMessage(message: String)
    }

    interface Presenter {
        fun loadProducts()
        fun onCancelClicked(product: SaleeModel)
        fun onPayClicked(product: SaleeModel)
    }
}


