package com.example.appquanly.ChooseDish.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanly.R
import com.example.appquanly.data.sqlite.Entity.InventoryItem
import com.example.appquanly.data.sqlite.Entity.SAInvoiceDetail
import com.example.appquanly.ChooseDish.contract.ChooseDishContract
import com.example.appquanly.ChooseDish.presenter.ChooseDishPresenter
import com.example.appquanly.CashRegister.CalculatorDialogFragment
import com.example.appquanly.Invoice.InvoiceActivity
import com.example.appquanly.data.sqlite.Entity.SAInvoiceItem
import com.example.appquanly.data.sqlite.Local.InventoryItemRepository
import com.example.appquanly.salee.SaleeActivity
import java.util.Locale

class ChooseDishActivity : AppCompatActivity(), ChooseDishContract.View {

    private lateinit var presenter: ChooseDishPresenter
    private lateinit var tvTotalMoney: TextView
    private lateinit var btnBack: ImageView
    private lateinit var btnSetting: TextView
    private lateinit var btnAvatar: TextView
    private lateinit var btnCollectMoney: Button
    private lateinit var edit_x: TextView
    private lateinit var edit_store: TextView
    private lateinit var adapter: ChooseDishAdapter
    private lateinit var recyclerView: RecyclerView
    private var currentTargetTextView: TextView? = null

