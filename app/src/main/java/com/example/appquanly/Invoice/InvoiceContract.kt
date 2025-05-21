import com.example.appquanly.data.sqlite.Entity.InventoryItem

interface InvoiceContract {
    interface View {
        fun showInvoiceData(items: List<InventoryItem>)
        fun showTotalAmount(amount: Double)
        fun showReceiveAmount(amount: Double)
        fun showRemainAmount(remain: Double)
        fun showInvoiceInfo(refNo: String, date: String)
    }

    interface Presenter {
        fun setSelectedItems(items: List<InventoryItem>)
        fun loadInvoiceData()
        fun calculateInvoice(receiveAmount: Double)
        fun onTienKhachDuaChanged(amount: Double)
        fun onDoneClick(receiveAmount: Double)
    }
}
