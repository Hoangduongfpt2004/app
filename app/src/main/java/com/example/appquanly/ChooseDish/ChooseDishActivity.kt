package com.example.appquanly.ChooseDish.view

import android.content.Intent
import android.os.Bundle
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
import com.example.appquanly.data.sqlite.Local.InventoryItemRepository

class ChooseDishActivity : AppCompatActivity(), ChooseDishContract.View {

    private lateinit var presenter: ChooseDishPresenter
    private lateinit var tvTotalMoney: TextView
    private lateinit var btnBack: ImageView
    private lateinit var btnSetting: ImageView
    private lateinit var btnAvatar: ImageView
    private lateinit var btnCollectMoney: TextView
    private lateinit var edit_x: TextView
    private lateinit var edit_store: TextView
    private lateinit var adapter: ChooseDishAdapter
    private lateinit var recyclerView: RecyclerView

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
            presenter.onSettingClick()
        }

        btnAvatar.setOnClickListener {
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
            val total = result.toDoubleOrNull() ?: 0.0
            updateTotalMoney(total)
        }
        calculatorDialog.show(supportFragmentManager, "CalculatorDialog")
    }

    override fun updateTotalMoney(total: Double) {
        tvTotalMoney.text = total.toInt().toString()
    }

    override fun navigateToInvoice(selectedItems: List<InventoryItem>) {
        val intent = Intent(this, InvoiceActivity::class.java)
        intent.putParcelableArrayListExtra("selected_items", ArrayList(selectedItems))
        startActivity(intent)
    }
}
