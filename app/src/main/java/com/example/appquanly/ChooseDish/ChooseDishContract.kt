package com.example.appquanly.ChooseDish.contract

interface ChooseDishContract {
    interface View {
        fun showMessage(msg: String)
        fun openCalculator()
        fun updateTotalMoney(total: Double)
    }

    interface Presenter {
        fun onBackClick()
        fun onCollectMoneyClick()
        fun onSettingClick()
        fun onAvatarClick()
        fun addProduct(price: Double)
    }
}
