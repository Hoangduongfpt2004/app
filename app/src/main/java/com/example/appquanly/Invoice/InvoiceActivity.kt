package com.example.appquanly.Invoice

import android.content.ContentValues
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.appquanly.R
import com.example.appquanly.data.sqlite.Entity.SAInvoiceDetail
import com.example.appquanly.data.sqlite.Entity.SAInvoiceItem
import com.example.appquanly.data.sqlite.Local.DatabaseCopyHelper
import com.example.appquanly.data.sqlite.Local.SAInvoiceRepository
import com.example.appquanly.salee.SaleeActivity
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
    private var invoiceItem: SAInvoiceItem? = null
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

        initDataFromIntent()

        val invoiceNumber = generateNextInvoiceNumber()
        tvSoHoaDon.text = "Số hóa đơn: $invoiceNumber"
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
            val tienKhachDua = tvTienKhachDua.text.toString().replace(".", "").toDoubleOrNull() ?: 0.0
            if (tienKhachDua >= totalAmount) {
                invoiceItem?.let { item ->
                    val updatedItem = item.copy(
                        paymentStatus = 1,
                        receiveAmount = tienKhachDua,
                        remainAmount = tienKhachDua - totalAmount,
                        modifiedDate = System.currentTimeMillis(),
                        modifiedBy = item.modifiedBy ?: "User"
                    )
                    val repository = SAInvoiceRepository(this@InvoiceActivity)
                    val db = DatabaseCopyHelper(this@InvoiceActivity).writableDatabase
                    val values = ContentValues().apply {
                        put("PaymentStatus", updatedItem.paymentStatus)
                        put("ReceiveAmount", updatedItem.receiveAmount)
                        put("RemainAmount", updatedItem.remainAmount)
                        put("ModifiedDate", updatedItem.modifiedDate)
                        put("ModifiedBy", updatedItem.modifiedBy)
                    }
                    db.update("SAInvoice", values, "RefID = ?", arrayOf(item.refId))
                    db.close()

                    val resultIntent = Intent()
                    resultIntent.putExtra(SaleeActivity.EXTRA_INVOICE_REF_ID, item.refId)
                    setResult(SaleeActivity.RESULT_PAYMENT_SUCCESS, resultIntent)
                    Toast.makeText(this, "Đã thu tiền thành công, vui lòng chọn món mới", Toast.LENGTH_SHORT).show()
                    finish()
                } ?: run {
                    Toast.makeText(this, "Lỗi: Không tìm thấy thông tin hóa đơn", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Số tiền khách đưa chưa đủ", Toast.LENGTH_SHORT).show()
            }
        }

        tvTienKhachDua.setOnClickListener {
            val dialog = CalculatorDialogFragment(totalAmount.toLong()) { soTienKhachDua ->
                tvTienKhachDua.text = String.format("%,d", soTienKhachDua).replace(",", ".")
                val tienThoi = soTienKhachDua - totalAmount
                showRemainAmount(tienThoi)
            }
            dialog.show(supportFragmentManager, "CalculatorDialog")
        }
    }

    private fun initDataFromIntent() {
        // Lấy dữ liệu hóa đơn từ intent
        invoiceItem = intent.getParcelableExtra<SAInvoiceItem>("invoiceItem")
        invoiceDetails = intent.getParcelableArrayListExtra<SAInvoiceDetail>("invoiceDetails")
            ?: intent.getParcelableArrayListExtra("EXTRA_INVOICE_DETAILS")
                    ?: listOf()

        // Lấy số bàn và số khách từ invoiceItem
        val soBan = invoiceItem?.tableName
        val soKhach = invoiceItem?.numberOfPeople?.toString()

        // Thiết lập số bàn
        tvSoBan.text = soBan?.let { "Bàn $it" } ?: "Bàn không xác định"

        // Thiết lập ngày
        val formattedDate = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Calendar.getInstance().time)
        tvNgay.text = "Ngày: $formattedDate"

        // Hiển thị chi tiết hóa đơn và tính tổng tiền
        if (invoiceDetails.isNotEmpty()) {
            displayInvoiceDetails(invoiceDetails)
            showTotalAmount(totalAmount)
        } else {
            showToast("Không có dữ liệu hóa đơn")
            tvTongTien.text = "0 đ"
        }
    }

    private fun displayInvoiceDetails(details: List<SAInvoiceDetail>) {
        tableLayout.removeAllViews()
        totalAmount = 0.0

        // Hàng tiêu đề
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

        // Dòng kẻ đen dưới tiêu đề
        addSolidDivider()

        // Hiển thị các dòng sản phẩm
        details.forEach { detail ->
            val amount = detail.Quantity * detail.UnitPrice
            totalAmount += amount

            val row = TableRow(this).apply {
                listOf(
                    detail.InventoryItemName,
                    detail.Quantity.toInt().toString(),
                    String.format("%,.0f", detail.UnitPrice).replace(",", "."),
                    String.format("%,.0f", amount).replace(",", ".")
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
        }

        // Cập nhật tvTienKhachDua với totalAmount
        tvTienKhachDua.text = String.format("%,.0f", totalAmount).replace(",", ".")
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
        tvTongTien.text = "${String.format("%,.0f", amount).replace(",", ".")} đ"
    }

    override fun showReceiveAmount(amount: Double) {
        tvTienKhachDua.text = String.format("%,.0f", amount).replace(",", ".")
    }

    override fun showRemainAmount(remain: Double) {
        // Sử dụng %.0f cho Double, vì remain là Double
        tvTienTraLai.text = "${String.format("%,.0f", remain).replace(",", ".")} đ"
    }

    override fun showInvoiceInfo(refNo: String, date: String) {
        tvSoHoaDon.text = "Số hóa đơn: $refNo"
        tvNgay.text = "Ngày: $date"
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}