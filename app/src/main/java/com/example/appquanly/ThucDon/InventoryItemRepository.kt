package com.example.appquanly.ThucDon

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.appquanly.data.local.DatabaseCopyHelper

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
                    UnitlD = cursor.getString(cursor.getColumnIndexOrThrow("UnitID")),
                    Price = cursor.getFloat(cursor.getColumnIndexOrThrow("Price")),
                    Description = cursor.getString(cursor.getColumnIndexOrThrow("Description")),
                    Inactive = cursor.getInt(cursor.getColumnIndexOrThrow("Inactive")) != 0,
                    CreatedDate = cursor.getString(cursor.getColumnIndexOrThrow("CreatedDate")) ,
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

    fun insertInventoryItem(item: InventoryItem): Long {
        val db = DatabaseCopyHelper(context).writableDatabase
        val values = ContentValues().apply {
            put("InventoryItemID", item.InventoryItemID)
            put("InventoryItemCode", item.InventoryItemCode)
            put("InventoryItemName", item.InventoryItemName)
            put("InventoryItemType", item.InventoryItemType)
            put("UnitID", item.UnitlD)
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
        val result = db.insert("InventoryItem", null, values)
        db.close()
        return result // Trả về ID của hàng vừa chèn, hoặc -1 nếu lỗi
    }

    private fun Cursor.getDoubleOrNull(index: Int): Double? =
        if (isNull(index)) null else getDouble(index)

    private fun Cursor.getIntOrNull(index: Int): Int? =
        if (isNull(index)) null else getInt(index)
}