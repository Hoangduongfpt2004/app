package com.example.appquanly.UnitOfMeasure

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanly.R
import com.example.appquanly.data.sqlite.Entity.UnitOfMeasure

class Unit_Of_MeasureAdapter(
    private val onItemClick: (UnitOfMeasure) -> Unit,
    private val onEditClick: (UnitOfMeasure) -> Unit
) : RecyclerView.Adapter<Unit_Of_MeasureAdapter.ViewHolder>() {

    private val items = mutableListOf<UnitOfMeasure>()

    fun setItems(newItems: List<UnitOfMeasure>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun addItem(unit: UnitOfMeasure) {
        items.add(unit)
        notifyItemInserted(items.size - 1)
    }

    fun updateItem(updated: UnitOfMeasure) {
        val index = items.indexOfFirst { it.UnitID == updated.UnitID }
        if (index != -1) {
            items[index] = updated
            notifyItemChanged(index)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName: TextView = view.findViewById(R.id.txtName)
        val iconEdit: ImageView = view.findViewById(R.id.iconEdit)
        val iconCheck: ImageView = view.findViewById(R.id.iconCheck)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_unit, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.txtName.text = item.UnitName
        holder.iconCheck.visibility = if (item.Inactive) View.VISIBLE else View.INVISIBLE

        holder.itemView.setOnClickListener {
            // Bỏ chọn tất cả
            items.forEach { it.Inactive = false }
            // Chọn item này
            item.Inactive = true
            notifyDataSetChanged()
            onItemClick(item)
        }

        holder.iconEdit.setOnClickListener {
            onEditClick(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
