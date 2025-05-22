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
        fun onQuantityClick(item: InventoryItem, position: Int)
    }

    private val expandedPositions = mutableSetOf<Int>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvTenMon)
        val tvPrice: TextView = itemView.findViewById(R.id.tvGia)
        val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val btnIncrease: ImageView = itemView.findViewById(R.id.btnIncrease)
        val btnDecrease: ImageView = itemView.findViewById(R.id.btnDecrease)
        val layoutQuantity: LinearLayout = itemView.findViewById(R.id.layoutQuantity)
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

        holder.tvName.text = item.InventoryItemName  ?: "Tên món trống"

        val priceFormatted = item.Price?.toInt()?.let {
            String.format("%,d đ", it)
        } ?: "0 đ"
        holder.tvPrice.text = priceFormatted

        item.IconFileName?.let { iconName ->
            val assetPath = "icondefault/$iconName"
            try {
                context.assets.open(assetPath).use { inputStream ->
                    val drawable = Drawable.createFromStream(inputStream, null)
                    holder.ivIcon.setImageDrawable(drawable)
                }
            } catch (e: IOException) {
                holder.ivIcon.setImageResource(R.drawable.ic_default)
            }
        } ?: holder.ivIcon.setImageResource(R.drawable.ic_default)

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

        // Lấy số lượng (quantity) từ InventoryItem, mặc định 0 nếu null
        val currentQuantity = item.quantity ?: 0
        holder.tvQuantity.text = currentQuantity.toString()

        // Hiển thị layout quantity nếu item đang mở rộng
        holder.layoutQuantity.visibility = if (expandedPositions.contains(position)) View.VISIBLE else View.GONE

        // Nút tăng số lượng
        holder.btnIncrease.setOnClickListener {
            listener.onIncrease(item)
        }

        // Nút giảm số lượng
        holder.btnDecrease.setOnClickListener {
            listener.onDecrease(item)
        }

        // Click vào số lượng để mở giao diện nhập thủ công hoặc xử lý khác
        holder.tvQuantity.setOnClickListener {
            listener.onQuantityClick(item, position)
        }

        // Click vào toàn bộ item để mở/thu gọn phần quantity
        holder.itemView.setOnClickListener {
            if (expandedPositions.contains(position)) {
                expandedPositions.remove(position)
            } else {
                expandedPositions.add(position)
            }
            notifyItemChanged(position)
        }
    }

    // Cập nhật toàn bộ danh sách và refresh adapter
    fun updateData(newList: List<InventoryItem>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    // Cập nhật số lượng cho 1 item tại vị trí và refresh item đó
    fun updateQuantityAt(position: Int, quantity: Int) {
        if (position in list.indices) {
            val item = list[position]
            item.quantity = quantity
            notifyItemChanged(position)
        }
    }
}
