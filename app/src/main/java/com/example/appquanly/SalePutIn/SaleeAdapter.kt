package com.example.appquanly.SalePutIn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanly.R
import com.example.appquanly.data.sqlite.Entity.SAInvoiceDetail
import com.example.appquanly.data.sqlite.Entity.SAInvoiceItem

class SaleeAdapter(
    private val invoices: MutableList<SAInvoiceItem>,
) : RecyclerView.Adapter<SaleeAdapter.ViewHolder>() {

    private val items = mutableListOf<SAInvoiceDetail>()
    private val onCancelClick: ((position: Int) -> Unit)? = null
    private val onPayClick: ((position: Int) -> Unit)? = null

    fun submitList(newItems: List<SAInvoiceDetail>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        if (position in items.indices) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTableNumber: TextView = view.findViewById(R.id.tvTableNumber)
        val tvNumberOfPeople: TextView = view.findViewById(R.id.tvNumberOfPeople)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val btnCancel: LinearLayout = view.findViewById(R.id.btnCancel)
        val btnPay: LinearLayout = view.findViewById(R.id.btnPay)

        init {
            btnCancel.setOnClickListener {
                onCancelClick?.invoke(bindingAdapterPosition)
            }
            btnPay.setOnClickListener {
                onPayClick?.invoke(bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = invoices.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = invoices[position]

        // Hiển thị số bàn và số người từ hóa đơn tổng (invoiceItem)
        holder.tvTableNumber.text = item.tableName ?: "?"
        holder.tvNumberOfPeople.text = item.numberOfPeople.toString()

        // Hiển thị tên món kèm số lượng
        holder.tvName.text = item.listItemName

        // Hiển thị giá tiền (đơn giá * số lượng)
        val totalPrice = item.amount
        holder.tvPrice.text = "%,.0f đ".format(totalPrice)

    }
}
