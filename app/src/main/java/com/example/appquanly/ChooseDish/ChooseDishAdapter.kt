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
        val ivTick: ImageView = itemView.findViewById(R.id.ivTick) // icon tick trong item
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

        holder.tvName.text = item.InventoryItemName ?: "Tên món trống"

        val priceFormatted = item.Price?.let {
            val format = NumberFormat.getNumberInstance(Locale("vi", "VN"))
            format.format(it) + " đ"
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

        val currentQuantity = item.quantity ?: 0
        val isTicked = item.isTicked ?: false

        if (isTicked) {
            // Nếu đã tick: ẩn nút tăng giảm, ẩn số lượng, chỉ hiện tick
            holder.layoutQuantity.visibility = View.GONE
            holder.ivTick.visibility = View.VISIBLE
        } else {
            // Chưa tick
            if (currentQuantity > 0) {
                holder.layoutQuantity.visibility = View.VISIBLE
                holder.tvQuantity.text = currentQuantity.toString()
                holder.ivTick.visibility = View.VISIBLE
            } else {
                holder.layoutQuantity.visibility = View.GONE
                holder.tvQuantity.text = ""
                holder.ivTick.visibility = View.GONE // Ẩn dấu tích luôn nếu quantity = 0 và chưa tick
            }
        }

        // Bấm vào dấu tick 1 lần là reset và ẩn dấu tích luôn
        holder.ivTick.setOnClickListener {
            if (isTicked) {
                // Bỏ tick: reset số lượng về 0 và ẩn tick
                item.isTicked = false
                item.quantity = 0
            } else {
                // Tick: ẩn nút tăng giảm, giữ nguyên quantity nếu cần
                item.isTicked = true
            }
            notifyItemChanged(position)
        }

        if (!isTicked) {

            holder.btnIncrease.setOnClickListener {
                listener.onIncrease(item)
            }

            holder.btnDecrease.setOnClickListener {
                listener.onDecrease(item)
            }

            holder.tvQuantity.setOnClickListener {
                listener.onQuantityClick(item, position)
            }


            holder.itemView.setOnClickListener {
                listener.onIncrease(item)
            }

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
}
