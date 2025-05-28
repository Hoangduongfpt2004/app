package com.example.appquanly.ProductInformation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appquanly.R
import com.example.appquanly.SalePutIn.BaseDrawerActivity

class ProductlnformationActivity : BaseDrawerActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    override fun getLayoutId(): Int {
        return R.layout.activity_link_account
    }

    override fun getNavigationMenuItemId(): Int {
        return R.id.thong_tin_san_pham
    }
}