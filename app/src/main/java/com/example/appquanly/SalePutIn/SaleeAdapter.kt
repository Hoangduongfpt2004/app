package com.example.appquanly.SalePutIn

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquanly.R

class ProductAdapter(
    private var productList: List<SaleeModel>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    // Callback để xử lý sự kiện từ bên ngoài (Activity/Presenter)
    var onCancelClicked: ((SaleeModel) -> Unit)? = null
    var onPayClicked: ((SaleeModel) -> Unit)? = null

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val btnCancel: LinearLayout = itemView.findViewById(R.id.btnCancel)
        val btnPay: LinearLayout = itemView.findViewById(R.id.btnPay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.tvName.text = product.name
        holder.tvPrice.text = product.price

        // Bắt sự kiện click Hủy
        holder.btnCancel.setOnClickListener {
            onCancelClicked?.invoke(product)
        }

        // Bắt sự kiện click Thu tiền
        holder.btnPay.setOnClickListener {
            onPayClicked?.invoke(product)
        }
    }

    override fun getItemCount(): Int = productList.size

    fun updateData(newList: List<SaleeModel>) {
        productList = newList
        notifyDataSetChanged()
    }
}
