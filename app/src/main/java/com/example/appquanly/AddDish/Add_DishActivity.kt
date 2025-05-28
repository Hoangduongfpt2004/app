package com.example.appquanly.AddDish

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.ParseException
import com.example.appquanly.R
import com.example.appquanly.UnitOfMeasure.Unit_Of_MeasureActivity
import com.example.appquanly.data.sqlite.Entity.InventoryItem
import java.io.IOException
import java.text.NumberFormat
import java.util.Locale

class Add_DishActivity : AppCompatActivity(), Add_DishContract.View {
    private lateinit var btnColor: ImageView
    private lateinit var btnIcon: ImageView
    private lateinit var presenter: Add_DishContract.Presenter
    private var selectedColor: Int = Color.WHITE
    private var selectedIcon: Drawable? = null
    private var selectedIconName: String? = null
    private lateinit var alertDialog: AlertDialog
    private lateinit var tvPrice: TextView
    private lateinit var ivBack: ImageView
    private lateinit var tvSave: TextView
    private lateinit var btnSave: Button
    private lateinit var edtItemName: EditText
    private lateinit var tvUnit: TextView
    private  var currentDishId: String? = null
    private lateinit var tvTitle:TextView
    private lateinit var btnDelete: Button
    private lateinit var checkboxInactive: CheckBox
    private lateinit var layoutNgungBan: LinearLayout
    private var selectedUnitName: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_dish)

        presenter = Add_DishPersenter(this)

        // Ánh xạ view
        btnColor = findViewById(R.id.btn_color)
        btnIcon = findViewById(R.id.btnIcon)
        tvPrice = findViewById(R.id.tvPrice)
        tvUnit = findViewById(R.id.tvUnit)
        ivBack = findViewById(R.id.ivBack)
        tvSave = findViewById(R.id.tvSave)
        btnSave = findViewById(R.id.btnSave)
        edtItemName = findViewById(R.id.edtItemName)
        tvTitle = findViewById(R.id.tvTitle)
        btnDelete = findViewById(R.id.btnDelete)
        layoutNgungBan = findViewById(R.id.layoutNgungBan)
        checkboxInactive = findViewById(R.id.checkboxInactive)

        // Biến lưu trạng thái sửa món
        var isEdit = false
        var editingDishId: String? = null

        // Lấy đơn vị tính đã chọn từ SharedPreferences để giữ trạng thái
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        var selectedUnitName = sharedPreferences.getString("LAST_SELECTED_UNIT", "bao") // "bao" mặc định nếu chưa chọn

        // Hiển thị đơn vị tính lên TextView
        tvUnit.text = selectedUnitName

        // Kiểm tra intent để biết là sửa món hay thêm mới
        isEdit = intent.getBooleanExtra("isEdit", false)
        if (isEdit) {
            editingDishId = intent.getStringExtra("dishId")
            editingDishId?.let {
                presenter.loadInventoryItemById(it) // Yêu cầu presenter load dữ liệu món lên view
            }

            //  Đổi tiêu đề
            tvTitle.text = "Sửa món"

            //  Hiện nút Xóa
            btnDelete.visibility = View.VISIBLE

            //  Xử lý sự kiện Xóa
            btnDelete.setOnClickListener {
                editingDishId?.let { id ->
                    presenter.deleteInventoryItem(id)
                }
            }
        }

        // Sự kiện chọn màu
        btnColor.setOnClickListener {
            presenter.onColorButtonClicked()
        }

        // Sự kiện chọn icon
        btnIcon.setOnClickListener {
            showIconPickerDialog()
        }

        // Sự kiện mở máy tính nhập giá
        tvPrice.setOnClickListener {
            showCalculatorDialog()
        }

        // Back button
        ivBack.setOnClickListener { finish() }

        // Chọn đơn vị tính: mở Unit_Of_MeasureActivity và truyền đơn vị hiện tại
        tvUnit.setOnClickListener {
            val intent = Intent(this, Unit_Of_MeasureActivity::class.java)
            intent.putExtra("SELECTED_UNIT_NAME", selectedUnitName) // truyền đơn vị hiện tại để chọn mặc định
            startActivityForResult(intent, REQUEST_UNIT)
        }

        checkboxInactive.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Sản phẩm ngừng bán", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Sản phẩm đang bán", Toast.LENGTH_SHORT).show()
            }
        }

        // Nút lưu (có thể 2 nút)
        val saveClickListener = {
            val itemName = edtItemName.text.toString().trim()
            val unit = tvUnit.text.toString().trim()
            val color = if (selectedColor != Color.WHITE) String.format("#%06X", 0xFFFFFF and selectedColor) else null
            val isInactive = checkboxInactive.isChecked

            val priceTextRaw = tvPrice.text.toString().trim()
            val priceClean = priceTextRaw.replace(".", "")
            val price = priceClean.toFloatOrNull() ?: 0f

            if (isEdit && editingDishId != null) {
                presenter.updateInventoryItem(editingDishId!!, itemName, price, unit, color, selectedIconName, isInactive)
            } else {
                presenter.addInventoryItem(itemName, price, unit, color, selectedIconName, isInactive)
            }


        }
        tvSave.setOnClickListener { saveClickListener() }
        btnSave.setOnClickListener { saveClickListener() }
    }

    private fun formatPrice(price: Float): String {
        val format = NumberFormat.getInstance(Locale("vi", "VN"))
        format.minimumFractionDigits = 0
        format.maximumFractionDigits = 0
        return format.format(price)
    }


    // Bổ sung override onActivityResult để nhận đơn vị tính mới từ Unit_Of_MeasureActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_UNIT && resultCode == RESULT_OK) {
            val newUnit = data?.getStringExtra("SELECTED_UNIT")
            if (!newUnit.isNullOrEmpty()) {
                tvUnit.text = newUnit

                // Lưu đơn vị mới vào SharedPreferences để giữ trạng thái cho lần sau
                val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().putString("LAST_SELECTED_UNIT", newUnit).apply()
            }
        }
    }

    // Định nghĩa hằng số request code
    companion object {
        private const val REQUEST_UNIT = 1001
    }



    // Hiện máy tính để nhập giá
    private fun showCalculatorDialog() {
        val dialogView = layoutInflater.inflate(R.layout.activity_calculator, null)
        val edtDisplay = dialogView.findViewById<EditText>(R.id.edtDisplay)
        val gridButtons = dialogView.findViewById<GridLayout>(R.id.gridButtons)
        val btnDone = dialogView.findViewById<Button>(R.id.btnDone)

        var currentInput = ""
        var previousValue = 0.0
        var operator = ""
        var isInOperation = false

        fun formatDisplayNumber(value: Double): String {
            val nf = NumberFormat.getInstance(Locale("vi", "VN"))
            nf.maximumFractionDigits = 0
            nf.minimumFractionDigits = 0
            return nf.format(value)
        }

        fun updateDisplay() {
            if (currentInput.isNotEmpty()) {
                val inputValue = currentInput.toDoubleOrNull() ?: 0.0
                if (isInOperation) {
                    val prevFormatted = formatDisplayNumber(previousValue)
                    val currFormatted = formatDisplayNumber(inputValue)
                    edtDisplay.setText("$prevFormatted $operator $currFormatted")
                } else {
                    edtDisplay.setText(formatDisplayNumber(inputValue))
                }
            } else {
                edtDisplay.setText("0")
            }
            edtDisplay.setSelection(edtDisplay.text.length)
        }

        fun clearInput() {
            currentInput = ""
            previousValue = 0.0
            operator = ""
            isInOperation = false
            btnDone.text = "Xong"
            updateDisplay()
        }

        fun appendDigit(digit: String) {
            if (digit in "0123456789") {
                currentInput += digit
                updateDisplay()
            }
        }

        fun increaseByThousand() {
            val currentVal = currentInput.toDoubleOrNull() ?: 0.0
            val newVal = currentVal + 1000
            currentInput = newVal.toLong().toString()
            updateDisplay()
        }

        fun decreaseByThousand() {
            val currentVal = currentInput.toDoubleOrNull() ?: 0.0
            val newVal = (currentVal - 1000).coerceAtLeast(0.0)
            currentInput = newVal.toLong().toString()
            updateDisplay()
        }

        fun performOperation() {
            val currentVal = currentInput.toDoubleOrNull() ?: 0.0
            val result = when (operator) {
                "+" -> previousValue + currentVal
                "-" -> previousValue - currentVal
                "x", "*" -> previousValue * currentVal
                "/" -> if (currentVal != 0.0) previousValue / currentVal else 0.0
                else -> currentVal
            }
            currentInput = result.toLong().toString()
            previousValue = 0.0
            operator = ""
            isInOperation = false
            btnDone.text = "Xong"
            updateDisplay()
        }

        clearInput()

        for (i in 0 until gridButtons.childCount) {
            val button = gridButtons.getChildAt(i) as Button
            button.setOnClickListener {
                val label = button.text.toString()
                when (label) {
                    "C" -> clearInput()
                    "Giảm" -> decreaseByThousand()
                    "Tăng" -> increaseByThousand()
                    "+", "-", "x", "/" -> {
                        if (!isInOperation) {
                            previousValue = currentInput.toDoubleOrNull() ?: 0.0
                            currentInput = ""
                            operator = label
                            isInOperation = true
                            btnDone.text = "="
                            updateDisplay()
                        }
                    }
                    "=" -> performOperation()
                    else -> {
                        if (label == "000") {
                            currentInput += "000"
                            updateDisplay()
                        } else {
                            appendDigit(label)
                        }
                    }

                }
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnDone.setOnClickListener {
            if (btnDone.text == "=") {
                performOperation()
            } else {
                val finalPrice = currentInput.toDoubleOrNull() ?: 0.0
                tvPrice.text = formatDisplayNumber(finalPrice)
                dialog.dismiss()
            }
        }

        dialog.show()
    }



    private fun applySelectedColorForIconBackground(color: Int) {
        btnColor.backgroundTintList = ColorStateList.valueOf(color)
        btnIcon.backgroundTintList = ColorStateList.valueOf(color)
    }

    override fun showColorPickerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.activity_dialog_select_color, null)
        val gridLayout = dialogView.findViewById<GridLayout>(R.id.gridColors)

        val size = resources.getDimensionPixelSize(R.dimen.color_circle_size)
        val margin = resources.getDimensionPixelSize(R.dimen.color_circle_margin)

        val colorNames = listOf(
            "red_base", "pink_base", "purple_base", "deep_purple_base", "indigo_base",
            "blue", "light_blue_base", "cyan_base", "teal_base", "green_base",
            "light_green_base", "lime_base", "yellow_base", "amber_base", "orange_base",
            "deep_orange_base", "brown_base", "grey_base", "blue_grey_base", "light_grey_base",
            "dark_grey_base", "navy_base", "olive_base", "maroon_base", "aqua_base",
            "fuchsia_base", "silver_base", "gold_base", "sky_blue_base", "beige_base",
            "mint_base", "peach_base"
        )

        colorNames.forEach { colorName ->
            val colorId = resources.getIdentifier(colorName, "color", packageName)
            val color = ContextCompat.getColor(this, colorId)

            val colorView = ImageView(this).apply {
                layoutParams = ViewGroup.MarginLayoutParams(size, size).apply {
                    setMargins(margin, margin, margin, margin)
                }
                background = ContextCompat.getDrawable(this@Add_DishActivity, R.drawable.circle_background)
                backgroundTintList = ColorStateList.valueOf(color)
                isClickable = true
                isFocusable = true

                setOnClickListener {
                    selectedColor = color
                    applySelectedColorForIconBackground(color)
                    alertDialog.dismiss()
                }
            }

            gridLayout.addView(colorView)
        }

        alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        alertDialog.show()
    }


    override fun applySelectedColor(color: Int) {
        selectedColor = color
        applySelectedColorForIconBackground(color)
    }

    override fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        setResult(RESULT_OK) // thông báo thành công cho activity gọi
        finish()
    }


    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showInventoryItemToEdit(item: InventoryItem) {
        layoutNgungBan.visibility = View.VISIBLE

        edtItemName.setText(item.InventoryItemName)
        tvPrice.text = item.Price?.let { formatPrice(it) } ?: ""
        tvUnit.text = item.UnitID
        checkboxInactive.isChecked = item.Inactive == true

        // Giữ nguyên icon cũ khi sửa món
        selectedIconName = item.IconFileName  // giả sử item có trường IconName lưu tên icon (chuỗi)
        if (selectedIconName != null) {
            try {
                val inputStream = assets.open("icondefault/$selectedIconName")
                val drawable = Drawable.createFromStream(inputStream, null)
                selectedIcon = drawable
                btnIcon.setImageDrawable(drawable)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        // Giữ nguyên màu cũ khi sửa món
        val colorString = item.Color  // giả sử item có trường Color lưu string "#RRGGBB"
        if (!colorString.isNullOrEmpty()) {
            try {
                selectedColor = Color.parseColor(colorString)
                applySelectedColorForIconBackground(selectedColor)
            } catch (e: IllegalArgumentException) {
                // Nếu màu sai định dạng thì dùng mặc định
                selectedColor = Color.WHITE
            }
        }
    }


    private fun showIconPickerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.activity_dialog_select_icon, null)
        val gridLayout = dialogView.findViewById<GridLayout>(R.id.gridIcons)

        val iconFiles = try {
            assets.list("icondefault") ?: arrayOf()
        } catch (e: IOException) {
            arrayOf()
        }

        val size = resources.getDimensionPixelSize(R.dimen.icon_size)
        val margin = resources.getDimensionPixelSize(R.dimen.icon_margin)

        iconFiles.forEach { fileName ->
            try {
                val inputStream = assets.open("icondefault/$fileName")
                val drawable = Drawable.createFromStream(inputStream, null)
                val imageView = ImageView(this).apply {
                    layoutParams = ViewGroup.MarginLayoutParams(size, size).apply {
                        setMargins(margin, margin, margin, margin)
                    }
                    setImageDrawable(drawable)
                    setOnClickListener {
                        selectedIcon = drawable
                        selectedIconName = fileName
                        btnIcon.setImageDrawable(drawable)
                        alertDialog.dismiss()
                    }
                }
                gridLayout.addView(imageView)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        alertDialog.show()
    }
}
