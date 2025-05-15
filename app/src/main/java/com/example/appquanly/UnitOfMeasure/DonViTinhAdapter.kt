package com.example.appquanly.UnitOfMeasure

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanly.R

class DonViTinhAdapter(
    private val onItemClick: (Int) -> Unit,
    private val onEditClick: (Int) -> Unit
) : RecyclerView.Adapter<DonViTinhAdapter.ViewHolder>() {

    private var items: List<DonViTinh> = listOf()

    fun setItems(list: List<DonViTinh>) {
        items = list
        notifyDataSetChanged()
    }

    fun getItems(): List<DonViTinh> {
        return items
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val iconEdit: ImageView = itemView.findViewById(R.id.iconEdit)
        val iconCheck: ImageView = itemView.findViewById(R.id.iconCheck)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_unit, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.txtName.text = item.name
        holder.iconCheck.visibility = if (item.isSelected) View.VISIBLE else View.INVISIBLE

        // Bấm vào item thì chọn
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }

        // Bấm vào icon sửa thì gọi callback sửa
        holder.iconEdit.setOnClickListener {
            onEditClick(position)
        }
    }

    override fun getItemCount(): Int = items.size
}
