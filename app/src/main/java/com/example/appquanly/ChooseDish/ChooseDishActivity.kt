package com.example.appquanly.ChooseDish.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanly.CashRegister.CalculatorDialogFragment
import com.example.appquanly.ChooseDish.contract.ChooseDishContract
import com.example.appquanly.ChooseDish.presenter.ChooseDishPresenter
import com.example.appquanly.Invoice.InvoiceActivity
import com.example.appquanly.R
import com.example.appquanly.SalePutIn.SaleeActivity
import com.example.appquanly.data.sqlite.Entity.InventoryItem
import com.example.appquanly.data.sqlite.Entity.SAInvoiceDetail
import com.example.appquanly.data.sqlite.Local.InventoryItemRepository

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
                presenter.onIncreaseItem(item)
            }

            override fun onDecrease(item: InventoryItem) {
                presenter.onDecreaseItem(item)
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
            val intent = Intent(this, SaleeActivity::class.java)
            startActivity(intent)
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
                presenter.updateQuantityForItem(item, quantity)
            } else {
                showMessage("Giá trị nhập không hợp lệ!")
            }
        }
        calculatorDialog.show(supportFragmentManager, "CalculatorDialog")
    }

    override fun updateTotalMoney(total: Double) {
        tvTotalMoney.text = total.toString()
    }

    override fun navigateToInvoice(selectedDetails: List<SAInvoiceDetail>) {
        val intent = Intent(this, InvoiceActivity::class.java)
        intent.putParcelableArrayListExtra("invoice_details", ArrayList(selectedDetails))
        startActivity(intent)
        finish()
    }


    override fun getContext() = this
}
