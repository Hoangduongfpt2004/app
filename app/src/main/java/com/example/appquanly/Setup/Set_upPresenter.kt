package com.example.appquanly.Setup.presenter

import com.example.appquanly.Setup.contract.Set_upContract

class Set_upPresenter(private val view: Set_upContract.View) : Set_upContract.Presenter {

    override fun onRestoreDataClicked() {
        view.showRestoreDataDialog()
    }
}
