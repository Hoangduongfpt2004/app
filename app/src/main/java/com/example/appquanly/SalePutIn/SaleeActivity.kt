// SaleeActivity.kt
package com.example.appquanly.SalePutIn

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanly.R
import com.example.appquanly.data.sqlite.Entity.SAInvoiceItem
import com.example.appquanly.data.sqlite.Local.SAInvoiceRepository

class SaleeActivity : AppCompatActivity(), SaleeContract.View {

    private lateinit var presenter: SaleeContract.Presenter
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_salee)

        // Khởi tạo Presenter
        presenter = SaleePresenter(this)

        // Setup RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.rvProducts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ProductAdapter(emptyList())
        adapter.onCancelClicked = { product -> presenter.onCancelClicked(product) }
        adapter.onPayClicked    = { product -> presenter.onPayClicked(product) }
        recyclerView.adapter    = adapter

        // Nhận dữ liệu hóa đơn từ Intent

        // Nhận dữ liệu từ Intent
        val invoiceItems = intent.getSerializableExtra("invoice_items")
                as? ArrayList<SAInvoiceItem>

        if (invoiceItems != null && invoiceItems.isNotEmpty()) {
            // 1. Lấy header hóa đơn (chỉ insert 1 lần)
            val header = invoiceItems.first()
            val repo = SAInvoiceRepository(this)
            val success = repo.insertInvoice(header)
            if (!success) {
                Toast.makeText(this, "Lưu hóa đơn thất bại", Toast.LENGTH_SHORT).show()
            }

            // 2. Chuyển list thành SaleeModel để hiển thị chi tiết
            val products = invoiceItems.map { inv ->
                SaleeModel(
                    name  = inv.listItemName ?: "Món chưa rõ",
                    price = String.format("%.0f đ", inv.amount)
                )
            }
            showProducts(products)
        } else {
            presenter.loadProducts()
        }


    }
    override fun showProducts(products: List<SaleeModel>) {
        adapter.updateData(products)

    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    }




