package com.example.appquanly.UnitOfMeasure

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanly.R
import com.example.appquanly.data.sqlite.Entity.UnitOfMeasure
import com.example.appquanly.data.sqlite.Local.UnitRepository
import com.google.android.material.appbar.MaterialToolbar
import java.time.LocalDateTime

class Unit_Of_MeasureActivity : AppCompatActivity(), Unit_Of_MeasureContract.View {

    private lateinit var presenter: Unit_Of_MeasureContract.Presenter
    private lateinit var adapter: Unit_Of_MeasureAdapter
    private var selectedUnitName: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unit_of_measure)

        presenter = Unit_Of_MeasurePresenter(this, UnitRepository(this))

        // Nhận đơn vị đã chọn từ Intent hoặc từ SharedPreferences (nếu cần)
        selectedUnitName = intent.getStringExtra("SELECTED_UNIT_NAME")
            ?: getSharedPreferences("AppPrefs", Context.MODE_PRIVATE).getString("LAST_SELECTED_UNIT", null)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }
        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.menu_add) {
                showAddDialog()
                true
            } else false
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewDonVi)
        adapter = Unit_Of_MeasureAdapter(
            onItemClick = { selectedUnit ->
                presenter.selectItem(selectedUnit)

                // Lưu đơn vị vừa chọn vào SharedPreferences để giữ lại trạng thái
                val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().putString("LAST_SELECTED_UNIT", selectedUnit.UnitName).apply()

                // Trả kết quả về màn hình trước
                val resultIntent = Intent().apply {
                    putExtra("SELECTED_UNIT", selectedUnit.UnitName)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            },
            onEditClick = { unit -> showEditDialog(unit) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        findViewById<Button>(R.id.btnDone).setOnClickListener { finish() }

        presenter.loadData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showAddDialog() {
        val dialogView = layoutInflater.inflate(R.layout.activiti_dialog_edit_unit, null)
        val editText = dialogView.findViewById<EditText>(R.id.edtUnitName)
        val btnAdd = dialogView.findViewById<Button>(R.id.btnSave)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        btnAdd.setOnClickListener {
            val donViTinh = editText.text.toString()
            if (donViTinh.isNotEmpty()) {
                presenter.addDonViTinh(donViTinh)
                dialog.dismiss()
            }
        }

        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showEditDialog(unit: UnitOfMeasure) {
        val dialogView = layoutInflater.inflate(R.layout.activiti_dialog_edit_unit, null)

        val editText = dialogView.findViewById<EditText>(R.id.edtUnitName)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val textTitle = dialogView.findViewById<TextView>(R.id.txtTitle)

        editText.setText(unit.UnitName)


        textTitle.text = "Sửa đơn vị tính"
        btnSave.text = "LƯU"

        val dialog = AlertDialog.Builder(this).setView(dialogView).create()

        btnSave.setOnClickListener {
            val newName = editText.text.toString()
            if (newName.isNotEmpty()) {
                unit.UnitName = newName
                unit.ModifiedDate = LocalDateTime.now()
                unit.ModifiedBy = "admin"
                presenter.updateUnit(unit)
                adapter.updateItem(unit)
                dialog.dismiss()
            }
        }

        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }


    override fun showList(units: List<UnitOfMeasure>) {
        // Đánh dấu đơn vị đang được chọn để hiển thị dấu tích
        val updatedList = units.map {
            it.Inactive = it.UnitName == selectedUnitName
            it
        }
        adapter.setItems(updatedList)
    }

    override fun updateList() {
        adapter.notifyDataSetChanged()
    }

    override fun addItemToList(unit: UnitOfMeasure) {
        adapter.addItem(unit)
    }
}
