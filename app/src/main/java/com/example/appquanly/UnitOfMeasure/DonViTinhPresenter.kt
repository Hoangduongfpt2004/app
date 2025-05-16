package com.example.appquanly.UnitOfMeasure

class DonViTinhPresenter(
    private val view: DonViTinhContract.View,
    private val repository: UnitRepository
) : DonViTinhContract.Presenter {

    private val donViTinhs = mutableListOf<DonViTinh>()

    override fun loadData() {
        val list = repository.getAllUnit().map {
            DonViTinh(it.UnitID, it.UnitName)
        }
        donViTinhs.clear()
        donViTinhs.addAll(list)
        view.showList(donViTinhs)
    }

    override fun addDonViTinh(name: String) {
        val item = DonViTinh(name = name)
        donViTinhs.add(item)
        repository.addDonViTinh(item)
        view.updateList()
    }

    override fun editDonViTinh(position: Int, newName: String) {
        val item = donViTinhs[position]
        item.name = newName
        repository.updateDonViTinh(item)
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
