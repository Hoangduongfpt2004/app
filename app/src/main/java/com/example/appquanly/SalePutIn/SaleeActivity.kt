package com.example.appquanly.SalePutIn

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanly.R

class SaleeActivity : AppCompatActivity(), SaleeContract.View {

    private lateinit var presenter: SaleeContract.Presenter
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_salee)

        presenter = SaleePresenter(this)

        val recyclerView = findViewById<RecyclerView>(R.id.rvProducts)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ProductAdapter(emptyList())

        // Thiết lập xử lý sự kiện từ adapter
        adapter.onCancelClicked = { product ->
            presenter.onCancelClicked(product)
        }

        adapter.onPayClicked = { product ->
            presenter.onPayClicked(product)
        }

        recyclerView.adapter = adapter

        presenter.loadProducts()
    }

    override fun showProducts(products: List<SaleeModel>) {
        adapter.updateData(products)
    }

    // đổi tên hàm cho đúng interface
    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
