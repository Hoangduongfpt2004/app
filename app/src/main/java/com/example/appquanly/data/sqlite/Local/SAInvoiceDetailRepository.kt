package com.example.appquanly.data.sqlite.Local

import android.content.ContentValues
import android.content.Context
import com.example.appquanly.data.sqlite.Entity.SAInvoiceDetail

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
        return list
    }

    fun getDetailsByRefID(refID: String): List<SAInvoiceDetail> {
        val list = mutableListOf<SAInvoiceDetail>()
        val db = DatabaseCopyHelper(context).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM SAInvoiceDetail WHERE RefD = ?", arrayOf(refID))

        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToDetail(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun insertDetail(detail: SAInvoiceDetail): Long {
        val db = DatabaseCopyHelper(context).writableDatabase
        return db.insert("SAInvoiceDetail", null, detailToContentValues(detail))
    }

    fun insertDetails(details: List<SAInvoiceDetail>) {
        val db = DatabaseCopyHelper(context).writableDatabase
        db.beginTransaction()
        try {
            for (detail in details) {
                db.insert("SAInvoiceDetail", null, detailToContentValues(detail))
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun deleteByRefID(refID: String): Int {
        val db = DatabaseCopyHelper(context).writableDatabase
        return db.delete("SAInvoiceDetail", "RefD = ?", arrayOf(refID))
    }

    fun updateDetail(detail: SAInvoiceDetail): Int {
        val db = DatabaseCopyHelper(context).writableDatabase
        return db.update(
            "SAInvoiceDetail",
            detailToContentValues(detail),
            "RefDetailID = ?",
            arrayOf(detail.RefDetailID)
        )
    }

    // Helper: convert Cursor to SAInvoiceDetail
    private fun cursorToDetail(cursor: android.database.Cursor): SAInvoiceDetail {
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

    // Helper: convert SAInvoiceDetail to ContentValues
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

    // Extension function to safely get nullable Long
    private fun android.database.Cursor.getLongOrNull(columnName: String): Long? {
        val index = getColumnIndex(columnName)
        return if (index != -1 && !isNull(index)) getLong(index) else null
    }
}
