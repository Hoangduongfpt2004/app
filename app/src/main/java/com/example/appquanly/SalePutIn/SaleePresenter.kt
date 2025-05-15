package com.example.appquanly.SalePutIn

class SaleePresenter(private val view: SaleeContract.View) : SaleeContract.Presenter {

    override fun loadProducts() {
        val productList = listOf(
            SaleeModel("Coca (1), Sting (1)", "57.000"),
            SaleeModel("Sting (1)", "12.000")
        )
        view.showProducts(productList)
    }

    override fun onCancelClicked(product: SaleeModel) {
        view.showMessage("Đã hủy món: ${product.name}")
    }

    override fun onPayClicked(product: SaleeModel) {
        view.showMessage("Thu tiền món: ${product.name}")
    }
}

