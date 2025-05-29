package com.example.appquanly.salee

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanly.*
import com.example.appquanly.ChooseDish.view.ChooseDishActivity
import com.example.appquanly.SalePutIn.BaseDrawerActivity
import com.example.appquanly.SalePutIn.InvoiceWithDetails
import com.example.appquanly.SalePutIn.SaleeAdapter
import com.example.appquanly.data.sqlite.Local.SAInvoiceRepository

class SaleeActivity : BaseDrawerActivity(), SaleeContract.View {

    private lateinit var presenter: SaleeContract.Presenter
    private lateinit var tvHint: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutEmpty: View
    private lateinit var adapter: SaleeAdapter
    private lateinit var repository: SAInvoiceRepository

    companion object {
        const val REQUEST_CODE_SALEE = 2001
    }

    override fun getLayoutId(): Int = R.layout.activity_sale
    override fun getNavigationMenuItemId(): Int = R.id.ban_hang

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tvHint = findViewById(R.id.tvHint)
        recyclerView = findViewById(R.id.rvProducts)
        layoutEmpty = findViewById(R.id.layoutEmpty)

        repository = SAInvoiceRepository(this)
        presenter = SaleePresenter(this, this)

        recyclerView.layoutManager = LinearLayoutManager(this)

        tvHint.setOnClickListener {
            presenter.onHintClicked()
        }

        adapter = SaleeAdapter(
            invoicesWithDetails = mutableListOf(),
            onCancelClick = { itemWithDetails ->
                showCancelDialog(itemWithDetails)
            },
            onPayClick = { itemWithDetails ->
                showMessage("Thanh toán bàn ${itemWithDetails.invoice.tableName}")
            }
        )

        recyclerView.adapter = adapter

        presenter.loadInvoiceItems()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_banhang, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add -> {
                presenter.onAddButtonClicked()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun showNoOrders() {
        layoutEmpty.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        tvHint.text = "Bấm vào đây hoặc dấu + để chọn món"
    }

    override fun openOrderScreen() {
        // Mở màn hình chọn món bằng startActivityForResult
        val intent = Intent(this, ChooseDishActivity::class.java)
        // Nếu cần thêm dữ liệu, bạn có thể putExtra vào intent ở đây
        startActivityForResult(intent, REQUEST_CODE_SALEE)
    }

    override fun showMenu() {
        Toast.makeText(this, "Mở menu", Toast.LENGTH_SHORT).show()
    }

    override fun navigateTo(screen: SaleeContract.MenuScreen) {
        // Đã xử lý ở BaseDrawerActivity
    }

    override fun onMenuItemClicked(itemId: Int) {
        presenter.onMenuItemClicked(itemId)
    }

    override fun showInvoiceItems(items: List<InvoiceWithDetails>) {
        if (items.isEmpty()) {
            showNoOrders()
        } else {
            layoutEmpty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            adapter.updateData(items)
        }
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showCancelDialog(itemWithDetails: InvoiceWithDetails) {
        val context = this
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.activity_popup, null)

        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.show()

        val btnCancelAdd = dialogView.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnCancelAdd)
        val btnSaveAdd = dialogView.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnSaveAdd)
        val btnCloseAdd = dialogView.findViewById<ImageView>(R.id.btnCloseAdd)

        btnCancelAdd.setOnClickListener { dialog.dismiss() }
        btnCloseAdd.setOnClickListener { dialog.dismiss() }

        btnSaveAdd.setOnClickListener {
            deleteInvoice(itemWithDetails)
            dialog.dismiss()
        }
    }

    private fun deleteInvoice(itemWithDetails: InvoiceWithDetails) {
        val success = repository.deleteInvoiceAndDetailsById(itemWithDetails.invoice.refId)
        if (success) {
            // Cập nhật lại danh sách trong adapter
            val currentList = adapter.getData().toMutableList()
            val removed = currentList.remove(itemWithDetails)
            if (removed) {
                adapter.updateData(currentList)
                showMessage("Hủy đơn bàn ${itemWithDetails.invoice.tableName} thành công")
            }
            if (currentList.isEmpty()) {
                showNoOrders()
            }
        } else {
            showMessage("Hủy đơn thất bại")
        }
    }
}
