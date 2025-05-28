package com.example.appquanly.SalePutIn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanly.R
import com.example.appquanly.data.sqlite.Entity.SAInvoiceItem

class SaleeAdapter(
    private val invoices: MutableList<SAInvoiceItem>,
    private val onCancelClick: () -> Unit,
    private val onPayClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    private var tableName: String = ""
    private var numberOfPeople: Int = 0

    fun updateData(newList: List<SAInvoiceItem>) {
        invoices.clear()
        invoices.addAll(newList)

        // Lấy dữ liệu header từ item đầu tiên (nếu có)
        tableName = newList.firstOrNull()?.tableName ?: "?"
        numberOfPeople = newList.firstOrNull()?.numberOfPeople ?: 0

        notifyDataSetChanged()
    }

    fun getCurrentItems(): List<SAInvoiceItem> = invoices

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_HEADER else TYPE_ITEM
    }

    override fun getItemCount(): Int = invoices.size + 1 // +1 cho header

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            val view = inflater.inflate(R.layout.item_product, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_product, parent, false)
            ItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.bind(tableName, numberOfPeople)
        } else if (holder is ItemViewHolder) {
            val item = invoices[position - 1]
            holder.bind(item)
        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTableNumber: TextView = itemView.findViewById(R.id.tvTableNumber)
        private val tvNumberOfPeople: TextView = itemView.findViewById(R.id.tvNumberOfPeople)
        private val btnCancel: LinearLayout = itemView.findViewById(R.id.btnCancel)
        private val btnPay: LinearLayout = itemView.findViewById(R.id.btnPay)

        init {
            btnCancel.setOnClickListener { onCancelClick() }
            btnPay.setOnClickListener { onPayClick() }
        }

        fun bind(tableName: String, numberOfPeople: Int) {
            tvTableNumber.text = tableName
            tvNumberOfPeople.text = numberOfPeople.toString()
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTableName: TextView = itemView.findViewById(R.id.tvTableName)
        private val tvTotalAmount: TextView = itemView.findViewById(R.id.tvTotalAmount)

        fun bind(item: SAInvoiceItem) {
            tvTableName.text = item.listItemName ?: ""
            tvTotalAmount.text = "%,.0f đ".format(item.amount)
        }
    }
}
