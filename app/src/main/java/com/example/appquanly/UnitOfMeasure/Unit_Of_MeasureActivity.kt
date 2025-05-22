package com.example.appquanly.UnitOfMeasure

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unit_of_measure)

        presenter = Unit_Of_MeasurePresenter(this, UnitRepository(this))

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

        // Thêm dòng kẻ ngang (divider) cho RecyclerView
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            DividerItemDecoration.VERTICAL
        )
        // Nếu muốn dùng drawable custom, tạo file res/drawable/divider_line.xml rồi uncomment đoạn dưới:
        /*
        val dividerDrawable = ContextCompat.getDrawable(this, R.drawable.divider_line)
        if (dividerDrawable != null) {
            dividerItemDecoration.setDrawable(dividerDrawable)
        }
        */
        recyclerView.addItemDecoration(dividerItemDecoration)

        findViewById<Button>(R.id.btnDone).setOnClickListener {
            finish()
        }

        presenter.loadData()
    }

    private fun showAddDialog() {
        val dialogView = layoutInflater.inflate(R.layout.activiti_dialog_edit_unit, null)

        val editText = dialogView.findViewById<EditText>(R.id.edtUnitName)
        val btnAdd = dialogView.findViewById<Button>(R.id.btnSave)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnAdd.setOnClickListener {
            val donViTinh = editText.text.toString()
            if (donViTinh.isNotEmpty()) {
                presenter.addDonViTinh(donViTinh)
                dialog.dismiss()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showEditDialog(unit: UnitOfMeasure) {
        val dialogView = layoutInflater.inflate(R.layout.activiti_dialog_edit_unit, null)

        val editText = dialogView.findViewById<EditText>(R.id.edtUnitName)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        editText.setText(unit.UnitName)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

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

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun showList(units: List<UnitOfMeasure>) {
        adapter.setItems(units)
    }

    override fun updateList() {
        adapter.notifyDataSetChanged()
    }
}
