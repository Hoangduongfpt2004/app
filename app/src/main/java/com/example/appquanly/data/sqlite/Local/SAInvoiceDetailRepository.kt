package com.example.appquanly.data.sqlite.Local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.appquanly.data.sqlite.Entity.SAInvoiceDetail
import java.util.Calendar
import java.util.UUID

class SAInvoiceDetailRepository(private val context: Context) {

    // Mở db đọc
    private val db: SQLiteDatabase
        get() = DatabaseCopyHelper(context).readableDatabase

    // Lấy tất cả chi tiết hóa đơn
    fun getAllDetails(): List<SAInvoiceDetail> {
        val list = mutableListOf<SAInvoiceDetail>()
        val cursor = db.rawQuery("SELECT * FROM SAInvoiceDetail", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToDetail(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // Lấy chi tiết theo RefID hóa đơn
    fun getDetailsByRefID(refID: String): List<SAInvoiceDetail> {
        val list = mutableListOf<SAInvoiceDetail>()
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

    // Thêm 1 chi tiết hóa đơn (tự tạo RefDetailID mới)
    fun insertDetail(detail: SAInvoiceDetail): Long {
        val dbWrite = DatabaseCopyHelper(context).writableDatabase
        val detailWithUniqueID = detail.copy(
            RefDetailID = UUID.randomUUID().toString()
        )
        Log.d("InsertDetail", "Inserting RefDetailID = ${detailWithUniqueID.RefDetailID}")
        val result = dbWrite.insert("SAInvoiceDetail", null, detailToContentValues(detailWithUniqueID))
        dbWrite.close()
        return result
    }

    // Thêm nhiều chi tiết cùng lúc (transaction)
    fun insertDetails(details: List<SAInvoiceDetail>) {
        val dbWrite = DatabaseCopyHelper(context).writableDatabase
        dbWrite.beginTransaction()
        try {
            for (detail in details) {
                val detailWithID = detail.copy(
                    RefDetailID = UUID.randomUUID().toString()
                )
                Log.d("InsertDetail", "Bulk insert RefDetailID = ${detailWithID.RefDetailID}")
                dbWrite.insert("SAInvoiceDetail", null, detailToContentValues(detailWithID))
            }
            dbWrite.setTransactionSuccessful()
        } finally {
            dbWrite.endTransaction()
            dbWrite.close()
        }
    }

    // Xóa chi tiết theo RefID
    fun deleteByRefID(refID: String): Int {
        val dbWrite = DatabaseCopyHelper(context).writableDatabase
        val result = dbWrite.delete("SAInvoiceDetail", "RefID = ?", arrayOf(refID))
        dbWrite.close()
        return result
    }

    // Cập nhật chi tiết theo RefDetailID
    fun updateDetail(detail: SAInvoiceDetail): Int {
        val dbWrite = DatabaseCopyHelper(context).writableDatabase
        val result = dbWrite.update(
            "SAInvoiceDetail",
            detailToContentValues(detail),
            "RefDetailID = ?",
            arrayOf(detail.RefDetailID)
        )
        dbWrite.close()
        return result
    }

    // Chuyển cursor sang đối tượng SAInvoiceDetail
    private fun cursorToDetail(cursor: Cursor): SAInvoiceDetail {
        val soLuong = cursor.getFloat(cursor.getColumnIndexOrThrow("Quantity"))
        val donGia = cursor.getFloat(cursor.getColumnIndexOrThrow("UnitPrice"))
        return SAInvoiceDetail(
            RefDetailID = cursor.getString(cursor.getColumnIndexOrThrow("RefDetailID")),
            RefDetailType = cursor.getInt(cursor.getColumnIndexOrThrow("RefDetailType")),
            RefID = cursor.getString(cursor.getColumnIndexOrThrow("RefID")),
            InventoryItemID = cursor.getString(cursor.getColumnIndexOrThrow("InventoryItemID")),
            InventoryItemName = cursor.getString(cursor.getColumnIndexOrThrow("InventoryItemName")),
            UnitID = cursor.getString(cursor.getColumnIndexOrThrow("UnitID")),
            UnitName = cursor.getString(cursor.getColumnIndexOrThrow("UnitName")),
            Quantity = soLuong,
            UnitPrice = donGia,
            Amount = cursor.getFloat(cursor.getColumnIndexOrThrow("Amount")),
            Description = cursor.getString(cursor.getColumnIndexOrThrow("Description")),
            SortOrder = cursor.getInt(cursor.getColumnIndexOrThrow("SortOrder")),
            CreatedDate = cursor.getLongOrNull("CreatedDate"),
            CreatedBy = cursor.getString(cursor.getColumnIndexOrThrow("CreatedBy")),
            ModifiedDate = cursor.getLongOrNull("ModifiedDate"),
            ModifiedBy = cursor.getString(cursor.getColumnIndexOrThrow("ModifiedBy")),
            quantity = soLuong.toInt(),
            price = donGia.toDouble()
        )
    }

    // Chuyển đối tượng SAInvoiceDetail sang ContentValues để ghi db
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

    // Extension function để lấy Long? an toàn khi cột có thể null
    private fun Cursor.getLongOrNull(columnName: String): Long? {
        val index = getColumnIndex(columnName)
        return if (index != -1 && !isNull(index)) getLong(index) else null
    }


    // -------------- Các hàm tính tổng doanh thu theo khoảng thời gian --------------

    // Hàm tính tổng Amount theo khoảng thời gian (timestamp millis bắt đầu và kết thúc)
    private fun getTotalAmountBetween(startTime: Long, endTime: Long): Double {
        val cursor = db.rawQuery(
            "SELECT SUM(Amount) as Total FROM SAInvoiceDetail WHERE CreatedDate BETWEEN ? AND ?",
            arrayOf(startTime.toString(), endTime.toString())
        )
        var total = 0.0
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndexOrThrow("Total"))
        }
        cursor.close()
        return total
    }

    // Tổng doanh thu hôm nay
    fun getTotalAmountToday(): Double {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis
        val endOfDay = startOfDay + 24 * 60 * 60 * 1000 - 1
        return getTotalAmountBetween(startOfDay, endOfDay)
    }

    // Tổng doanh thu hôm qua
    fun getTotalAmountYesterday(): Double {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfYesterday = calendar.timeInMillis
        val endOfYesterday = startOfYesterday + 24 * 60 * 60 * 1000 - 1
        return getTotalAmountBetween(startOfYesterday, endOfYesterday)
    }

    // Tổng doanh thu tuần này (tính từ thứ 2 đầu tuần)
    fun getTotalAmountThisWeek(): Double {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val startOfWeek = calendar.timeInMillis
        val endOfWeek = startOfWeek + 7 * 24 * 60 * 60 * 1000 - 1
        return getTotalAmountBetween(startOfWeek, endOfWeek)
    }

    // Tổng doanh thu tháng này
    fun getTotalAmountThisMonth(): Double {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.timeInMillis
        calendar.add(Calendar.MONTH, 1)
        val endOfMonth = calendar.timeInMillis - 1
        return getTotalAmountBetween(startOfMonth, endOfMonth)
    }

    // Tổng doanh thu năm nay
    fun getTotalAmountThisYear(): Double {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_YEAR, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfYear = calendar.timeInMillis
        calendar.add(Calendar.YEAR, 1)
        val endOfYear = calendar.timeInMillis - 1
        return getTotalAmountBetween(startOfYear, endOfYear)
    }

    fun saveInvoiceDetailsToDatabase(details: List<SAInvoiceDetail>) {
        val repository = SAInvoiceDetailRepository(context)
        repository.insertDetails(details)
    }

    fun getDetailsBetween(startTime: Long, endTime: Long): List<SAInvoiceDetail> {
        val list = mutableListOf<SAInvoiceDetail>()
        val cursor = db.rawQuery(
            "SELECT * FROM SAInvoiceDetail WHERE CreatedDate BETWEEN ? AND ?",
            arrayOf(startTime.toString(), endTime.toString())
        )
        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToDetail(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }






}
