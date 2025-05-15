package com.example.appquanly.Sale

interface BanHangContract {

    interface View {
        fun showNoOrders()
        fun openOrderScreen()
        fun showMenu()
        fun navigateTo(screen: MenuScreen)

        // Đảm bảo khai báo phương thức này trong View
        fun onMenuItemClicked(itemId: Int)
    }

    interface Presenter {
        fun onHintClicked()
        fun onMenuItemClicked(itemId: Int)
        fun onAddButtonClicked()
    }

    enum class MenuScreen {
        BAN_HANG, THUC_DON, BAO_CAO, DONG_BO_DU_LIEU, THIET_LAP, LIEN_KET_TAI_KHOAN,
        THONG_BAO, GIOI_THIEU_CHO_BAN, DANH_GIA_UNG_DUNG, GOP_Y_VOI_NHA_PHAT_TRIEN,
        THONG_TIN_SAN_PHAM, DAT_MAT_KHAU, DANG_XUAT
    }
}
