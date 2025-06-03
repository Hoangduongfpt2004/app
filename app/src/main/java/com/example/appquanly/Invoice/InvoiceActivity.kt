package com.example.appquanly.Invoice

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.appquanly.CalculatorDialogFragment
import com.example.appquanly.R
import com.example.appquanly.data.sqlite.Entity.SAInvoiceDetail
import com.example.appquanly.data.sqlite.Entity.SAInvoiceItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class InvoiceActivity : AppCompatActivity(), InvoiceContract.View {

    private lateinit var tvSoHoaDon: TextView
    private lateinit var tvNgay: TextView
    private lateinit var tvTienKhachDua: TextView
    private lateinit var tvTienTraLai: TextView
    private lateinit var tvTongTien: TextView
    private lateinit var tableLayout: TableLayout
    private lateinit var tvSoBan: TextView
    private lateinit var btnXong: AppCompatButton
    private lateinit var icBack: ImageView
    private var invoiceDetails: List<SAInvoiceDetail> = listOf()

    private var totalAmount = 0.0

    companion object {
        private const val PREFS_NAME = "InvoicePrefs"
        private const val KEY_INVOICE_NUMBER = "invoice_number"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice2)

        bindViews()
        setupListeners()

        val invoiceNumber = generateNextInvoiceNumber()
        tvSoHoaDon.text = "Số hóa đơn: $invoiceNumber"

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

        invoiceDetails = intent.getParcelableArrayListExtra("invoiceDetails")
            ?: intent.getParcelableArrayListExtra("EXTRA_INVOICE_DETAILS")
                    ?: listOf()

        val invoiceItem = intent.getParcelableExtra<SAInvoiceItem>("invoiceItem")

        // Nhận dữ liệu từ cả nút "Cất" và "Thanh toán"
        val invoiceDetails = intent.getParcelableArrayListExtra<SAInvoiceDetail>("invoiceDetails")
            ?: intent.getParcelableArrayListExtra("EXTRA_INVOICE_DETAILS")

        val soBan = intent.getStringExtra("EXTRA_SO_BAN")
        val soKhach = intent.getStringExtra("EXTRA_SO_KHACH")
        val tongTienStr = intent.getStringExtra("EXTRA_TONG_TIEN")

        tvSoBan.text = soBan?.let { " Bàn $it " }

        // Hiển thị tổng tiền nếu có truyền vào
        if (!tongTienStr.isNullOrEmpty()) {
            tvTongTien.text = " $tongTienStr"
        }

        val formattedDate = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().time)
        tvNgay.text = "Ngày: $formattedDate"

        if (invoiceDetails != null && invoiceDetails.isNotEmpty()) {
            displayInvoiceDetails(invoiceDetails)
        } else {
            showToast("Không có dữ liệu hóa đơn")
        }
    }

    private fun displayInvoiceDetails(details: List<SAInvoiceDetail>) {
        tableLayout.removeAllViews()
        totalAmount = 0.0

        // Header row
        val headerRow = TableRow(this).apply {
            listOf("Tên Hàng", "SL", "Đơn giá", "Thành tiền").forEach {
                addView(TextView(context).apply {
                    text = it
                    setPadding(16, 8, 16, 8)
                    setTypeface(null, Typeface.BOLD)
                    gravity = Gravity.CENTER
                })
            }
        }
        tableLayout.addView(headerRow)

        // Dòng kẻ đen dưới header
        addSolidDivider()


        // Hiển thị các dòng sản phẩm
        details.forEach { detail ->
            val amount = detail.Quantity * detail.UnitPrice

            val row = TableRow(this).apply {
                listOf(
                    detail.InventoryItemName,
                    detail.Quantity.toInt().toString(),
                    String.format("%,.0f", detail.UnitPrice),
                    String.format("%,.0f", amount)
                ).forEach {
                    addView(TextView(context).apply {
                        text = it
                        setPadding(16, 8, 16, 8)
                        gravity = Gravity.CENTER
                    })
                }
            }
            tableLayout.addView(row)

            // Dòng kẻ nét đứt sau mỗi sản phẩm
            addDashedLine()

            totalAmount += amount
        }

        tvTienKhachDua.text = String.format("%,.0f", totalAmount)

    }

    private fun addSolidDivider() {
        val divider = View(this).apply {
            layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                2
            )
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.black))
        }
        tableLayout.addView(divider)
    }

    private fun addDashedLine() {
        val line = View(this).apply {
            layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                2
            ).apply {
                topMargin = 8
                bottomMargin = 8
            }
            background = ContextCompat.getDrawable(context, R.drawable.dashed_line)
        }
        tableLayout.addView(line)
    }


    private fun generateNextInvoiceNumber(): String {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val currentNumber = prefs.getInt(KEY_INVOICE_NUMBER, 0) + 1
        prefs.edit().putInt(KEY_INVOICE_NUMBER, currentNumber).apply()
        return String.format("%04d", currentNumber)
    }

    override fun showInvoiceData(items: List<com.example.appquanly.data.sqlite.Entity.InventoryItem>) {}

    override fun showTotalAmount(amount: Double) {
        tvTongTien.text = " ${String.format("%,.0f", amount)} đ"
    }

    override fun showReceiveAmount(amount: Double) {
        tvTienKhachDua.setText(String.format("%,.0f", amount))
    }

    override fun showRemainAmount(remain: Double) {
        tvTienTraLai.text = " ${String.format("%,.0f", remain)} đ"
    }

    override fun showInvoiceInfo(refNo: String, date: String) {
        tvSoHoaDon.text = "Số hóa đơn: $refNo"
        tvNgay.text = "Ngày: $date"
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
