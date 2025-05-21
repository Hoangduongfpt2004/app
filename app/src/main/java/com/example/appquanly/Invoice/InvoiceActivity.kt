package com.example.appquanly.Invoice

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.appquanly.R
import com.example.appquanly.data.sqlite.Entity.InventoryItem
import java.text.DecimalFormat

class InvoiceActivity : AppCompatActivity(), InvoiceContract.View {

    private lateinit var presenter: InvoiceContract.Presenter
    private lateinit var tableLayout: TableLayout
    private lateinit var tvSoHoaDon: TextView
    private lateinit var tvNgay: TextView
    private lateinit var tvTienKhachDua: EditText
    private lateinit var tvTienTraLai: TextView
    private lateinit var btnXong: Button
    private lateinit var tvTongTien: TextView

    private val moneyFormat = DecimalFormat("#,###")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice2)

        tableLayout = findViewById(R.id.tableLayout)
        tvSoHoaDon = findViewById(R.id.tvSoHoaDon)
        tvNgay = findViewById(R.id.tvNgay)
        tvTienKhachDua = findViewById(R.id.tvTienKhachDua)
        tvTienTraLai = findViewById(R.id.tvTienTraLai)
        btnXong = findViewById(R.id.btnXong)
        tvTongTien = findViewById(R.id.tvTongTien)

        presenter = InvoicePresenter(this, this)

        val selectedItems = intent.getParcelableArrayListExtra<InventoryItem>("selected_items")
        selectedItems?.let {
            presenter.setSelectedItems(it)
        } ?: run {
            presenter.setSelectedItems(emptyList())
        }

        presenter.loadInvoiceData()

        btnXong.setOnClickListener {
            val tienKhachDua = tvTienKhachDua.text.toString().replace(".", "").toDoubleOrNull() ?: 0.0
            presenter.onDoneClick(tienKhachDua)
            finish()
        }

        tvTienKhachDua.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val cleanString = s.toString().replace(".", "")
                val amount = cleanString.toDoubleOrNull() ?: 0.0
                presenter.calculateInvoice(amount)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun showInvoiceData(items: List<InventoryItem>) {
        tableLayout.removeAllViews()

        // Header
        val headerRow = TableRow(this).apply {
            addView(createCell("Tên hàng", true))
            addView(createCell("SL", true))
            addView(createCell("Đơn giá", true))
            addView(createCell("Thành tiền", true))
        }
        tableLayout.addView(headerRow)

        var tongTien = 0.0
        for (item in items) {
            val soLuong = item.UseCount
            val donGia = item.Price ?: 0f
            val thanhTien = soLuong * donGia
            tongTien += thanhTien

            val row = TableRow(this).apply {
                addView(createCell(item.InventoryItemName ?: ""))
                addView(createCell(soLuong.toString()))
                addView(createCell(moneyFormat.format(donGia.toDouble())))
                addView(createCell(moneyFormat.format(thanhTien.toDouble())))
            }
            tableLayout.addView(row)
        }

        // Dòng tổng tiền
        val totalRow = TableRow(this).apply {
            addView(createCell("Tổng cộng", true))
            addView(createCell(""))
            addView(createCell(""))
            addView(createCell(moneyFormat.format(tongTien), true))
        }
        tableLayout.addView(totalRow)

        showTotalAmount(tongTien)
    }

    override fun showTotalAmount(amount: Double) {
        tvTongTien.text = "Tiền khách phải trả: ${moneyFormat.format(amount)}"
    }

    override fun showReceiveAmount(amount: Double) {
        // Nếu muốn format lại tiền khách đưa:
        // tvTienKhachDua.setText(moneyFormat.format(amount))
    }

    override fun showRemainAmount(remain: Double) {
        tvTienTraLai.text = "Tiền trả lại: ${moneyFormat.format(remain)}"
    }

    override fun showInvoiceInfo(refNo: String, date: String) {
        tvSoHoaDon.text = "Số: $refNo"
        tvNgay.text = "Ngày: $date"
    }

    private fun createCell(text: String, isHeader: Boolean = false): TextView {
        return TextView(this).apply {
            setPadding(16, 12, 16, 12)
            this.text = text
            if (isHeader) {
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
        }
    }
}
