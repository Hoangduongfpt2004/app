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
import java.text.NumberFormat
import java.util.Locale

class ChooseDishAdapter(
    private val list: MutableList<InventoryItem>,
    private val listener: OnDishActionListener
) : RecyclerView.Adapter<ChooseDishAdapter.ViewHolder>() {

    interface OnDishActionListener {
        fun onIncrease(item: InventoryItem)
        fun onDecrease(item: InventoryItem)
        fun onQuantityClick(item: InventoryItem, position: Int)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvTenMon)
        val tvPrice: TextView = itemView.findViewById(R.id.tvGia)
        val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val btnIncrease: ImageView = itemView.findViewById(R.id.btnIncrease)
        val btnDecrease: ImageView = itemView.findViewById(R.id.btnDecrease)
        val layoutQuantity: LinearLayout = itemView.findViewById(R.id.layoutQuantity)
        val ivTick: ImageView = itemView.findViewById(R.id.ivTick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dish, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val context = holder.itemView.context

        // Tên và giá
        holder.tvName.text = item.InventoryItemName ?: "Tên món trống"
        holder.tvPrice.text = item.Price?.let {
            NumberFormat.getNumberInstance(Locale("vi", "VN")).format(it) + ""
        } ?: "0 đ"

        // Icon món
        val iconName = item.IconFileName
        if (!iconName.isNullOrEmpty()) {
            val assetPath = "icondefault/$iconName"
            try {
                context.assets.open(assetPath).use { inputStream ->
                    val drawable = Drawable.createFromStream(inputStream, null)
                    holder.ivIcon.setImageDrawable(drawable)
                }
            } catch (e: IOException) {
                holder.ivIcon.setImageResource(R.drawable.ic_default)
            }
        } else {
            holder.ivIcon.setImageResource(R.drawable.ic_default)
        }

        // Màu nền icon
        item.Color?.let {
            try {
                holder.ivIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor(it))
            } catch (e: Exception) {
                holder.ivIcon.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
            }
        } ?: run {
            holder.ivIcon.backgroundTintList = null
        }

        val quantity = item.quantity ?: 0
        val isTicked = item.isTicked ?: false

        if (isTicked) {
            // Nếu đã tick: ẩn tăng giảm, ẩn layout số lượng, chỉ hiện tick
            holder.layoutQuantity.visibility = View.GONE
            holder.ivTick.visibility = View.VISIBLE
        } else {
            if (quantity > 0) {
                holder.layoutQuantity.visibility = View.VISIBLE
                holder.tvQuantity.text = quantity.toString()
                holder.ivTick.visibility = View.VISIBLE
            } else {
                holder.layoutQuantity.visibility = View.GONE
                holder.tvQuantity.text = ""
                holder.ivTick.visibility = View.GONE
            }
        }

        // Click tick để bỏ tick hoặc tick lại
        holder.ivTick.setOnClickListener {
            if (item.isTicked == true) {
                item.isTicked = false
                item.quantity = 0
            } else {
                item.isTicked = true
            }
            notifyItemChanged(position)
        }

        // Set click nếu chưa bị tick
        if (!isTicked) {
            holder.btnIncrease.setOnClickListener { listener.onIncrease(item) }
            holder.btnDecrease.setOnClickListener { listener.onDecrease(item) }
            holder.tvQuantity.setOnClickListener { listener.onQuantityClick(item, position) }
            holder.itemView.setOnClickListener { listener.onIncrease(item) }
        } else {
            holder.btnIncrease.setOnClickListener(null)
            holder.btnDecrease.setOnClickListener(null)
            holder.tvQuantity.setOnClickListener(null)
            holder.itemView.setOnClickListener(null)
        }
    }

    fun updateData(newList: List<InventoryItem>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    fun updateQuantityAt(position: Int, quantity: Int) {
        if (position in list.indices) {
            list[position].quantity = quantity
            notifyItemChanged(position)
        }
    }

    fun setSelectedItems(selectedList: List<InventoryItem>) {
        for (item in list) {
            val matched = selectedList.find { it.InventoryItemID == item.InventoryItemID }
            item.isTicked = matched?.isTicked ?: false
            item.quantity = matched?.quantity ?: 0
        }
        notifyDataSetChanged()
    }
}