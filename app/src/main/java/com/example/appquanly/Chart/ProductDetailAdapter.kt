package com.example.appquanly.Chart

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanly.R
import java.util.Calendar

class ProductDetailAdapter(
    private var dataList: List<Triple<String, Int, Float>> = emptyList(),
    private val timeType: String,
    private var colors: List<Int> = emptyList(),
    private val onItemClick: (String, String) -> Unit
) : RecyclerView.Adapter<ProductDetailAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIndex: TextView = itemView.findViewById(R.id.tvIndex)
        val tvPeriod: TextView = itemView.findViewById(R.id.tvProductName)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report_detail, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList.getOrNull(position)
        val color = colors.getOrNull(position) ?: ContextCompat.getColor(
            holder.itemView.context,
            R.color.colorPrimary
        )

        val label = data?.first ?: "N/A"
        val quantity = data?.second ?: 0
        val amount = data?.third ?: 0f

        holder.tvIndex.text = (position + 1).toString()
        holder.tvPeriod.text = label
        holder.tvAmount.text = String.format("%,.0f", amount)
        holder.tvQuantity.text = "SL: $quantity"
        holder.tvQuantity.visibility = View.VISIBLE

        val background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
            setSize(24, 24)
        }
        holder.tvIndex.background = background
        holder.tvIndex.setTextColor(
            ContextCompat.getColor(
                holder.itemView.context,
                android.R.color.white
            )
        )

        holder.itemView.setOnClickListener {
            onItemClick(label, timeType)
        }
    }

    override fun getItemCount(): Int = dataList.size

    fun updateData(newList: List<Triple<String, Int, Float>>?, newColors: List<Int>?) {
        dataList = when (timeType) {
            "week", "last_week" -> newList?.sortedBy {
                val dayOrder = listOf("Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7")
                dayOrder.indexOf(it.first)
            }?.filter { it.third > 0 || timeType == "week" } ?: emptyList()

            "month", "last_month" -> newList?.sortedBy {
                it.first.replace("Ngày ", "").toIntOrNull() ?: Int.MAX_VALUE
            }?.filter {
                it.third > 0 || (timeType == "month" && (it.first.replace("Ngày ", "").toIntOrNull()
                    ?: 0) <= Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
            } ?: emptyList()

            "year" -> newList?.sortedBy {
                it.first.replace("Tháng ", "").toIntOrNull() ?: Int.MAX_VALUE
            }?.filter {
                val month = it.first.replace("Tháng ", "").toIntOrNull() ?: 0
                month <= Calendar.getInstance().get(Calendar.MONTH) + 1 || it.third > 0
            } ?: emptyList()

            "last_year" -> newList?.sortedBy {
                it.first.replace("Tháng ", "").toIntOrNull() ?: Int.MAX_VALUE
            }?.filter { it.third > 0 } ?: emptyList()

            else -> newList?.sortedByDescending { it.third } ?: emptyList()
        }
        colors = newColors ?: emptyList()
        notifyDataSetChanged()
    }
}