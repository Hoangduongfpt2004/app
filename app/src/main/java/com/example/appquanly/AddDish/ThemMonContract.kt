package com.example.appquanly.AddDish

interface ThemMonContract {
    interface View {
        fun showColorPickerDialog()
        fun applySelectedColor(color: Int)
    }

    interface Presenter {
        fun onColorButtonClicked()
        fun onColorSelected(color: Int)
    }
}
