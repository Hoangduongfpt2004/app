package com.example.appquanly.MenuCategory

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanly.AddDish.Add_DishActivity
import com.example.appquanly.R
import com.example.appquanly.SalePutIn.BaseDrawerActivity
import com.example.appquanly.data.sqlite.Entity.InventoryItem
import com.example.appquanly.data.sqlite.Local.InventoryItemRepository

class Menu_CategoryActivity : BaseDrawerActivity(), Menu_CategoryContract.View {

    private lateinit var btnAdd: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InventoryAdapter
    private val itemList = mutableListOf<InventoryItem>()
    private lateinit var presenter: Menu_CategoryContract.Presenter

    private val themMonLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            presenter.loadThucDon()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_thuc_don
    }

    override fun getNavigationMenuItemId(): Int {
        return R.id.thuc_don  // ID menu tương ứng trong NavigationView XML
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        btnAdd = findViewById(R.id.btnAdd)
        recyclerView = findViewById(R.id.rvProducts)

        presenter = Menu_CategoryPresenter(this, InventoryItemRepository(this))

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Thêm divider giữa các item
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(this, R.drawable.divider)?.let {
            divider.setDrawable(it)
        }
        recyclerView.addItemDecoration(divider)

        adapter = InventoryAdapter(itemList, this, { item ->
            val intent = Intent(this, Add_DishActivity::class.java).apply {
                putExtra("isEdit", true)
                putExtra("dishId", item.InventoryItemID)
            }
            themMonLauncher.launch(intent)
        }, { item, isInactive ->
            // TODO: Cập nhật trạng thái trong DB nếu cần
        })

        recyclerView.adapter = adapter

        btnAdd.setOnClickListener {
            val intent = Intent(this, Add_DishActivity::class.java)
            themMonLauncher.launch(intent)
        }

        presenter.loadThucDon()
    }

    override fun showThucDon(items: List<InventoryItem>) {
        adapter.updateData(items)
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
