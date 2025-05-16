// ThemMonPresenter.kt
package com.example.appquanly.AddDish

import com.example.appquanly.ThucDon.InventoryItem
import com.example.appquanly.ThucDon.InventoryItemRepository
import java.util.UUID

class ThemMonPresenter(private val view: ThemMonContract.View) : ThemMonContract.Presenter {
    override fun onColorButtonClicked() {
        view.showColorPickerDialog()
    }

    override fun onColorSelected(color: Int) {
        view.applySelectedColor(color)
    }

    override fun addInventoryItem(name: String, price: Float?, unit: String, color: String?, iconFileName: String?) {
        if (name.isEmpty()) {
            view.showError("Vui lòng nhập tên món")
            return
        }
        if (unit.isEmpty()) {
            view.showError("Vui lòng chọn đơn vị tính")
            return
        }

        val repository = InventoryItemRepository(view as android.content.Context)
        val item = InventoryItem(
            InventoryItemID = UUID.randomUUID().toString(),
            InventoryItemCode = null,
            InventoryItemName = name,
            InventoryItemType = 1,
            UnitlD = unit,
            Price = price,
            Description = null,
            Inactive = false,
            CreatedDate = System.currentTimeMillis().toString(),
            CreatedBy = "User",
            ModifiedBy = null,
            Color = color,
            IconFileName = iconFileName,
            UseCount = 0
        )

        val result = repository.insertInventoryItem(item)
        if (result != -1L) {
            view.showSuccess("Thêm món thành công")
        } else {
            view.showError("Lỗi khi thêm món")
        }
    }
}