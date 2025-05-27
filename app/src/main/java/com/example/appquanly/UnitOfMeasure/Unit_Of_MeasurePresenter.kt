package com.example.appquanly.UnitOfMeasure

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.appquanly.data.sqlite.Entity.UnitOfMeasure
import com.example.appquanly.data.sqlite.Local.UnitRepository
import java.time.LocalDateTime
import java.util.UUID

class Unit_Of_MeasurePresenter(
    private val view: Unit_Of_MeasureContract.View,
    private val repository: UnitRepository
) : Unit_Of_MeasureContract.Presenter {

    private val units = mutableListOf<UnitOfMeasure>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun loadData() {
        units.clear()
        units.addAll(repository.getAllUnit())
        view.showList(units)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun addDonViTinh(name: String) {
        val now = LocalDateTime.now()
        val newUnit = UnitOfMeasure(
            UnitID = UUID.randomUUID().toString(),
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
        view.addItemToList(newUnit)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun updateUnit(unit: UnitOfMeasure) {
        unit.ModifiedDate = LocalDateTime.now()
        unit.ModifiedBy = "admin"
        repository.updateDonViTinh(unit)
        view.updateList()
    }

    override fun selectItem(selectedUnit: UnitOfMeasure) {
        units.forEach { it.Inactive = (it.UnitID == selectedUnit.UnitID) }
        view.updateList()
    }
}
