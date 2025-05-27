package com.example.appquanly.UnitOfMeasure

import com.example.appquanly.data.sqlite.Entity.UnitOfMeasure

interface Unit_Of_MeasureContract {
    interface View {
        fun showList(units: List<UnitOfMeasure>)
        fun updateList()
        fun addItemToList(unit: UnitOfMeasure)
    }

    interface Presenter {
        fun loadData()
        fun addDonViTinh(name: String)
        fun updateUnit(unit: UnitOfMeasure)
        fun selectItem(selectedUnit: UnitOfMeasure)
    }
}
