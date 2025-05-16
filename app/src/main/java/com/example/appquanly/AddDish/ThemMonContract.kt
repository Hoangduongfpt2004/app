// ThemMonContract.kt
package com.example.appquanly.AddDish

interface ThemMonContract {
    interface View {
        fun showColorPickerDialog()
        fun applySelectedColor(color: Int)
        fun showSuccess(message: String)
        fun showError(message: String)
    }

    interface Presenter {
        fun onColorButtonClicked()
        fun onColorSelected(color: Int)
        fun addInventoryItem(name: String, price: Float?, unit: String, color: String?, iconFileName: String?)
    }
}