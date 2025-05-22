package com.example.appquanly.MenuCategory

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanly.R
import com.example.appquanly.data.sqlite.Entity.InventoryItem
import java.io.IOException
import java.text.NumberFormat
import java.util.*

class InventoryAdapter(
    private val itemList: MutableList<InventoryItem>,
    private val context: Context,
    private val onItemClick: (InventoryItem) -> Unit,
    private val onInactiveChanged: (InventoryItem, Boolean) -> Unit
) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)
        val imageViewIcon: ImageView = itemView.findViewById(R.id.imageViewIcon)
        val tvTrangThai: TextView = itemView.findViewById(R.id.tvTrangThai)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inventory, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]

        holder.textViewName.text = item.InventoryItemName ?: "No Name"
        holder.textViewPrice.text = item.Price?.let { "Giá tiền: ${formatCurrencyVND(it)}" } ?: "No Price"

        val inactive = item.Inactive == true

        if (inactive) {
            holder.tvTrangThai.visibility = View.VISIBLE
            holder.textViewName.setTextColor(Color.GRAY)
            holder.textViewPrice.setTextColor(Color.GRAY)
        } else {
            holder.tvTrangThai.visibility = View.GONE
            holder.textViewName.setTextColor(Color.BLACK)
            holder.textViewPrice.setTextColor(Color.BLACK)
        }

        item.Color?.let { colorStr ->
            try {
                holder.imageViewIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorStr))
            } catch (e: IllegalArgumentException) {
                holder.imageViewIcon.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
            }
        } ?: run {
            holder.imageViewIcon.backgroundTintList = null
        }

        if (!item.IconFileName.isNullOrEmpty()) {
            val assetPath = "icondefault/${item.IconFileName}"
            try {
                context.assets.open(assetPath).use { inputStream ->
                    val drawable = Drawable.createFromStream(inputStream, null)
                    holder.imageViewIcon.setImageDrawable(drawable ?: context.getDrawable(R.drawable.ic_default))
                }
            } catch (e: IOException) {
                Log.e("InventoryAdapter", "Cannot open asset $assetPath", e)
                holder.imageViewIcon.setImageResource(R.drawable.ic_default)
            }
        } else {
            holder.imageViewIcon.setImageResource(R.drawable.ic_default)
        }

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = itemList.size

    fun updateData(newList: List<InventoryItem>) {
        itemList.clear()
        itemList.addAll(newList)
        notifyDataSetChanged()
    }

    private fun formatCurrencyVND(amount: Float): String {
        val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        return format.format(amount)
    }
}
