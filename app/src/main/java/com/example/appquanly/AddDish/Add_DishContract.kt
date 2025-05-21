import com.example.appquanly.data.sqlite.Entity.InventoryItem

interface Add_DishContract {
    interface View {
        fun showColorPickerDialog()
        fun applySelectedColor(color: Int)
        fun showError(message: String)
        fun showSuccess(message: String)
        fun showInventoryItemToEdit(item: InventoryItem)
    }

    interface Presenter {
        fun onColorButtonClicked()
        fun onColorSelected(color: Int)
        fun addInventoryItem(name: String, price: Float?, unit: String, color: String?, icon: String?, isInactive: Boolean)
        fun loadInventoryItemById(itemId: String)
        fun updateInventoryItem(id: String, name: String, price: Float?, unit: String, color: String?, icon: String?, isInactive: Boolean)
        fun deleteInventoryItem(id: String)  // mới thêm
    }
}
