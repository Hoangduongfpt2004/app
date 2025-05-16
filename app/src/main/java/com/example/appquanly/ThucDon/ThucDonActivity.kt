package com.example.appquanly.ThucDon

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanly.AddDish.ThemMonActivity
import com.example.appquanly.R
import com.google.android.material.navigation.NavigationView

class ThucDonActivity : AppCompatActivity(), ThucDonContract.View {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var btnAdd: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InventoryAdapter
    private val itemList = mutableListOf<InventoryItem>()
    private lateinit var presenter: ThucDonContract.Presenter

    private val themMonLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            presenter.loadThucDon() // Refresh danh sách
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thuc_don)

        // Ánh xạ view
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        toolbar = findViewById(R.id.toolbar)
        btnAdd = findViewById(R.id.btnAdd)
        recyclerView = findViewById(R.id.rvProducts)

        // Khởi tạo Presenter
        presenter = ThucDonPresenter(this, InventoryItemRepository(this))

        // Thiết lập toolbar
        setSupportActionBar(toolbar)

        // Toggle mở/đóng drawer
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open, R.string.close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Thiết lập RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = InventoryAdapter(itemList, this) // Truyền context
        recyclerView.adapter = adapter

        // Nút thêm
        btnAdd.setOnClickListener {
            val intent = Intent(this, ThemMonActivity::class.java)
            themMonLauncher.launch(intent)
        }

        // Load dữ liệu
        presenter.loadThucDon()
    }

    override fun showThucDon(items: List<InventoryItem>) {
        itemList.clear()
        itemList.addAll(items)
        adapter.notifyDataSetChanged()
    }

    override fun showError(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}