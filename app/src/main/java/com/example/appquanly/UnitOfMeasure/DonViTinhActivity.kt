package com.example.appquanly.UnitOfMeasure

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanly.R
import com.google.android.material.appbar.MaterialToolbar

class DonViTinhActivity : AppCompatActivity(), DonViTinhContract.View {

    private lateinit var presenter: DonViTinhContract.Presenter
    private lateinit var adapter: DonViTinhAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unit_of_measure)

        presenter = DonViTinhPresenter(this, UnitRepository(this))


        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }
        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.menu_add) {
                showAddDialog()
                true
            } else false
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewDonVi)
        adapter = DonViTinhAdapter(
            onItemClick = { position -> presenter.selectItem(position) },
            onEditClick = { position -> showEditDialog(position) } // xử lý nút sửa
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

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

    private fun showEditDialog(position: Int) {
        val item = adapter.getItems()[position]

        val dialogView = layoutInflater.inflate(R.layout.activity_add_product, null)

        val editText = dialogView.findViewById<EditText>(R.id.edtNewUnitName)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveAdd)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelAdd)

        editText.setText(item.name)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnSave.text = "Lưu"

        btnSave.setOnClickListener {
            val newName = editText.text.toString()
            if (newName.isNotEmpty()) {
                presenter.editDonViTinh(position, newName)
                dialog.dismiss()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun showList(donViTinhs: List<DonViTinh>) {
        adapter.setItems(donViTinhs)
    }

    override fun updateList() {
        adapter.notifyDataSetChanged()
    }
}
