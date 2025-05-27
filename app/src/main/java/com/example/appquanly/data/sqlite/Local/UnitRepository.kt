package com.example.appquanly.data.sqlite.Local

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

import com.example.appquanly.data.sqlite.Entity.UnitOfMeasure
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
    fun addDonViTinh(item: UnitOfMeasure) {
        val db = DatabaseCopyHelper(context).writableDatabase
        val sql = """
        INSERT INTO Unit (UnitID, UnitName, Description, Inactive, CreatedBy, CreatedDate, ModifiedDate, ModifiedBy)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """.trimIndent()

        val statement = db.compileStatement(sql)
        val now = LocalDateTime.now().toString()

        statement.bindString(1, item.UnitID)
        statement.bindString(2, item.UnitName)
        statement.bindString(3, item.Description)
        statement.bindLong(4, if (item.Inactive) 1 else 0)
        statement.bindString(5, item.CreatedBy)
        statement.bindString(6, item.CreatedDate.toString())
        statement.bindString(7, item.ModifiedDate.toString())
        statement.bindString(8, item.ModifiedBy)

        statement.executeInsert()
        db.close()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateDonViTinh(item: UnitOfMeasure) {
        val db = DatabaseCopyHelper(context).writableDatabase
        val sql = "UPDATE Unit SET UnitName = ?, ModifiedDate = ?, ModifiedBy = ? WHERE UnitID = ?"

        val statement = db.compileStatement(sql)
        statement.bindString(1, item.UnitName)
        statement.bindString(2, item.ModifiedDate.toString())
        statement.bindString(3, item.ModifiedBy)
        statement.bindString(4, item.UnitID)

        statement.executeUpdateDelete()
        db.close()
    }

}
