package com.example.appquanly.ChooseDish.presenter

import com.example.appquanly.ChooseDish.contract.ChooseDishContract

class ChooseDishPresenter(private val view: ChooseDishContract.View) : ChooseDishContract.Presenter {

    private var totalMoney = 0.0
    private var hasProduct = false

    override fun onBackClick() {
        // Xử lý nút back, ví dụ finish activity hoặc show dialog
        // Tạm thời gọi finish giả sử view là Activity
        // Nếu view là activity bạn có thể cast để finish hoặc dùng callback
    }

    override fun onCollectMoneyClick() {
        if (hasProduct) {
            // Cho phép thu tiền
            // Xử lý thu tiền tại đây
            view.showMessage("Thu tiền: $totalMoney")
        } else {
            view.showMessage("Bạn phải thêm sản phẩm trước khi thu tiền!")
        }
    }

    override fun onSettingClick() {
        // Mở máy tính (CalculatorActivity)
        view.openCalculator()
    }

    override fun onAvatarClick() {
        // Mở máy tính (CalculatorActivity)
        view.openCalculator()
    }

    override fun addProduct(price: Double) {
        totalMoney += price
        hasProduct = true
        view.updateTotalMoney(totalMoney)
    }
}
