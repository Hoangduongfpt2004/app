package com.example.appquanly.data.sqlite.Local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.appquanly.data.sqlite.Entity.SAInvoiceDetail
import com.example.appquanly.data.sqlite.Entity.SAInvoiceItem

class SAInvoiceRepository(private val context: Context) {

    private val detailRepo = SAInvoiceDetailRepository(context)

    // Lấy tất cả hóa đơn
    fun getAllInvoices(): List<SAInvoiceItem> {
        val list = mutableListOf<SAInvoiceItem>()
        val db = DatabaseCopyHelper(context).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM SAInvoice", null)

        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToInvoice(cursor))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    // Lấy hóa đơn theo ID
    fun getInvoiceById(refId: String): SAInvoiceItem? {
        val db = DatabaseCopyHelper(context).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM SAInvoice WHERE RefID = ?", arrayOf(refId))
        var invoice: SAInvoiceItem? = null

        if (cursor.moveToFirst()) {
            invoice = cursorToInvoice(cursor)
        }

        cursor.close()
        db.close()
        return invoice
    }

    // Thêm hóa đơn mới
    fun insertInvoice(invoice: SAInvoiceItem): Boolean {
        val db = DatabaseCopyHelper(context).writableDatabase
        return try {
            // Kiểm tra xem RefID đã tồn tại chưa
            val cursor = db.query(
                "SAInvoice",                    // bảng
                arrayOf("RefID"),               // cột cần lấy
                "RefID = ?",                   // điều kiện WHERE
                arrayOf(invoice.refId),         // giá trị tham số
                null, null, null
            )
            val exists = cursor.count > 0
            cursor.close()

            if (exists) {
                // Nếu tồn tại rồi thì không insert, có thể trả về false hoặc true tuỳ ý
                db.close()
                false
            } else {
                // Nếu chưa tồn tại thì insert mới
                val values = invoiceToContentValues(invoice)
                val result = db.insert("SAInvoice", null, values)
                db.close()
                result != -1L
            }
        } catch (e: Exception) {
            e.printStackTrace()
            db.close()
            false
        }
    }



    fun deleteInvoiceById(refId: String): Int {
        val db = DatabaseCopyHelper(context).writableDatabase
        val result = db.delete("SAInvoice", "RefID = ?", arrayOf(refId))
        db.close()
        return result
    }

    // Lấy danh sách hóa đơn mới nhất (theo ngày giảm dần)
    fun getLatestInvoices(): List<SAInvoiceItem> {
        val db = DatabaseCopyHelper(context).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM SAInvoice ORDER BY RefDate DESC", null)
        val invoices = mutableListOf<SAInvoiceItem>()

        if (cursor.moveToFirst()) {
            do {
                invoices.add(cursorToInvoice(cursor))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return invoices
    }

    // Lấy hóa đơn mới nhất
    fun getLatestInvoice(): SAInvoiceItem? {
        val invoices = getLatestInvoices()
        return if (invoices.isNotEmpty()) invoices[0] else null
    }

    // Chuyển cursor thành đối tượng SAInvoiceItem
    private fun cursorToInvoice(cursor: Cursor): SAInvoiceItem {
        return SAInvoiceItem(
            refId = cursor.getString(cursor.getColumnIndexOrThrow("RefID")),
            refType = cursor.getInt(cursor.getColumnIndexOrThrow("RefType")),
            refNo = cursor.getString(cursor.getColumnIndexOrThrow("RefNo")),
            refDate = cursor.getLong(cursor.getColumnIndexOrThrow("RefDate")),
            amount = cursor.getDouble(cursor.getColumnIndexOrThrow("Amount")),
            returnAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("ReturnAmount")),
            receiveAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("ReceiveAmount")),
            remainAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("RemainAmount")),
            journalMemo = cursor.getString(cursor.getColumnIndexOrThrow("JournalMemo")),
            paymentStatus = cursor.getInt(cursor.getColumnIndexOrThrow("PaymentStatus")),
            numberOfPeople = cursor.getInt(cursor.getColumnIndexOrThrow("NumberOfPeople")),
            tableName = cursor.getString(cursor.getColumnIndexOrThrow("TableName")),
            listItemName = cursor.getString(cursor.getColumnIndexOrThrow("ListItemName")),
            createdDate = cursor.getLongOrNull("CreatedDate"),
            createdBy = cursor.getString(cursor.getColumnIndexOrThrow("CreatedBy")),
            modifiedDate = cursor.getLongOrNull("ModifiedDate"),
            modifiedBy = cursor.getString(cursor.getColumnIndexOrThrow("ModifiedBy"))
        )
    }

    // Chuyển SAInvoiceItem thành ContentValues
    private fun invoiceToContentValues(invoice: SAInvoiceItem): ContentValues {
        return ContentValues().apply {
            put("refID", invoice.refId)
            put("refType", invoice.refType)
            put("refNo", invoice.refNo)
            put("refDate", invoice.refDate)
            put("amount", invoice.amount)
            put("returnAmount", invoice.returnAmount)
            put("receiveAmount", invoice.receiveAmount)
            put("remainAmount", invoice.remainAmount)
            put("journalMemo", invoice.journalMemo)
            put("paymentStatus", invoice.paymentStatus)
            put("numberOfPeople", invoice.numberOfPeople)
            put("tableName", invoice.tableName)
            put("listItemName", invoice.listItemName)
            put("createdDate", invoice.createdDate)
            put("createdBy", invoice.createdBy)
            put("modifiedDate", invoice.modifiedDate)
            put("modifiedBy", invoice.modifiedBy)
        }
    }

    // Extension để lấy Long? từ Cursor
    private fun Cursor.getLongOrNull(columnName: String): Long? {
        val index = getColumnIndex(columnName)
        return if (index != -1 && !isNull(index)) getLong(index) else null
    }

    // --- DAO xử lý chi tiết hóa đơn thông qua SAInvoiceDetailRepository ---
    fun insertInvoiceDetail(detail: SAInvoiceDetail): Boolean {
        return detailRepo.insertDetail(detail) != -1L
    }

    fun insertInvoiceItems(details: List<SAInvoiceDetail>) {
        detailRepo.insertDetails(details)
    }

    fun getDetailsByRefID(refID: String): List<SAInvoiceDetail> {
        return detailRepo.getDetailsByRefID(refID)
    }

    fun deleteInvoiceDetails(refID: String): Int {
        return detailRepo.deleteByRefID(refID)
    }

    fun updateInvoiceDetail(detail: SAInvoiceDetail): Int {
        return detailRepo.updateDetail(detail)
    }

    fun getDetailsOfLatestInvoice(): List<SAInvoiceDetail> {
        val latestInvoice = getLatestInvoice() ?: return emptyList()
        return detailRepo.getDetailsByRefID(latestInvoice.refId)
    }

    // ✅ Thêm DAO như bạn yêu cầu
    fun insertIntoSAInvoiceItem(detail: SAInvoiceDetail): Boolean {
        return insertInvoiceDetail(detail)
    }

    fun insertIntoSAInvoice(invoice: SAInvoiceItem): Boolean {
        return insertInvoice(invoice)
    }
}
