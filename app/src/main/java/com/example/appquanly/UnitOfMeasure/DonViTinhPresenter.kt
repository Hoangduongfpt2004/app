package com.example.appquanly.UnitOfMeasure

class DonViTinhPresenter(private val view: DonViTinhContract.View) : DonViTinhContract.Presenter {

    private val donViTinhs = mutableListOf<DonViTinh>()

    override fun loadData() {
        // load từ db hoặc mặc định trống
        view.showList(donViTinhs)
    }

    override fun addDonViTinh(name: String) {
        donViTinhs.add(DonViTinh(name))
        view.updateList()
    }

    override fun editDonViTinh(position: Int, newName: String) {
        donViTinhs[position].name = newName
        view.updateList()
    }

    override fun selectItem(position: Int) {
        donViTinhs.forEachIndexed { index, item ->
            item.isSelected = index == position
        }
        view.updateList()
    }

    fun getItems(): List<DonViTinh> = donViTinhs
}
