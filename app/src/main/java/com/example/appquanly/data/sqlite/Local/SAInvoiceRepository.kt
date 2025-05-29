package com.example.appquanly.data.sqlite.Local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.example.appquanly.SalePutIn.InvoiceWithDetails
import com.example.appquanly.data.sqlite.Entity.SAInvoiceDetail
import com.example.appquanly.data.sqlite.Entity.SAInvoiceItem

class SAInvoiceRepository(private val context: Context) {

    private val detailRepo = SAInvoiceDetailRepository(context)

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

    fun getAllInvoicesWithDetails(): List<InvoiceWithDetails> {
        val invoicesWithDetails = mutableListOf<InvoiceWithDetails>()
        val invoices = getAllInvoices()
        for (invoice in invoices) {
            val details = detailRepo.getDetailsByRefID(invoice.refId)
            invoicesWithDetails.add(InvoiceWithDetails(invoice, details))
        }
        return invoicesWithDetails
    }

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

    fun insertInvoice(invoice: SAInvoiceItem): Boolean {
        val db = DatabaseCopyHelper(context).writableDatabase
        return try {
            val cursor = db.query(
                "SAInvoice",
                arrayOf("RefID"),
                "RefID = ?",
                arrayOf(invoice.refId),
                null, null, null
            )
            val exists = cursor.count > 0
            cursor.close()
            if (exists) {
                db.close()
                false
            } else {
                val result = db.insert("SAInvoice", null, invoiceToContentValues(invoice))
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

    fun getLatestInvoice(): SAInvoiceItem? {
        val invoices = getLatestInvoices()
        return invoices.firstOrNull()
    }

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

    private fun invoiceToContentValues(invoice: SAInvoiceItem): ContentValues {
        return ContentValues().apply {
            put("RefID", invoice.refId)
            put("RefType", invoice.refType)
            put("RefNo", invoice.refNo)
            put("RefDate", invoice.refDate)
            put("Amount", invoice.amount)
            put("ReturnAmount", invoice.returnAmount)
            put("ReceiveAmount", invoice.receiveAmount)
            put("RemainAmount", invoice.remainAmount)
            put("JournalMemo", invoice.journalMemo)
            put("PaymentStatus", invoice.paymentStatus)
            put("NumberOfPeople", invoice.numberOfPeople)
            put("TableName", invoice.tableName)
            put("ListItemName", invoice.listItemName)
            put("CreatedDate", invoice.createdDate)
            put("CreatedBy", invoice.createdBy)
            put("ModifiedDate", invoice.modifiedDate)
            put("ModifiedBy", invoice.modifiedBy)
        }
    }

    private fun Cursor.getLongOrNull(columnName: String): Long? {
        val index = getColumnIndex(columnName)
        return if (index != -1 && !isNull(index)) getLong(index) else null
    }

    fun insertInvoiceDetail(detail: SAInvoiceDetail): Long {
        return detailRepo.insertDetail(detail)
    }

    fun insertInvoiceDetails(details: List<SAInvoiceDetail>) {
        detailRepo.insertDetails(details)
    }

    fun deleteInvoiceDetailsByRefID(refID: String): Int {
        return detailRepo.deleteByRefID(refID)
    }

    fun deleteInvoiceAndDetailsById(refId: String): Boolean {
        val db = DatabaseCopyHelper(context).writableDatabase
        try {
            db.beginTransaction()

            val detailsDeleted = db.delete("SAInvoiceDetail", "RefID = ?", arrayOf(refId))
            val invoiceDeleted = db.delete("SAInvoice", "RefID = ?", arrayOf(refId))

            db.setTransactionSuccessful()

            Log.d("DeleteInvoice", "Deleted details rows: $detailsDeleted, invoice rows: $invoiceDeleted")

            return (detailsDeleted > 0 || invoiceDeleted > 0)
        } catch (e: Exception) {
            Log.e("DeleteInvoice", "Error deleting invoice with RefID=$refId", e)
            return false
        } finally {
            try {
                db.endTransaction()
            } catch (e: Exception) {
                Log.e("DeleteInvoice", "Error ending transaction", e)
            }
            db.close()
        }
    }

}
