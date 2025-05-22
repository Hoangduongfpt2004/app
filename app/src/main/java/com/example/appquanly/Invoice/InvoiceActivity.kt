package com.example.appquanly.Invoice

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appquanly.R
import com.example.appquanly.data.sqlite.Entity.InventoryItem
import com.example.appquanly.data.sqlite.Entity.SAInvoiceDetail

class InvoiceActivity : AppCompatActivity(), InvoiceContract.View {

    private lateinit var tvSoHoaDon: TextView
    private lateinit var tvNgay: TextView
    private lateinit var tvTienKhachDua: EditText
    private lateinit var tvTienTraLai: TextView
    private lateinit var tvTongTien: TextView
    private lateinit var tableLayout: TableLayout

    private var totalAmount = 0.0

    // Giả sử bạn có Presenter
    private lateinit var presenter: InvoiceContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice2)

        tvSoHoaDon = findViewById(R.id.tvSoHoaDon)
        tvNgay = findViewById(R.id.tvNgay)
        tvTienKhachDua = findViewById(R.id.tvTienKhachDua)
        tvTienTraLai = findViewById(R.id.tvTienTraLai)
        tvTongTien = findViewById(R.id.tvTongTien)
        tableLayout = findViewById(R.id.tableLayout)

        // Khởi tạo Presenter nếu có
        // presenter = InvoicePresenter(this)

        // Lấy dữ liệu danh sách hóa đơn từ Intent (giả sử là SAInvoiceDetail)
        val invoiceDetails = intent.getParcelableArrayListExtra<SAInvoiceDetail>("invoice_details") ?: emptyList()

        // Lấy mã hóa đơn
        val refID = intent.getStringExtra("refID") ?: ""

        showInvoiceInfo(refID, java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(System.currentTimeMillis()))
        displayInvoiceDetails(invoiceDetails)

        tvTienKhachDua.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val given = s.toString().toDoubleOrNull() ?: 0.0
                val change = given - totalAmount
                showRemainAmount(if (change >= 0) change else 0.0)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun displayInvoiceDetails(details: List<SAInvoiceDetail>) {
        tableLayout.removeAllViews()
        totalAmount = 0.0

        details.forEach { detail ->
            val row = TableRow(this)

            val tvName = TextView(this).apply { text = detail.InventoryItemName }
            val tvQuantity = TextView(this).apply { text = detail.Quantity.toInt().toString() }
            val tvUnitPrice = TextView(this).apply { text = detail.UnitPrice.toInt().toString() }
            val tvAmount = TextView(this).apply { text = detail.Amount.toInt().toString() }

            row.addView(tvName)
            row.addView(tvQuantity)
            row.addView(tvUnitPrice)
            row.addView(tvAmount)

            tableLayout.addView(row)

            totalAmount += detail.Amount.toDouble()
        }

        showTotalAmount(totalAmount)
    }

    override fun showInvoiceData(items: List<InventoryItem>) {
        // Bạn có thể xử lý nếu dùng InventoryItem thay vì SAInvoiceDetail
    }

    override fun showTotalAmount(amount: Double) {
        tvTongTien.text = amount.toInt().toString()
    }

    override fun showReceiveAmount(amount: Double) {
        tvTienKhachDua.setText(amount.toInt().toString())
    }

    override fun showRemainAmount(remain: Double) {
        tvTienTraLai.text = remain.toInt().toString()
    }

    override fun showInvoiceInfo(refNo: String, date: String) {
        tvSoHoaDon.text = "Hóa đơn: $refNo"
        tvNgay.text = "Ngày: $date"
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
