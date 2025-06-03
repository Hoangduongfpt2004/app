package com.example.appquanly.data.sqlite.Local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.appquanly.data.sqlite.Entity.SAInvoiceDetail
import java.text.SimpleDateFormat
import java.util.*

class SAInvoiceDetailRepository(private val context: Context) {

    private val dbRead: SQLiteDatabase
        get() = DatabaseCopyHelper(context).readableDatabase

    private val dbWrite: SQLiteDatabase
        get() = DatabaseCopyHelper(context).writableDatabase

    private fun Cursor.getLongOrNull(columnName: String): Long? {
        val index = getColumnIndex(columnName)
        return if (index != -1 && !isNull(index)) getLong(index) else null
    }

    private fun cursorToDetail(cursor: Cursor): SAInvoiceDetail {
        val idxRefDetailID = cursor.getColumnIndexOrThrow("RefDetailID")
        val idxRefDetailType = cursor.getColumnIndexOrThrow("RefDetailType")
        val idxRefID = cursor.getColumnIndexOrThrow("RefID")
        val idxInventoryItemID = cursor.getColumnIndexOrThrow("InventoryItemID")
        val idxInventoryItemName = cursor.getColumnIndexOrThrow("InventoryItemName")
        val idxUnitID = cursor.getColumnIndexOrThrow("UnitID")
        val idxUnitName = cursor.getColumnIndexOrThrow("UnitName")
        val idxQuantity = cursor.getColumnIndexOrThrow("Quantity")
        val idxUnitPrice = cursor.getColumnIndexOrThrow("UnitPrice")
        val idxAmount = cursor.getColumnIndexOrThrow("Amount")
        val idxDescription = cursor.getColumnIndexOrThrow("Description")
        val idxSortOrder = cursor.getColumnIndexOrThrow("SortOrder")
        val idxCreatedDate = cursor.getColumnIndex("CreatedDate")
        val idxCreatedBy = cursor.getColumnIndexOrThrow("CreatedBy")
        val idxModifiedDate = cursor.getColumnIndex("ModifiedDate")
        val idxModifiedBy = cursor.getColumnIndexOrThrow("ModifiedBy")

        val quantityFloat = cursor.getFloat(idxQuantity)
        val unitPriceFloat = cursor.getFloat(idxUnitPrice)

        return SAInvoiceDetail(
            RefDetailID = cursor.getString(idxRefDetailID),
            RefDetailType = cursor.getInt(idxRefDetailType),
            RefID = cursor.getString(idxRefID),
            InventoryItemID = cursor.getString(idxInventoryItemID),
            InventoryItemName = cursor.getString(idxInventoryItemName),
            UnitID = cursor.getString(idxUnitID),
            UnitName = cursor.getString(idxUnitName),
            Quantity = quantityFloat,
            UnitPrice = unitPriceFloat,
            Amount = cursor.getFloat(idxAmount),
            Description = cursor.getString(idxDescription),
            SortOrder = cursor.getInt(idxSortOrder),
            CreatedDate = if (idxCreatedDate != -1 && !cursor.isNull(idxCreatedDate)) cursor.getLong(idxCreatedDate) else null,
            CreatedBy = cursor.getString(idxCreatedBy),
            ModifiedDate = if (idxModifiedDate != -1 && !cursor.isNull(idxModifiedDate)) cursor.getLong(idxModifiedDate) else null,
            ModifiedBy = cursor.getString(idxModifiedBy),
            quantity = quantityFloat.toInt(),
            price = unitPriceFloat.toDouble()
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
            put("CreatedDate", detail.CreatedDate ?: System.currentTimeMillis())
            put("CreatedBy", detail.CreatedBy)
            put("ModifiedDate", detail.ModifiedDate)
            put("ModifiedBy", detail.ModifiedBy)
        }
    }

    fun getAllDetails(): List<SAInvoiceDetail> {
        val list = mutableListOf<SAInvoiceDetail>()
        dbRead.use { db ->
            val cursor = db.rawQuery("SELECT * FROM SAInvoiceDetail", null)
            cursor.use {
                if (it.moveToFirst()) {
                    do {
                        list.add(cursorToDetail(it))
                    } while (it.moveToNext())
                }
            }
        }
        return list
    }

    fun getDetailsByRefID(refID: String): List<SAInvoiceDetail> {
        val list = mutableListOf<SAInvoiceDetail>()
        dbRead.use { db ->
            val cursor = db.rawQuery("SELECT * FROM SAInvoiceDetail WHERE RefID = ?", arrayOf(refID))
            cursor.use {
                if (it.moveToFirst()) {
                    do {
                        list.add(cursorToDetail(it))
                    } while (it.moveToNext())
                }
            }
        }
        return list
    }

