package com.example.appquanly.AddDish

class ThemMonPresenter(private val view: ThemMonContract.View) : ThemMonContract.Presenter {
    override fun onColorButtonClicked() {
        view.showColorPickerDialog()
    }

    override fun onColorSelected(color: Int) {
        view.applySelectedColor(color)
    }
}
