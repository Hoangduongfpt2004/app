package com.example.appquanly.SalePutIn

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanly.R
import com.example.appquanly.data.sqlite.Local.SAInvoiceDetailRepository
import com.example.appquanly.data.sqlite.Local.SAInvoiceRepository
import java.util.UUID

class SaleeActivity : AppCompatActivity() {

    private lateinit var repoInvoice: SAInvoiceRepository
    private lateinit var repoDetail: SAInvoiceDetailRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SaleeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_salee)

        recyclerView = findViewById(R.id.rvProducts)
        recyclerView.layoutManager = LinearLayoutManager(this)

        repoInvoice = SAInvoiceRepository(this)
        repoDetail = SAInvoiceDetailRepository(this)


        val invoices = repoInvoice.getLatestInvoice()

        adapter = SaleeAdapter(invoices)
        recyclerView.adapter = adapter
    }
}
