package com.example.appquanly.SalePutIn

import com.example.appquanly.data.sqlite.Entity.SAInvoiceDetail

class SaleePresenter(private val view: SaleeContract.View) : SaleeContract.Presenter {

    private var productList: List<SaleeModel> = emptyList()

    override fun loadProducts() {
        if (productList.isNotEmpty()) {
            // Hiển thị sản phẩm bạn đã thêm
            view.showProducts(productList)
        } else {
            // Nếu chưa có sản phẩm nào thì có thể show rỗng hoặc message
            view.showProducts(emptyList())
        }
    }


    override fun loadProducts(products: List<SAInvoiceDetail>) {
        // Chuyển đổi SAInvoiceDetail thành SaleeModel phù hợp hiển thị
        productList = products.map {
            val name = "${it.InventoryItemName} (${it.Quantity.toInt()})"
            val price = (it.UnitPrice * it.Quantity).toInt().toString() // convert tiền nếu cần
            SaleeModel(name, price)
        }
        view.showProducts(productList)
    }

    override fun onCancelClicked(product: SaleeModel) {
        view.showMessage("Đã hủy món: ${product.name}")
    }

    override fun onPayClicked(product: SaleeModel) {
        view.showMessage("Thu tiền món: ${product.name}")
    }
}


