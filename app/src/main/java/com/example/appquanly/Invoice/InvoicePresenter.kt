import android.content.Context
import com.example.appquanly.Invoice.InvoiceContract
import com.example.appquanly.data.sqlite.Entity.InventoryItem
import com.example.appquanly.data.sqlite.Entity.SAInvoiceDetail
import com.example.appquanly.data.sqlite.Entity.SAInvoiceItem
import com.example.appquanly.data.sqlite.Local.InventoryItemRepository
import com.example.appquanly.data.sqlite.Local.SAInvoiceDetailRepository
import com.example.appquanly.data.sqlite.Local.SAInvoiceRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.ceil

class InvoicePresenter(
    private val context: Context,
    private val view: InvoiceContract.View,
    private val inventoryRepository: InventoryItemRepository,
    private val invoiceRepository: SAInvoiceRepository,
    private val invoiceDetailRepository: SAInvoiceDetailRepository
) : InvoiceContract.Presenter {

    private var itemList: List<InventoryItem> = listOf()
    private var totalAmount: Double = 0.0
    private var refNo: String = ""
    private var refDate: Long = 0L
    private var invoiceRefId: String = ""

    // Hàm làm tròn theo bội (ví dụ bội 1000)
    private fun roundAmount(amount: Double, roundTo: Int = 1000): Double {
        return ceil(amount / roundTo) * roundTo
    }

    override fun setSelectedItems(items: List<InventoryItem>) {
        itemList = items
    }

    override fun loadInvoiceData() {
        totalAmount = itemList.sumOf { (it.Price ?: 0f).toDouble() * it.UseCount.toDouble() }
        totalAmount = roundAmount(totalAmount) // làm tròn tổng tiền
        view.showInvoiceData(itemList)
        view.showTotalAmount(totalAmount)

        refNo = "HD-${System.currentTimeMillis() % 100000}"
        refDate = System.currentTimeMillis()

        val dateStr = SimpleDateFormat("dd/MM/yyyy (HH:mm)", Locale.getDefault()).format(Date(refDate))
        view.showInvoiceInfo(refNo, dateStr)
    }

    override fun calculateInvoice(receiveAmount: Double) {
        val roundedReceive = roundAmount(receiveAmount)
        val remain = roundedReceive - totalAmount
        view.showReceiveAmount(roundedReceive)
        view.showRemainAmount(if (remain >= 0) remain else 0.0)
    }

    override fun onDoneClick(receiveAmount: Double) {
        val roundedReceive = roundAmount(receiveAmount)
        val remain = roundedReceive - totalAmount
        invoiceRefId = UUID.randomUUID().toString()
        val refDateNow = System.currentTimeMillis()
        val refNoNow = "HD${refDateNow}"

        // Tạo hóa đơn tổng (header)
        val invoiceHeader = SAInvoiceItem(
            refId = invoiceRefId,
            refType = 1,  // Loại hóa đơn
            refNo = refNoNow,
            refDate = refDateNow,
            amount = totalAmount,
            returnAmount = 0.0,
            receiveAmount = roundedReceive,
            remainAmount = if (remain >= 0) remain else 0.0,
            journalMemo = "Hóa đơn bán hàng",
            paymentStatus = 1, // thanh toán thành công
            numberOfPeople = 1,
            tableName = null,
            listItemName = itemList.joinToString(", ") { it.InventoryItemName ?: "" },
            createdDate = refDateNow,
            createdBy = "admin",
            modifiedDate = null,
            modifiedBy = null
        )

        val insertHeaderResult = invoiceRepository.insertInvoice(invoiceHeader)

        if (insertHeaderResult) {
            // Chèn chi tiết hóa đơn từng món
            var successAllDetails = true

            itemList.forEachIndexed { index, item ->
                val quantity = item.UseCount.toFloat()
                val unitPrice = item.Price ?: 0f
                val amount = quantity * unitPrice

                val invoiceDetail = SAInvoiceDetail(
                    RefDetailID = UUID.randomUUID().toString(),
                    RefDetailType = 1,
                    RefID = invoiceRefId,
                    InventoryItemID = item.InventoryItemID,
                    InventoryItemName = item.InventoryItemName ?: "",
                    UnitID = item.UnitID ?: "",
                    UnitName = "", // Nếu có dữ liệu UnitName thì lấy ở đây
                    Quantity = quantity,
                    UnitPrice = unitPrice,
                    Amount = amount,
                    Description = "",
                    SortOrder = index,
                    CreatedDate = refDateNow,
                    CreatedBy = "admin",
                    ModifiedDate = null,
                    ModifiedBy = ""
                )

                val detailInsertResult = invoiceDetailRepository.insertDetail(invoiceDetail)
                if (detailInsertResult == -1L) {
                    successAllDetails = false
                }

            }

            if (successAllDetails) {
                view.showToast("Đã lưu hóa đơn thành công!")
            } else {
                view.showToast("Lỗi khi lưu chi tiết hóa đơn.")
            }

        } else {
            view.showToast("Lỗi khi lưu hóa đơn tổng.")
        }

        view.showRemainAmount(if (remain >= 0) remain else 0.0)
    }
}
