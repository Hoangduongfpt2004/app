package com.example.appquanly.data.sqlite.Local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream

class DatabaseCopyHelper(private val context: Context) : SQLiteOpenHelper(
    context,
    DB_NAME,
    null,
    DB_VERSION
) {

    companion object {
        private const val DB_NAME = "cukcuk_blank.db"
        private const val DB_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Không cần tạo bảng vì dùng DB có sẵn
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Có thể xử lý nâng cấp nếu cần
    }

    /**
     * Sao chép database từ assets nếu chưa tồn tại.
     */
    fun createDatabaseIfNeeded() {
        val dbFile = context.getDatabasePath(DB_NAME)

        if (dbFile.exists()) return // Đã có rồi, không sao chép nữa

        dbFile.parentFile?.mkdirs() // Đảm bảo thư mục tồn tại

        context.assets.open(DB_NAME).use { inputStream ->
            FileOutputStream(dbFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }
}
