package com.example.appquanly.UnitOfMeasure

interface DonViTinhContract {
    interface View {
        fun showList(donViTinhs: List<DonViTinh>)
        fun updateList()
    }

    interface Presenter {
        fun loadData()
        fun addDonViTinh(name: String)
        fun editDonViTinh(position: Int, newName: String)
        fun selectItem(position: Int)
    }
}
