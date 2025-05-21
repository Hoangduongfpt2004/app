package com.example.appquanly.ChooseDish.view

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanly.R
import com.example.appquanly.data.sqlite.Entity.InventoryItem
import java.io.IOException

class ChooseDishAdapter(
    private val list: MutableList<InventoryItem>,
    private val listener: OnDishActionListener
) : RecyclerView.Adapter<ChooseDishAdapter.ViewHolder>() {

    interface OnDishActionListener {
        fun onIncrease(item: InventoryItem)
        fun onDecrease(item: InventoryItem)
    }

    // Map để lưu số lượng món theo ID
    private val quantityMap = mutableMapOf<String, Int>()

    // Set lưu vị trí các item đang mở (có layout tăng giảm hiển thị)
    private val expandedPositions = mutableSetOf<Int>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvTenMon)
        val tvPrice: TextView = itemView.findViewById(R.id.tvGia)
        val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val btnIncrease: ImageView = itemView.findViewById(R.id.btnIncrease)
        val btnDecrease: ImageView = itemView.findViewById(R.id.btnDecrease)
        val layoutQuantity: LinearLayout =
            itemView.findViewById(R.id.layoutQuantity)  // layout bao quanh số lượng + nút
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_dish, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val context = holder.itemView.context

        // Tên món
        holder.tvName.text = item.InventoryItemName ?: "Tên món trống"

        // Giá
        val priceFormatted = item.Price?.toInt()?.let {
            String.format("%,d đ", it)
        } ?: "0 đ"
        holder.tvPrice.text = priceFormatted

        // Load icon từ assets
        item.IconFileName?.let { iconName ->
            try {
                context.assets.open(iconName).use { inputStream ->
                    val drawable = Drawable.createFromStream(inputStream, null)
                    holder.ivIcon.setImageDrawable(drawable)
                }
            } catch (e: IOException) {
                holder.ivIcon.setImageResource(R.drawable.ic_default)
            }
        } ?: holder.ivIcon.setImageResource(R.drawable.ic_default)

        // Màu icon
        item.Color?.let { colorString ->
            try {
                holder.ivIcon.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(colorString))
            } catch (e: IllegalArgumentException) {
                holder.ivIcon.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
            }
        } ?: run {
            holder.ivIcon.backgroundTintList = null
        }

        val id = item.InventoryItemID ?: return
        val currentQuantity = quantityMap[id] ?: 0
        holder.tvQuantity.text = currentQuantity.toString()

        // Hiển thị hoặc ẩn layout số lượng tùy theo expandedPositions có chứa position hay không
        holder.layoutQuantity.visibility = if (expandedPositions.contains(position)) View.VISIBLE else View.GONE

        // Xử lý tăng số lượng
        holder.btnIncrease.setOnClickListener {
            val newQuantity = currentQuantity + 1
            quantityMap[id] = newQuantity
            holder.tvQuantity.text = newQuantity.toString()
            listener.onIncrease(item)
        }

        // Xử lý giảm số lượng
        holder.btnDecrease.setOnClickListener {
            val newQuantity = if (currentQuantity > 0) currentQuantity - 1 else 0
            quantityMap[id] = newQuantity
            holder.tvQuantity.text = newQuantity.toString()
            listener.onDecrease(item)

            if (newQuantity == 0 && expandedPositions.contains(position)) {
                expandedPositions.remove(position)
                notifyItemChanged(position)
            }
        }

        // Xử lý click vào item để mở/đóng phần số lượng
        holder.itemView.setOnClickListener {
            if (expandedPositions.contains(position)) {
                expandedPositions.remove(position)
            } else {
                expandedPositions.add(position)
            }
            notifyItemChanged(position)
        }
    }

    fun updateData(newList: List<InventoryItem>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
    fun getQuantityMap(): Map<String, Int> = quantityMap

    fun getCurrentItems(): List<InventoryItem> {
        return list
    }


}
