package com.example.appquanly.SalePutIn

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanly.ChooseDish.view.ChooseDishActivity
import com.example.appquanly.Invoice.InvoiceActivity
import com.example.appquanly.R
import com.example.appquanly.data.sqlite.Entity.SAInvoiceDetail

class SaleeAdapter(
    private val invoicesWithDetails: MutableList<InvoiceWithDetails>,
    private val onCancelClick: (InvoiceWithDetails) -> Unit,
    private val onPayClick: (InvoiceWithDetails) -> Unit
) : RecyclerView.Adapter<SaleeAdapter.InvoiceViewHolder>() {

    fun updateData(newList: List<InvoiceWithDetails>) {
        invoicesWithDetails.clear()
        invoicesWithDetails.addAll(newList)
        notifyDataSetChanged()
    }

    fun getData(): List<InvoiceWithDetails> = invoicesWithDetails

    fun getCurrentItems(): List<InvoiceWithDetails> = invoicesWithDetails

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return InvoiceViewHolder(view)
    }

    override fun getItemCount(): Int = invoicesWithDetails.size

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        val item = invoicesWithDetails[position]
        holder.bind(item)
    }

    inner class InvoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDishNames: TextView = itemView.findViewById(R.id.tvDishNames)
        private val tvTotalAmount: TextView = itemView.findViewById(R.id.tvTotalAmount)
        private val tvNumberOfPeople: TextView = itemView.findViewById(R.id.tvNumberOfPeople)
        private val tvTableNumber: TextView = itemView.findViewById(R.id.tvTableNumber)
        private val btnCancel: LinearLayout = itemView.findViewById(R.id.btnCancel)
        private val btnPay: LinearLayout = itemView.findViewById(R.id.btnPay)
        private val flCircleTableNumber: View = itemView.findViewById(R.id.flCircleTableNumber)

        fun bind(itemWithDetails: InvoiceWithDetails) {
            val invoice = itemWithDetails.invoice
            val details = itemWithDetails.details

            val grouped = details
                .groupBy { it.InventoryItemName }
                .map { (name, items) ->
                    val quantity = items.sumOf { it.quantity }
                    val price = items.firstOrNull()?.price ?: 0.0
                    name to (quantity to price)
                }

            val dishNames = grouped.joinToString(", ") { (name, pair) ->
                val (quantity, _) = pair
                "$name ($quantity)"
            }

            val totalAmount = grouped.sumOf { (_, pair) ->
                val (quantity, price) = pair
                quantity * price
            }

            tvDishNames.text = if (dishNames.isNotBlank()) dishNames else "Chưa có món"
            tvTotalAmount.text = "%,.0f đ".format(totalAmount)

            val numberOfPeopleText = invoice.numberOfPeople.takeIf { it > 0 }?.toString() ?: "-"
            tvNumberOfPeople.text = numberOfPeopleText

            tvTableNumber.text = invoice.tableName ?: "-"

            if (invoice.tableName.isNullOrBlank()) {
                flCircleTableNumber.setBackgroundResource(R.drawable.bg_circle_gray)
            } else {
                flCircleTableNumber.setBackgroundResource(R.drawable.circle_green)
            }

            btnPay.setOnClickListener {
                onPayClick(itemWithDetails)
            }

            btnCancel.setOnClickListener {
                showCancelDialog(itemWithDetails)
            }

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, ChooseDishActivity::class.java)
                intent.putExtra("invoiceItem", itemWithDetails.invoice)
                intent.putParcelableArrayListExtra("selectedItems", ArrayList(itemWithDetails.details))
                context.startActivity(intent)
            }
        }

        private fun showCancelDialog(itemWithDetails: InvoiceWithDetails) {
            val context = itemView.context
            val builder = AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            val dialogView = inflater.inflate(R.layout.activity_popup, null)

            builder.setView(dialogView)
            val dialog = builder.create()
            dialog.show()

            val btnCancelAdd = dialogView.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnCancelAdd)
            val btnSaveAdd = dialogView.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnSaveAdd)
            val btnCloseAdd = dialogView.findViewById<ImageView>(R.id.btnCloseAdd)

            btnCancelAdd.setOnClickListener { dialog.dismiss() }
            btnCloseAdd.setOnClickListener { dialog.dismiss() }
            btnSaveAdd.setOnClickListener {
                onCancelClick(itemWithDetails)
                dialog.dismiss()
            }
        }
    }
}