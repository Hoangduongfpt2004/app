package com.example.appquanly.Sale

import com.example.appquanly.R


class BanHangPresenter(private val view: BanHangContract.View) : BanHangContract.Presenter {

    override fun onHintClicked() {
        view.showMenu()
    }

    override fun onAddButtonClicked() {
        view.openOrderScreen()
    }

    override fun onMenuItemClicked(itemId: Int) {
        when (itemId) {
            R.id.ban_hang -> view.navigateTo(BanHangContract.MenuScreen.BAN_HANG)
            R.id.thuc_don -> view.navigateTo(BanHangContract.MenuScreen.THUC_DON)
            R.id.bao_cao -> view.navigateTo(BanHangContract.MenuScreen.BAO_CAO)
            R.id.dong_bo_du_lieu -> view.navigateTo(BanHangContract.MenuScreen.DONG_BO_DU_LIEU)
            R.id.thiet_lap -> view.navigateTo(BanHangContract.MenuScreen.THIET_LAP)
            R.id.lien_ket_tai_khoan -> view.navigateTo(BanHangContract.MenuScreen.LIEN_KET_TAI_KHOAN)
            R.id.thong_bao -> view.navigateTo(BanHangContract.MenuScreen.THONG_BAO)
            R.id.gioi_thieu -> view.navigateTo(BanHangContract.MenuScreen.GIOI_THIEU_CHO_BAN)
            R.id.danh_gia_ung_dung -> view.navigateTo(BanHangContract.MenuScreen.DANH_GIA_UNG_DUNG)
            R.id.gop_y -> view.navigateTo(BanHangContract.MenuScreen.GOP_Y_VOI_NHA_PHAT_TRIEN)
            R.id.thong_tin_san_pham -> view.navigateTo(BanHangContract.MenuScreen.THONG_TIN_SAN_PHAM)
            R.id.dat_mat_khau -> view.navigateTo(BanHangContract.MenuScreen.DAT_MAT_KHAU)
            R.id.dang_xuat -> view.navigateTo(BanHangContract.MenuScreen.DANG_XUAT)
        }
    }
}

