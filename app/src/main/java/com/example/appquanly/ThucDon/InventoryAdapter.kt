package com.example.appquanly.ThucDon

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanly.R
import java.io.IOException

class InventoryAdapter(private val itemList: List<InventoryItem>, private val context: Context) :
    RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewPrice: TextView = itemView.findViewById(R.id.textViewPrice)
        val imageViewIcon: ImageView = itemView.findViewById(R.id.imageViewIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inventory, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.textViewName.text = item.InventoryItemName ?: "No Name"
        holder.textViewPrice.text = item.Price?.let { "Price: $%.2f".format(it) } ?: "No Price"


        // Áp dụng background dựa trên Color từ InventoryItem
                item.Color?.let { color ->
                    try {
                        holder.imageViewIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
                    } catch (e: IllegalArgumentException) {
                        holder.imageViewIcon.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                    }
                }

        // Tải icon từ assets
        item.IconFileName?.let { iconName ->
            try {
                context.assets.open(iconName).use { inputStream ->
                    val drawable = Drawable.createFromStream(inputStream, null)
                    holder.imageViewIcon.setImageDrawable(drawable)
                }
            } catch (e: IOException) {
                holder.imageViewIcon.setImageResource(R.drawable.ic_default) // Fallback icon
            }
        } ?: holder.imageViewIcon.setImageResource(R.drawable.ic_default)
    }

    override fun getItemCount(): Int = itemList.size
}