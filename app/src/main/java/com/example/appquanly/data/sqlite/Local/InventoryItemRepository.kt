// InventoryItemRepository.kt
package com.example.appquanly.data.sqlite.Local

import android.content.ContentValues
import android.content.Context
import com.example.appquanly.data.sqlite.Entity.InventoryItem

class InventoryItemRepository(private val context: Context) {

    fun getAllInventoryItems(): List<InventoryItem> {
        val itemList = mutableListOf<InventoryItem>()
        val db = DatabaseCopyHelper(context).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM InventoryItem", null)

        if (cursor.moveToFirst()) {
            do {
                val item = InventoryItem(
                    InventoryItemID = cursor.getString(cursor.getColumnIndexOrThrow("InventoryItemID")),
                    InventoryItemCode = cursor.getString(cursor.getColumnIndexOrThrow("InventoryItemCode")),
                    InventoryItemName = cursor.getString(cursor.getColumnIndexOrThrow("InventoryItemName")),
                    InventoryItemType = cursor.getInt(cursor.getColumnIndexOrThrow("InventoryItemType")),
                    UnitID = cursor.getString(cursor.getColumnIndexOrThrow("UnitID")),
                    Price = cursor.getFloat(cursor.getColumnIndexOrThrow("Price")),
                    Description = cursor.getString(cursor.getColumnIndexOrThrow("Description")),
                    Inactive = cursor.getInt(cursor.getColumnIndexOrThrow("Inactive")) != 0,
                    CreatedDate = cursor.getString(cursor.getColumnIndexOrThrow("CreatedDate")),
                    CreatedBy = cursor.getString(cursor.getColumnIndexOrThrow("CreatedBy")),
                    ModifiedBy = cursor.getString(cursor.getColumnIndexOrThrow("ModifiedBy")),
                    Color = cursor.getString(cursor.getColumnIndexOrThrow("Color")),
                    IconFileName = cursor.getString(cursor.getColumnIndexOrThrow("IconFileName")),
                    UseCount = cursor.getInt(cursor.getColumnIndexOrThrow("UseCount"))
                )
                itemList.add(item)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return itemList
    }

    fun getInventoryItemById(id: String): InventoryItem? {
        val db = DatabaseCopyHelper(context).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM InventoryItem WHERE InventoryItemID = ?", arrayOf(id))

        var item: InventoryItem? = null
        if (cursor.moveToFirst()) {
            item = InventoryItem(
                InventoryItemID = cursor.getString(cursor.getColumnIndexOrThrow("InventoryItemID")),
                InventoryItemCode = cursor.getString(cursor.getColumnIndexOrThrow("InventoryItemCode")),
                InventoryItemName = cursor.getString(cursor.getColumnIndexOrThrow("InventoryItemName")),
                InventoryItemType = cursor.getInt(cursor.getColumnIndexOrThrow("InventoryItemType")),
                UnitID = cursor.getString(cursor.getColumnIndexOrThrow("UnitID")),
                Price = cursor.getFloat(cursor.getColumnIndexOrThrow("Price")),
                Description = cursor.getString(cursor.getColumnIndexOrThrow("Description")),
                Inactive = cursor.getInt(cursor.getColumnIndexOrThrow("Inactive")) != 0,
                CreatedDate = cursor.getString(cursor.getColumnIndexOrThrow("CreatedDate")),
                CreatedBy = cursor.getString(cursor.getColumnIndexOrThrow("CreatedBy")),
                ModifiedBy = cursor.getString(cursor.getColumnIndexOrThrow("ModifiedBy")),
                Color = cursor.getString(cursor.getColumnIndexOrThrow("Color")),
                IconFileName = cursor.getString(cursor.getColumnIndexOrThrow("IconFileName")),
                UseCount = cursor.getInt(cursor.getColumnIndexOrThrow("UseCount"))
            )
        }

        cursor.close()
        return item
    }

    fun insertInventoryItem(item: InventoryItem): Long {
        val db = DatabaseCopyHelper(context).writableDatabase
        val values = ContentValues().apply {
            put("InventoryItemID", item.InventoryItemID)
            put("InventoryItemCode", item.InventoryItemCode)
            put("InventoryItemName", item.InventoryItemName)
            put("InventoryItemType", item.InventoryItemType)
            put("UnitID", item.UnitID)
            put("Price", item.Price)
            put("Description", item.Description)
            put("Inactive", if (item.Inactive == true) 1 else 0)
            put("CreatedDate", item.CreatedDate)
            put("CreatedBy", item.CreatedBy)
            put("ModifiedBy", item.ModifiedBy)
            put("Color", item.Color)
            put("IconFileName", item.IconFileName)
            put("UseCount", item.UseCount)
        }

        return db.insert("InventoryItem", null, values)
    }

    fun updateInventoryItem(item: InventoryItem): Int {
        val db = DatabaseCopyHelper(context).writableDatabase
        val values = ContentValues().apply {
            put("InventoryItemName", item.InventoryItemName)
            put("InventoryItemType", item.InventoryItemType)
            put("UnitID", item.UnitID)
            put("Price", item.Price)
            put("Description", item.Description)
            put("Inactive", if (item.Inactive == true) 1 else 0)
            put("ModifiedBy", item.ModifiedBy)
            put("Color", item.Color)
            put("IconFileName", item.IconFileName)
            put("UseCount", item.UseCount)
        }

        return db.update(
            "InventoryItem",
            values,
            "InventoryItemID = ?",
            arrayOf(item.InventoryItemID)
        )
    }

    // Thêm hàm xóa món
    fun deleteInventoryItem(id: String): Int {
        val db = DatabaseCopyHelper(context).writableDatabase
        return db.delete("InventoryItem", "InventoryItemID = ?", arrayOf(id))
    }
}