    companion object {
        const val REQUEST_CODE_CALCULATOR = 1001
        const val REQUEST_CODE_SALEE = 2001
        const val REQUEST_CODE_INVOICE = 3001 // Thêm để xử lý kết quả từ InvoiceActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_dish)

        val repository = InventoryItemRepository(this)
        presenter = ChooseDishPresenter(this, repository)

        tvTotalMoney = findViewById(R.id.tvTongTien)
        btnBack = findViewById(R.id.img_back)
        btnSetting = findViewById(R.id.seting)
        btnAvatar = findViewById(R.id.avatar)
        btnCollectMoney = findViewById(R.id.btnCollectMoney)
        edit_x = findViewById(R.id.edt_x)
        edit_store = findViewById(R.id.editStore)

        recyclerView = findViewById(R.id.rvMonAn)
        adapter = ChooseDishAdapter(mutableListOf(), object : ChooseDishAdapter.OnDishActionListener {
            override fun onIncrease(item: InventoryItem) {
                val newQuantity = (item.quantity ?: 0) + 1
                item.quantity = newQuantity
                presenter.updateQuantityForItem(item, newQuantity)
                adapter.notifyDataSetChanged()
            }

            override fun onDecrease(item: InventoryItem) {
                val current = item.quantity ?: 0
                if (current > 0) {
                    val newQuantity = current - 1
                    item.quantity = newQuantity
                    presenter.updateQuantityForItem(item, newQuantity)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onQuantityClick(item: InventoryItem, position: Int) {
                currentTargetTextView = recyclerView.findViewHolderForAdapterPosition(position)
                    ?.itemView?.findViewById(R.id.tvQuantity)
                openCalculatorForQuantity(item, position)
            }
        })

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        btnBack.setOnClickListener {
            presenter.onBackClick()
            finish()
        }

        btnCollectMoney.setOnClickListener {
            presenter.onCollectMoneyClick()
        }

        btnSetting.setOnClickListener {
            currentTargetTextView = btnSetting
            presenter.onSettingClick()
        }

        btnAvatar.setOnClickListener {
            currentTargetTextView = btnAvatar
            presenter.onAvatarClick()
        }

        edit_x.setOnClickListener {
            presenter.onBackClick()
            finish()
        }

        edit_store.setOnClickListener {
            val selectedDetails = presenter.getSelectedInvoiceDetails()
            val soBan = btnSetting.text.toString()
            val soKhach = btnAvatar.text.toString()
            val tongTien = tvTotalMoney.text.toString()

            val invoiceItems = presenter.convertToInvoiceItems(selectedDetails)
            presenter.saveInvoice(soBan, soKhach, tongTien, selectedDetails)

            val intent = Intent(this, SaleeActivity::class.java)
            intent.putExtra("so_ban", soBan)
            intent.putExtra("so_khach", soKhach)
            intent.putExtra("tong_tien", tongTien)
            intent.putExtra("invoiceItems", ArrayList(invoiceItems))
            intent.putParcelableArrayListExtra("invoice_details", ArrayList(selectedDetails))
            startActivityForResult(intent, REQUEST_CODE_SALEE)
        }

        presenter.loadItemsFromDB()
    }

    override fun onResume() {
        super.onResume()
        presenter.loadItemsFromDB()
    }

    override fun updateInventoryList(list: List<InventoryItem>) {
        adapter.updateData(list)
    }

    override fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun openCalculator() {
        val calculatorDialog = CalculatorDialogFragment { result ->
            showMessage("Giá trị máy tính trả về: $result")
            currentTargetTextView?.text = result
        }
        calculatorDialog.show(supportFragmentManager, "CalculatorDialog")
    }

    private fun openCalculatorForQuantity(item: InventoryItem, position: Int) {
        val calculatorDialog = CalculatorDialogFragment { result ->
            showMessage("Giá trị máy tính trả về: $result")
            val quantity = result.toIntOrNull()
            if (quantity != null && quantity >= 0) {
                adapter.updateQuantityAt(position, quantity)
                item.quantity = quantity
                presenter.updateQuantityForItem(item, quantity)
            } else {
                showMessage("Vui lòng nhập số hợp lệ")
            }
        }
        calculatorDialog.show(supportFragmentManager, "CalculatorDialog")
    }

    override fun updateTotalMoney(total: Double) {
        val formattedTotal = String.format(Locale("vi", "VN"), "%,.0f ", total)
        tvTotalMoney.text = formattedTotal
    }

    override fun navigateToInvoice(selectedDetails: List<SAInvoiceDetail>, invoiceItem: SAInvoiceItem) {
        val intent = Intent(this, InvoiceActivity::class.java)
        intent.putParcelableArrayListExtra("EXTRA_INVOICE_DETAILS", ArrayList(selectedDetails))
        intent.putExtra("invoiceItem", invoiceItem)
        intent.putExtra("EXTRA_SO_BAN", btnSetting.text.toString())
        intent.putExtra("EXTRA_SO_KHACH", btnAvatar.text.toString())
        intent.putExtra("EXTRA_TONG_TIEN", tvTotalMoney.text.toString())
        startActivityForResult(intent, REQUEST_CODE_INVOICE)
    }

    override fun getContext(): Context {
        return this
    }

    override fun openInvoiceScreen(refId: String) {
        // Không cần thiết hiện tại
    }

    override fun navigateToSaleeScreen(invoiceItems: List<SAInvoiceItem>) {
        val intent = Intent(this, SaleeActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable("invoiceItems", ArrayList(invoiceItems))
        intent.putExtras(bundle)
        startActivityForResult(intent, REQUEST_CODE_SALEE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_INVOICE && resultCode == SaleeActivity.RESULT_PAYMENT_SUCCESS) {
            // Làm mới danh sách món và tổng tiền
            presenter.clearSelectedItems()
            presenter.loadItemsFromDB()
            showMessage("Thanh toán thành công!")
            // Gửi broadcast để thông báo cho ReportActivity làm mới
            val intent = Intent("com.example.appquanly.REFRESH_REPORT")
            sendBroadcast(intent)
        } else if (requestCode == REQUEST_CODE_SALEE && resultCode == RESULT_OK) {
            val selectedItems = data?.getParcelableArrayListExtra<SAInvoiceDetail>("selected_items")
            selectedItems?.let {
                val selectedInventoryItems = it.map { detail ->
                    InventoryItem(
                        InventoryItemID = detail.InventoryItemID ?: "",
                        InventoryItemCode = null,
                        InventoryItemType = null,
                        InventoryItemName = detail.InventoryItemName,
                        UnitID = null,
                        Price = detail.UnitPrice ?: 0f,
                        Description = null,
                        Inactive = false,
                        CreatedDate = "",
                        CreatedBy = null,
                        ModifiedBy = null,
                        Color = null,
                        IconFileName = null,
                        UseCount = 0,
                        quantity = detail.Quantity?.toInt() ?: 0,
                        isTicked = true
                    )
                }
                adapter.setSelectedItems(selectedInventoryItems)
            }
        }
    }
}