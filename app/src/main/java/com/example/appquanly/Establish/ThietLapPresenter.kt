package com.example.appquanly.Establish.presenter

import com.example.appquanly.Establish.contract.ThietLapContract

class ThietLapPresenter(private val view: ThietLapContract.View) : ThietLapContract.Presenter {

    override fun onRestoreDataClicked() {
        view.showRestoreDataDialog()
    }
}
