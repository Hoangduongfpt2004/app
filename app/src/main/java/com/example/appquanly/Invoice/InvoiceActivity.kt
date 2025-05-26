package com.example.appquanly.Invoice

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.appquanly.CalculatorDialogFragment
import com.example.appquanly.R
import com.example.appquanly.data.sqlite.Entity.SAInvoiceDetail
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class InvoiceActivity : AppCompatActivity(), InvoiceContract.View {

    private lateinit var tvSoHoaDon: TextView
    private lateinit var tvNgay: TextView
    private lateinit var tvTienKhachDua: EditText
    private lateinit var tvTienTraLai: TextView
    private lateinit var tvTongTien: TextView
    private lateinit var tableLayout: TableLayout
    private lateinit var tvSoBan: TextView
    private lateinit var btnXong: AppCompatButton
    private lateinit var icBack: ImageView

    private var totalAmount = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice2)

        bindViews()
        setupListeners()
        initDataFromIntent()
    }

    private fun bindViews() {
        tvSoHoaDon = findViewById(R.id.tvSoHoaDon)
        tvNgay = findViewById(R.id.tvNgay)
        tvTienKhachDua = findViewById(R.id.tvTienKhachDua)
        tvTienTraLai = findViewById(R.id.tvTienTraLai)
        tvTongTien = findViewById(R.id.tvTongTien)
        tableLayout = findViewById(R.id.tableLayout)
        tvSoBan = findViewById(R.id.tvSoBan)
        btnXong = findViewById(R.id.btnXong)
        icBack = findViewById(R.id.icBack)
    }

    private fun setupListeners() {
        icBack.setOnClickListener {
            onBackPressed()
        }

        btnXong.setOnClickListener {
            Toast.makeText(this, "Đã thu tiền thành công, vui lòng chọn món mới", Toast.LENGTH_SHORT).show()
            finish()
        }

        tvTienKhachDua.setOnClickListener {
            val dialog = CalculatorDialogFragment(totalAmount.toLong()) { soTienKhachDua ->
            tvTienKhachDua.setText(String.format("%,d", soTienKhachDua).replace(",", "."))
                val tienThoi = soTienKhachDua - totalAmount
                showRemainAmount(tienThoi)
            }
            dialog.show(supportFragmentManager, "CalculatorDialog")
        }


    }

    private fun initDataFromIntent() {
        // Hiển thị số bàn
        val soBan = intent.getStringExtra("EXTRA_SO_BAN")
        tvSoBan.text = soBan?.let { " Bàn $it " } ?: "Chưa có số bàn"

        // Hiển thị ngày giờ hiện tại
        val formattedDate = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().time)
        tvNgay.text = "Ngày: $formattedDate"

        // Nhận dữ liệu hóa đơn
        val details: ArrayList<SAInvoiceDetail>? = intent.getParcelableArrayListExtra("EXTRA_INVOICE_DETAILS")
        if (details != null && details.isNotEmpty()) {
            displayInvoiceDetails(details)
        } else {
            showToast("Không có dữ liệu hóa đơn")
        }
    }

    private fun displayInvoiceDetails(details: List<SAInvoiceDetail>) {
        tableLayout.removeAllViews()
        totalAmount = 0.0

        // Tạo header
        val headerRow = TableRow(this).apply {
            listOf("Tên món", "SL", "Đơn giá", "Thành tiền").forEach {
                addView(TextView(context).apply {
                    text = it
                    setPadding(16, 8, 16, 8)
                })
            }
        }
        tableLayout.addView(headerRow)

        // Tạo các dòng chi tiết
        details.forEach { detail ->
            val amount = detail.Quantity * detail.UnitPrice

            val row = TableRow(this).apply {
                addView(TextView(context).apply { text = detail.InventoryItemName })
                addView(TextView(context).apply { text = detail.Quantity.toString() })
                addView(TextView(context).apply { text = String.format("%,.0f", detail.UnitPrice) })
                addView(TextView(context).apply { text = String.format("%,.0f", amount) })
            }
            tableLayout.addView(row)

            totalAmount += amount
        }

        showTotalAmount(totalAmount)
    }

    override fun showInvoiceData(items: List<com.example.appquanly.data.sqlite.Entity.InventoryItem>) { /* không dùng */ }

    override fun showTotalAmount(amount: Double) {
        tvTongTien.text = "Số tiền phải trả: ${String.format("%,.0f", amount)} đ"
    }

    override fun showReceiveAmount(amount: Double) {
        tvTienKhachDua.setText(String.format("%,.0f", amount))
    }

    override fun showRemainAmount(remain: Double) {
        tvTienTraLai.text = "Tiền thối lại: ${String.format("%,.0f", remain)} đ"
    }

    override fun showInvoiceInfo(refNo: String, date: String) {
        tvSoHoaDon.text = "Số hóa đơn: $refNo"
        tvNgay.text = "Ngày: $date"
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
