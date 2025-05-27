package com.example.appquanly.AddDish

import android.content.Context
import com.example.appquanly.data.sqlite.Entity.InventoryItem
import com.example.appquanly.data.sqlite.Local.InventoryItemRepository
import java.util.UUID

class Add_DishPersenter(private val view: Add_DishContract.View) : Add_DishContract.Presenter {

    private val repository = InventoryItemRepository(view as Context)

    override fun onColorButtonClicked() {
        view.showColorPickerDialog()
    }

    override fun onColorSelected(color: Int) {
        view.applySelectedColor(color)
    }

    override fun addInventoryItem(
        name: String,
        price: Float?,
        unit: String,
        color: String?,
        iconFileName: String?,
        isInactive: Boolean
    ) {
        if (name.isEmpty()) {
            view.showError("Vui lòng nhập tên món")
            return
        }
        if (unit.isEmpty()) {
            view.showError("Vui lòng chọn đơn vị tính")
            return
        }

        val item = InventoryItem(
            InventoryItemID = UUID.randomUUID().toString(),
            InventoryItemCode = null,
            InventoryItemName = name,
            InventoryItemType = 1,
            UnitID = unit,
            Price = price ?: 0f,
            Description = null,
            Inactive = isInactive,
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


    override fun loadInventoryItemById(itemId: String) {
        val item = repository.getInventoryItemById(itemId)
        if (item != null) {
            view.showInventoryItemToEdit(item)
        } else {
            view.showError("Không tìm thấy món")
        }
    }

    override fun updateInventoryItem(
        id: String,
        name: String,
        price: Float?,
        unit: String,
        color: String?,
        iconFileName: String?,
        isInactive: Boolean
    ) {
        if (name.isEmpty()) {
            view.showError("Vui lòng nhập tên món")
            return
        }
        if (unit.isEmpty()) {
            view.showError("Vui lòng chọn đơn vị tính")
            return
        }

        val oldItem = repository.getInventoryItemById(id)
        if (oldItem == null) {
            view.showError("Món cần cập nhật không tồn tại")
            return
        }

        val item = InventoryItem(
            InventoryItemID = id,
            InventoryItemCode = oldItem.InventoryItemCode,
            InventoryItemName = name,
            InventoryItemType = oldItem.InventoryItemType,
            UnitID = unit,
            Price = price ?: 0f,
            Description = oldItem.Description,
            Inactive = isInactive,
            CreatedDate = oldItem.CreatedDate,
            CreatedBy = oldItem.CreatedBy,
            ModifiedBy = "User",
            Color = color,
            IconFileName = iconFileName,
            UseCount = oldItem.UseCount
        )

        val result = repository.updateInventoryItem(item)
        if (result > 0) {
            view.showSuccess("Cập nhật món thành công")
        } else {
            view.showError("Không thể cập nhật món")
        }
    }


    override fun deleteInventoryItem(id: String) {
        val result = repository.deleteInventoryItem(id)
        if (result > 0) {
            view.showSuccess("Xóa món thành công")
        } else {
            view.showError("Không thể xóa món")
        }
    }
}
