package com.example.appquanly.data.sqlite.Local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.example.appquanly.data.sqlite.Entity.SAInvoiceDetail
import java.util.UUID

class SAInvoiceDetailRepository(private val context: Context) {

    fun getAllDetails(): List<SAInvoiceDetail> {
        val list = mutableListOf<SAInvoiceDetail>()
        val db = DatabaseCopyHelper(context).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM SAInvoiceDetail", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToDetail(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    fun getDetailsByRefID(refID: String): List<SAInvoiceDetail> {
        val list = mutableListOf<SAInvoiceDetail>()
        val db = DatabaseCopyHelper(context).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM SAInvoiceDetail WHERE RefID = ?", arrayOf(refID))
        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToDetail(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    fun insertDetail(detail: SAInvoiceDetail): Long {
        val db = DatabaseCopyHelper(context).writableDatabase
        // ⚠️ Đảm bảo RefDetailID không bị trùng
        val detailWithUniqueID = detail.copy(
            RefDetailID = UUID.randomUUID().toString()
        )
        Log.d("InsertDetail", "Inserting RefDetailID = ${detailWithUniqueID.RefDetailID}")
        val result = db.insert("SAInvoiceDetail", null, detailToContentValues(detailWithUniqueID))
        db.close()
        return result
    }

    fun insertDetails(details: List<SAInvoiceDetail>) {
        val db = DatabaseCopyHelper(context).writableDatabase
        db.beginTransaction()
        try {
            for (detail in details) {
                // ⚠️ Luôn tạo mới RefDetailID cho từng detail
                val detailWithID = detail.copy(
                    RefDetailID = UUID.randomUUID().toString()
                )
                Log.d("InsertDetail", "Bulk insert RefDetailID = ${detailWithID.RefDetailID}")
                db.insert("SAInvoiceDetail", null, detailToContentValues(detailWithID))
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun deleteByRefID(refID: String): Int {
        val db = DatabaseCopyHelper(context).writableDatabase
        val result = db.delete("SAInvoiceDetail", "RefID = ?", arrayOf(refID))
        db.close()
        return result
    }

    fun updateDetail(detail: SAInvoiceDetail): Int {
        val db = DatabaseCopyHelper(context).writableDatabase
        val result = db.update(
            "SAInvoiceDetail",
            detailToContentValues(detail),
            "RefDetailID = ?",
            arrayOf(detail.RefDetailID)
        )
        db.close()
        return result
    }

    private fun cursorToDetail(cursor: Cursor): SAInvoiceDetail {
        return SAInvoiceDetail(
            RefDetailID = cursor.getString(cursor.getColumnIndexOrThrow("RefDetailID")),
            RefDetailType = cursor.getInt(cursor.getColumnIndexOrThrow("RefDetailType")),
            RefID = cursor.getString(cursor.getColumnIndexOrThrow("RefID")),
            InventoryItemID = cursor.getString(cursor.getColumnIndexOrThrow("InventoryItemID")),
            InventoryItemName = cursor.getString(cursor.getColumnIndexOrThrow("InventoryItemName")),
            UnitID = cursor.getString(cursor.getColumnIndexOrThrow("UnitID")),
            UnitName = cursor.getString(cursor.getColumnIndexOrThrow("UnitName")),
            Quantity = cursor.getFloat(cursor.getColumnIndexOrThrow("Quantity")),
            UnitPrice = cursor.getFloat(cursor.getColumnIndexOrThrow("UnitPrice")),
            Amount = cursor.getFloat(cursor.getColumnIndexOrThrow("Amount")),
            Description = cursor.getString(cursor.getColumnIndexOrThrow("Description")),
            SortOrder = cursor.getInt(cursor.getColumnIndexOrThrow("SortOrder")),
            CreatedDate = cursor.getLongOrNull("CreatedDate"),
            CreatedBy = cursor.getString(cursor.getColumnIndexOrThrow("CreatedBy")),
            ModifiedDate = cursor.getLongOrNull("ModifiedDate"),
            ModifiedBy = cursor.getString(cursor.getColumnIndexOrThrow("ModifiedBy"))
        )
    }

    private fun detailToContentValues(detail: SAInvoiceDetail): ContentValues {
        return ContentValues().apply {
            put("RefDetailID", detail.RefDetailID)
            put("RefDetailType", detail.RefDetailType)
            put("RefID", detail.RefID)
            put("InventoryItemID", detail.InventoryItemID)
            put("InventoryItemName", detail.InventoryItemName)
            put("UnitID", detail.UnitID)
            put("UnitName", detail.UnitName)
            put("Quantity", detail.Quantity)
            put("UnitPrice", detail.UnitPrice)
            put("Amount", detail.Amount)
            put("Description", detail.Description)
            put("SortOrder", detail.SortOrder)
            put("CreatedDate", detail.CreatedDate)
            put("CreatedBy", detail.CreatedBy)
            put("ModifiedDate", detail.ModifiedDate)
            put("ModifiedBy", detail.ModifiedBy)
        }
    }

    // Extension function để lấy Long? từ Cursor một cách an toàn
    private fun Cursor.getLongOrNull(columnName: String): Long? {
        val index = getColumnIndex(columnName)
        return if (index != -1 && !isNull(index)) getLong(index) else null
    }
}
