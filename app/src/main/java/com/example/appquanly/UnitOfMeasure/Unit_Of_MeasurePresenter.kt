package com.example.appquanly.UnitOfMeasure

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.appquanly.data.sqlite.Entity.UnitOfMeasure
import com.example.appquanly.data.sqlite.Local.UnitRepository
import java.time.LocalDateTime

class Unit_Of_MeasurePresenter(
    private val view: Unit_Of_MeasureContract.View,
    private val repository: UnitRepository
) : Unit_Of_MeasureContract.Presenter {

    private val units = mutableListOf<UnitOfMeasure>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun loadData() {
        val list = repository.getAllUnit()
        units.clear()
        units.addAll(list)
        view.showList(units)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun addDonViTinh(name: String) {
        val now = LocalDateTime.now()
        val newUnit = UnitOfMeasure(
            UnitID = java.util.UUID.randomUUID().toString(), // tạo id ngẫu nhiên
            UnitName = name,
            Description = "",
            Inactive = false,
            CreatedBy = "admin",
            CreatedDate = now,
            ModifiedDate = now,
            ModifiedBy = "admin"
        )
        repository.addDonViTinh(newUnit)
        units.add(newUnit)
        view.updateList()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun updateUnit(unit: UnitOfMeasure) {
        unit.ModifiedDate = LocalDateTime.now()
        unit.ModifiedBy = "admin"
        repository.updateDonViTinh(unit)
        view.updateList()
    }

    override fun selectItem(selectedUnit: UnitOfMeasure) {
        units.forEach { it.isSelected = (it.UnitID == selectedUnit.UnitID) }
        view.updateList()
    }
}

