package com.example.appquanly

import android.app.Application
import com.example.appquanly.data.sqlite.Local.DatabaseCopyHelper

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DatabaseCopyHelper(this).createDatabaseIfNeeded()
    }
}
