package com.example.appquanly.Sunchroize

class SyncDataPresenter(private val view: SyncDataContract.View) : SyncDataContract.Presenter {

    override fun onSyncClicked(code: String) {
        if (code.isBlank()) {
            view.showToast("Vui lòng nhập mã đồng bộ")
        } else {
            // TODO: Gọi xử lý đồng bộ dữ liệu tại đây
            view.showToast("Đang đồng bộ với mã: $code")

            // Sau khi xử lý xong, quay về màn hình chính
            view.navigateToSaleScreen()
        }
    }
}
