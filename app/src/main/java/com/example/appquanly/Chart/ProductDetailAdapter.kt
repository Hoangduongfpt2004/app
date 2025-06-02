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
    private var dataList: List<Pair<String, Float>>,
    private val timeType: String, // "today", "yesterday", "week", "month", "year"
    private var colors: List<Int> // Danh sách màu sắc
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
        val data = dataList[position]
        val color = if (position < colors.size) colors[position] else ContextCompat.getColor(holder.itemView.context, R.color.colorPrimary)

        val label = try {
            convertPeriodToLabel(timeType, data.first)
        } catch (e: Exception) {
            Log.e("ProductDetailAdapter", "Error parsing label: ${data.first}", e)
            data.first
        }

        holder.tvIndex.text = (position + 1).toString()
        holder.tvPeriod.text = label
        holder.tvAmount.text = String.format("%,.0fđ", data.second)
        holder.tvQuantity.visibility = View.GONE // Ẩn số lượng vì không cần thiết

        val background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color) // Sử dụng màu từ danh sách colors
            setSize(24, 24)
        }
        holder.tvIndex.background = background
        holder.tvIndex.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white))
    }

    override fun getItemCount(): Int = dataList.size

    fun updateData(newList: List<Pair<String, Float>>, newColors: List<Int>) {
        dataList = newList
        colors = newColors
        notifyDataSetChanged()
    }

    private fun convertPeriodToLabel(timeType: String, period: String): String {
        return when (timeType) {
            "today", "yesterday" -> period // Hiển thị tên sản phẩm cho today/yesterday
            "week" -> {
                try {
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val date = sdf.parse(period)
                    val cal = Calendar.getInstance()
                    cal.time = date!!
                    when (cal.get(Calendar.DAY_OF_WEEK)) {
                        Calendar.MONDAY -> "T2"
                        Calendar.TUESDAY -> "T3"
                        Calendar.WEDNESDAY -> "T4"
                        Calendar.THURSDAY -> "T5"
                        Calendar.FRIDAY -> "T6"
                        Calendar.SATURDAY -> "T7"
                        else -> period
                    }
                } catch (e: Exception) {
                    period
                }
            }
            "month" -> {
                try {
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val date = sdf.parse(period)
                    if (date != null) {
                        val cal = Calendar.getInstance()
                        cal.time = date
                        return "Tháng" +  cal.get(Calendar.DAY_OF_MONTH).toString()
                    } else period
                } catch (e: Exception) {
                    period
                }
            }
            "year" -> {
                try {
                    val parts = period.split("-")
                    if (parts.size == 2) {
                        val month = parts[1].toInt()
                        return "Tháng $month"
                    } else period
                } catch (e: Exception) {
                    period
                }
            }
            else -> period
        }
    }
}