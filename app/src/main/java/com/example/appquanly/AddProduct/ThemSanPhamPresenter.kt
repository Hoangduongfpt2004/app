package com.example.appquanly.AddProduct

// ThemSanPhamPresenter.kt
class ThemSanPhamPresenter(private val view: ThemSanPhamView) {

    fun onSaveButtonClick(unitName: String) {
        if (unitName.isBlank()) {
            view.showError("Tên đơn vị tính không được để trống")
        } else {
            // Thực hiện lưu sản phẩm vào cơ sở dữ liệu (hoặc xử lý thêm sản phẩm)
            view.showSuccess("Đơn vị tính đã được thêm: $unitName")
        }
    }

    fun onCancelButtonClick() {
        view.dismissDialog()
    }
}
