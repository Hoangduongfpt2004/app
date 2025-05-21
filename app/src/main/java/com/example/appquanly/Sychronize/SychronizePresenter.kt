package com.example.appquanly.Sychronize

class SychronizePresenter(private val view: Sync_DataContract.View) : Sync_DataContract.Presenter {

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
