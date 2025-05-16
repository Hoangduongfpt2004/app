package com.example.appquanly.UnitOfMeasure

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.appquanly.data.local.DatabaseCopyHelper
import java.time.LocalDateTime

class UnitRepository(private val context: Context) {

    @SuppressLint("NewApi")
    fun getAllUnit(): List<UnitOfMeasure> {
        val itemUnit = mutableListOf<UnitOfMeasure>()
        val db = DatabaseCopyHelper(context).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Unit", null)

        if (cursor.moveToFirst()) {
            do {
                val item = UnitOfMeasure(
                    UnitID = cursor.getString(cursor.getColumnIndexOrThrow("UnitID")),
                    UnitName = cursor.getString(cursor.getColumnIndexOrThrow("UnitName")),
                    Description = cursor.getString(cursor.getColumnIndexOrThrow("Description")),
                    Inactive = cursor.getInt(cursor.getColumnIndexOrThrow("Inactive")) != 0,
                    CreatedBy = cursor.getString(cursor.getColumnIndexOrThrow("CreatedBy")),
                    CreatedDate = LocalDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow("CreatedDate"))),
                    ModifiedDate = LocalDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow("ModifiedDate"))),
                    ModifiedBy = cursor.getString(cursor.getColumnIndexOrThrow("ModifiedBy"))
                )
                itemUnit.add(item)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return itemUnit
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addDonViTinh(item: DonViTinh) {
        val db = DatabaseCopyHelper(context).writableDatabase
        val sql = """
            INSERT INTO Unit (UnitID, UnitName, Description, Inactive, CreatedBy, CreatedDate, ModifiedDate, ModifiedBy)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        val statement = db.compileStatement(sql)
        val now = LocalDateTime.now().toString()

        statement.bindString(1, item.id)
        statement.bindString(2, item.name)
        statement.bindString(3, "")
        statement.bindLong(4, 0)
        statement.bindString(5, "admin")
        statement.bindString(6, now)
        statement.bindString(7, now)
        statement.bindString(8, "admin")

        statement.executeInsert()
        db.close()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDonViTinh(item: DonViTinh) {
        val db = DatabaseCopyHelper(context).writableDatabase
        val sql = "UPDATE Unit SET UnitName = ?, ModifiedDate = ?, ModifiedBy = ? WHERE UnitID = ?"

        val statement = db.compileStatement(sql)
        val now = LocalDateTime.now().toString()

        statement.bindString(1, item.name)
        statement.bindString(2, now)
        statement.bindString(3, "admin")
        statement.bindString(4, item.id)

        statement.executeUpdateDelete()
        db.close()
    }
}
