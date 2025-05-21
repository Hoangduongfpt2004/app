package com.example.appquanly.SalesSreen

import com.example.appquanly.R


class Sale_SreenPresenter(private val view: Sale_SreenContract.View) : Sale_SreenContract.Presenter {

    override fun onHintClicked() {
        view.showMenu()
    }

    override fun onAddButtonClicked() {
        view.openOrderScreen()
    }

    override fun onMenuItemClicked(itemId: Int) {
        when (itemId) {
            R.id.ban_hang -> view.navigateTo(Sale_SreenContract.MenuScreen.BAN_HANG)
            R.id.thuc_don -> view.navigateTo(Sale_SreenContract.MenuScreen.THUC_DON)
            R.id.bao_cao -> view.navigateTo(Sale_SreenContract.MenuScreen.BAO_CAO)
            R.id.dong_bo_du_lieu -> view.navigateTo(Sale_SreenContract.MenuScreen.DONG_BO_DU_LIEU)
            R.id.thiet_lap -> view.navigateTo(Sale_SreenContract.MenuScreen.THIET_LAP)
            R.id.lien_ket_tai_khoan -> view.navigateTo(Sale_SreenContract.MenuScreen.LIEN_KET_TAI_KHOAN)
            R.id.thong_bao -> view.navigateTo(Sale_SreenContract.MenuScreen.THONG_BAO)
            R.id.gioi_thieu -> view.navigateTo(Sale_SreenContract.MenuScreen.GIOI_THIEU_CHO_BAN)
            R.id.danh_gia_ung_dung -> view.navigateTo(Sale_SreenContract.MenuScreen.DANH_GIA_UNG_DUNG)
            R.id.gop_y -> view.navigateTo(Sale_SreenContract.MenuScreen.GOP_Y_VOI_NHA_PHAT_TRIEN)
            R.id.thong_tin_san_pham -> view.navigateTo(Sale_SreenContract.MenuScreen.THONG_TIN_SAN_PHAM)
            R.id.dat_mat_khau -> view.navigateTo(Sale_SreenContract.MenuScreen.DAT_MAT_KHAU)
            R.id.dang_xuat -> view.navigateTo(Sale_SreenContract.MenuScreen.DANG_XUAT)
        }
    }
}