    fun insertDetail(detail: SAInvoiceDetail): Long {
        dbWrite.use { db ->
            val detailWithUniqueID = detail.copy(
                RefDetailID = UUID.randomUUID().toString(),
                Amount = detail.Quantity * detail.UnitPrice // Đảm bảo Amount đúng
            )
            Log.d("InsertDetail", "Inserting RefDetailID = ${detailWithUniqueID.RefDetailID}, Amount = ${detailWithUniqueID.Amount}")
            return db.insert("SAInvoiceDetail", null, detailToContentValues(detailWithUniqueID))
        }
    }

    fun insertDetails(details: List<SAInvoiceDetail>) {
        dbWrite.use { db ->
            db.beginTransaction()
            try {
                for (detail in details) {
                    val detailWithID = detail.copy(
                        RefDetailID = UUID.randomUUID().toString(),
                        Amount = detail.Quantity * detail.UnitPrice
                    )
                    db.insert("SAInvoiceDetail", null, detailToContentValues(detailWithID))
                }
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
    }

    fun deleteByRefID(refID: String): Int {
        dbWrite.use { db ->
            return db.delete("SAInvoiceDetail", "RefID = ?", arrayOf(refID))
        }
    }



    fun updateDetail(detail: SAInvoiceDetail): Int {
        dbWrite.use { db ->
            return db.update(
                "SAInvoiceDetail",
                detailToContentValues(detail),
                "RefDetailID = ?",
                arrayOf(detail.RefDetailID)
            )
        }
    }

    fun getDetailsBetween(startTime: Long, endTime: Long): List<SAInvoiceDetail> {
        val list = mutableListOf<SAInvoiceDetail>()
        dbRead.use { db ->
            val cursor = db.rawQuery(
                "SELECT * FROM SAInvoiceDetail WHERE CreatedDate BETWEEN ? AND ?",
                arrayOf(startTime.toString(), endTime.toString())
            )
            cursor.use {
                if (it.moveToFirst()) {
                    do {
                        list.add(cursorToDetail(it))
                    } while (it.moveToNext())
                }
            }
        }
        return list
    }

    fun getGroupedProductDetails(startTime: Long, endTime: Long): List<Triple<String, Float, Float>> {
        val data = mutableListOf<Triple<String, Float, Float>>()
        dbRead.use { db ->
            val cursor = db.rawQuery(
                """
                SELECT InventoryItemName, SUM(Quantity) as total_quantity, SUM(Amount) as total_amount
                FROM SAInvoiceDetail
                WHERE CreatedDate BETWEEN ? AND ?
                GROUP BY InventoryItemName
                """,
                arrayOf(startTime.toString(), endTime.toString())
            )
            cursor.use {
                if (it.moveToFirst()) {
                    do {
                        val productName = it.getString(it.getColumnIndexOrThrow("InventoryItemName"))
                        val totalQuantity = it.getFloat(it.getColumnIndexOrThrow("total_quantity"))
                        val totalAmount = it.getFloat(it.getColumnIndexOrThrow("total_amount"))
                        data.add(Triple(productName, totalQuantity, totalAmount))
                        Log.d("SAInvoiceDetailRepo", "Grouped: $productName, Quantity: $totalQuantity, Amount: $totalAmount")
                    } while (it.moveToNext())
                }
            }
        }
        return data
    }

    fun getPieChartData(startTime: Long, endTime: Long): List<Pair<String, Float>> {
        val data = mutableListOf<Pair<String, Float>>()
        dbRead.use { db ->
            val cursor = db.rawQuery(
                """
                SELECT InventoryItemName, SUM(Amount) as total_amount
                FROM SAInvoiceDetail
                WHERE CreatedDate BETWEEN ? AND ?
                GROUP BY InventoryItemName
                """,
                arrayOf(startTime.toString(), endTime.toString())
            )
            cursor.use {
                if (it.moveToFirst()) {
                    do {
                        val productName = it.getString(it.getColumnIndexOrThrow("InventoryItemName"))
                        val totalAmount = it.getFloat(it.getColumnIndexOrThrow("total_amount"))
                        data.add(Pair(productName, totalAmount))
                        Log.d("SAInvoiceDetailRepo", "PieChart: $productName, Amount: $totalAmount")
                    } while (it.moveToNext())
                }
            }
        }
        return data
    }

    fun getTotalAmountBetween(startTime: Long, endTime: Long): Double {
        var total = 0.0
        Log.d("SAInvoiceDetailRepo", "Query total amount between $startTime and $endTime")
        dbRead.use { db ->
            val cursor = db.rawQuery(
                "SELECT SUM(Amount) as Total FROM SAInvoiceDetail WHERE CreatedDate BETWEEN ? AND ?",
                arrayOf(startTime.toString(), endTime.toString())
            )
            cursor.use {
                if (it.moveToFirst()) {
                    val totalIndex = it.getColumnIndexOrThrow("Total")
                    total = if (!it.isNull(totalIndex)) it.getDouble(totalIndex) else 0.0
                }
            }
        }
        Log.d("SAInvoiceDetailRepo", "Total amount: $total")
        return total
    }

    fun getLineChartData(timeType: String): List<Pair<String, Float>> {
        val data = mutableListOf<Pair<String, Float>>()
        dbRead.use { db ->
            val query: String
            val selectionArgs: Array<String>
            val calendar = Calendar.getInstance()

            when (timeType) {
                "week" -> {
                    calendar.firstDayOfWeek = Calendar.MONDAY
                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    val startOfWeek = calendar.timeInMillis

                    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
                    calendar.set(Calendar.HOUR_OF_DAY, 23)
                    calendar.set(Calendar.MINUTE, 59)
                    calendar.set(Calendar.SECOND, 59)
                    calendar.set(Calendar.MILLISECOND, 999)
                    val endOfWeek = calendar.timeInMillis

                    query = """
                    SELECT strftime('%Y-%m-%d', CreatedDate / 1000, 'unixepoch') as period, 
                           SUM(Amount) as total_amount
                    FROM SAInvoiceDetail
                    WHERE CreatedDate BETWEEN ? AND ?
                    GROUP BY period
                    ORDER BY period
                """
                    selectionArgs = arrayOf(startOfWeek.toString(), endOfWeek.toString())
                }
                "month" -> {
                    calendar.set(Calendar.DAY_OF_MONTH, 1)
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    val startOfMonth = calendar.timeInMillis

                    calendar.add(Calendar.MONTH, 1)
                    calendar.add(Calendar.MILLISECOND, -1)
                    val endOfMonth = calendar.timeInMillis

                    query = """
                    SELECT strftime('%Y-%m-%d', CreatedDate / 1000, 'unixepoch') as period, 
                           SUM(Amount) as total_amount
                    FROM SAInvoiceDetail
                    WHERE CreatedDate BETWEEN ? AND ?
                    GROUP BY period
                    ORDER BY period
                """
                    selectionArgs = arrayOf(startOfMonth.toString(), endOfMonth.toString())
                }
                else -> { // year
                    calendar.set(Calendar.DAY_OF_YEAR, 1)
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    val startOfYear = calendar.timeInMillis

                    calendar.add(Calendar.YEAR, 1)
                    calendar.add(Calendar.MILLISECOND, -1)
                    val endOfYear = calendar.timeInMillis

                    query = """
                    SELECT strftime('%Y-%m', CreatedDate / 1000, 'unixepoch') as period, 
                           SUM(Amount) as total_amount
                    FROM SAInvoiceDetail
                    WHERE CreatedDate BETWEEN ? AND ?
                    GROUP BY period
                    ORDER BY period
                """
                    selectionArgs = arrayOf(startOfYear.toString(), endOfYear.toString())
                }
            }

            val monthData = mutableMapOf<Int, Float>()
            dbRead.use { db ->
                val cursor = db.rawQuery(query, selectionArgs)
                cursor.use {
                    if (it.moveToFirst()) {
                        do {
                            val period = it.getString(it.getColumnIndexOrThrow("period"))
                            val totalAmount = it.getFloat(it.getColumnIndexOrThrow("total_amount"))
                            val month = period.split("-")[1].toInt()
                            monthData[month] = totalAmount
                            Log.d("SAInvoiceDetailRepo", "LineChart: Month $month, Amount: $totalAmount")
                        } while (it.moveToNext())
                    }
                }
            }

            // Điền dữ liệu cho tất cả các tháng từ 1 đến tháng hiện tại
            if (timeType == "year") {
                val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1 // Tháng 6
                for (month in 1..currentMonth) {
                    val amount = monthData[month] ?: 0f
                    data.add(Pair("Tháng $month", amount))
                }
                // Thêm các tháng sau tháng hiện tại nếu có dữ liệu
                for (month in (currentMonth + 1)..12) {
                    if (monthData.containsKey(month)) {
                        data.add(Pair("Tháng $month", monthData[month]!!))
                    }
                }
            } else {
                dbRead.use { db ->
                    val cursor = db.rawQuery(query, selectionArgs)
                    cursor.use {
                        if (it.moveToFirst()) {
                            do {
                                val period = it.getString(it.getColumnIndexOrThrow("period"))
                                val totalAmount = it.getFloat(it.getColumnIndexOrThrow("total_amount"))
                                data.add(Pair(period, totalAmount))
                                Log.d("SAInvoiceDetailRepo", "LineChart: $period, Amount: $totalAmount")
                            } while (it.moveToNext())
                        }
                    }
                }
            }
        }
        return data
    }



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

    fun getTotalAmountYesterday(): Double {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val startOfYesterday = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfYesterday = calendar.timeInMillis - 1

        return getTotalAmountBetween(startOfYesterday, endOfYesterday)
    }


    fun getTotalAmountThisWeek(): Double {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfWeek = calendar.timeInMillis
        val endOfWeek = startOfWeek + 7 * 24 * 60 * 60 * 1000 - 1
        return getTotalAmountBetween(startOfWeek, endOfWeek)
    }

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


}