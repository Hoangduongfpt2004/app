package com.example.appquanly.Chart

import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanly.R
import java.text.SimpleDateFormat
import java.util.*

class ProductDetailAdapter(
    private var dataList: List<Pair<String, Float>> = emptyList(),
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
        val color = colors.getOrNull(position) ?: ContextCompat.getColor(holder.itemView.context, R.color.colorPrimary)

        val label = data?.first ?: "N/A"

        holder.tvIndex.text = (position + 1).toString()
        holder.tvPeriod.text = label
        holder.tvAmount.text = if (data != null) String.format("%,.0fđ", data.second) else "0đ"
        holder.tvQuantity.visibility = View.GONE

        val background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
            setSize(24, 24)
        }
        holder.tvIndex.background = background
        holder.tvIndex.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white))

        holder.itemView.setOnClickListener {
            onItemClick(label, timeType)
        }
    }

    override fun getItemCount(): Int = dataList.size

    fun updateData(newList: List<Pair<String, Float>>?, newColors: List<Int>?) {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1 // Tháng hiện tại (6)

        // Định dạng lại nhãn dựa trên timeType
        val formattedList = newList?.map { (period, amount) ->
            val formattedPeriod = when (timeType) {
                "week", "last_week" -> {
                    val day = period.split("-")[2].substring(0, 2)
                    val dayMap = mapOf(
                        "01" to "Thứ 2", "02" to "Thứ 2", "03" to "Thứ 3", "04" to "Thứ 4",
                        "05" to "Thứ 5", "06" to "Thứ 6", "07" to "Thứ 7"
                    )
                    dayMap[day] ?: period
                }
                "month", "last_month" -> {
                    val day = period.split("-")[2]
                    "Ngày $day"
                }
                "year", "last_year" -> {
                    val month = period.replace("Tháng ", "").toIntOrNull() ?: 1
                    if (month <= currentMonth || amount > 0) period else null
                }
                else -> period
            }
            if (formattedPeriod != null) Pair(formattedPeriod, amount) else null
        }?.filterNotNull() ?: emptyList()

        dataList = formattedList
        colors = newColors ?: emptyList()
        notifyDataSetChanged()
    }
}