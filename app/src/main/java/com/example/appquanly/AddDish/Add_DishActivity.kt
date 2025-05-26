package com.example.appquanly.AddDish

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



    companion object {
        private const val REQUEST_UNIT = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_dish)

        presenter = ThemMonPresenter(this)

        // Ánh xạ view
        btnColor = findViewById(R.id.btn_color)
        btnIcon = findViewById(R.id.btnIcon)
        tvPrice = findViewById(R.id.tvPrice)
        tvUnit = findViewById(R.id.tvUnit)
        ivBack = findViewById(R.id.ivBack)
        tvSave = findViewById(R.id.tvSave)
        btnSave = findViewById(R.id.btnSave)
        edtItemName = findViewById(R.id.edtItemName)
        tvTitle = findViewById(R.id.tvTitle) // Thêm dòng này nếu chưa có
        btnDelete = findViewById(R.id.btnDelete) // Thêm dòng này nếu chưa có
        layoutNgungBan = findViewById(R.id.layoutNgungBan)
        checkboxInactive = findViewById(R.id.checkboxInactive)



        // Biến lưu trạng thái sửa món
        var isEdit = false
        var editingDishId: String? = null

        // Kiểm tra intent để biết là sửa món hay thêm mới
        isEdit = intent.getBooleanExtra("isEdit", false)
        if (isEdit) {
            editingDishId = intent.getStringExtra("dishId")
            editingDishId?.let {
                presenter.loadInventoryItemById(it) // Yêu cầu presenter load dữ liệu món lên view
            }

            // 👉 Đổi tiêu đề
            tvTitle.text = "Sửa món"

            // 👉 Hiện nút Xóa
            btnDelete.visibility = View.VISIBLE

            // 👉 Xử lý sự kiện Xóa
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

        // Chọn đơn vị tính
        tvUnit.setOnClickListener {
            val intent = Intent(this, Unit_Of_MeasureActivity::class.java)
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
            val priceText = tvPrice.text.toString().replace(",", "")
            val price = priceText.toFloatOrNull()


            if (isEdit && editingDishId != null) {
                presenter.updateInventoryItem(editingDishId!!, itemName, price, unit, color, selectedIconName, isInactive)
            } else {
                presenter.addInventoryItem(itemName, price, unit, color, selectedIconName, isInactive)
            }

        }

        tvSave.setOnClickListener { saveClickListener() }
        btnSave.setOnClickListener { saveClickListener() }
    }

    private fun formatPrice(value: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
        return formatter.format(value)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_UNIT && resultCode == RESULT_OK) {
            val selectedUnit = data?.getStringExtra("SELECTED_UNIT")
            if (!selectedUnit.isNullOrEmpty()) {
                tvUnit.text = selectedUnit
            }
        }
    }

    // Hiện máy tính để nhập giá
    private fun showCalculatorDialog() {
        val dialogView = layoutInflater.inflate(R.layout.activity_calculator, null)
        val edtDisplay = dialogView.findViewById<EditText>(R.id.edtDisplay)
        val gridButtons = dialogView.findViewById<GridLayout>(R.id.gridButtons)
        val btnDone = dialogView.findViewById<Button>(R.id.btnDone)

        var currentInput = ""
        var previousInput = ""
        var operator = ""
        var isInOperation = false

        fun formatNumberDisplay(value: String): String {
            val number = value.toDoubleOrNull() ?: return "0.0"
            return if (number % 1 == 0.0) number.toInt().toString() else number.toString()
        }

        fun updateDisplay() {
            val displayText = when {
                isInOperation && previousInput.isNotEmpty() && currentInput.isEmpty() -> "${formatNumberDisplay(previousInput)} $operator"
                previousInput.isNotEmpty() && currentInput.isNotEmpty() && operator.isNotEmpty() -> "${formatNumberDisplay(previousInput)} $operator ${formatNumberDisplay(currentInput)}"
                else -> formatNumberDisplay(currentInput)
            }
            edtDisplay.setText(displayText)
        }

        for (i in 0 until gridButtons.childCount) {
            val button = gridButtons.getChildAt(i) as Button
            button.setOnClickListener {
                val label = button.text.toString()

                when (label) {
                    "C" -> {
                        currentInput = ""
                        previousInput = ""
                        operator = ""
                        isInOperation = false
                        btnDone.text = "Xong"
                        updateDisplay()
                    }
                    "Giảm" -> {
                        val value = currentInput.toDoubleOrNull() ?: 0.0
                        currentInput = ((value - 1000).coerceAtLeast(0.0)).toInt().toString()
                        updateDisplay()
                    }
                    "Tăng" -> {
                        val value = currentInput.toDoubleOrNull() ?: 0.0
                        currentInput = (value + 1000).toInt().toString()
                        updateDisplay()
                    }
                    "," -> {
                        if (!currentInput.contains(".")) currentInput += "."
                        updateDisplay()
                    }
                    "+", "-", "x", "/" -> {
                        if (currentInput.isNotEmpty()) {
                            previousInput = currentInput
                            currentInput = ""
                            operator = if (label == "x") "*" else label
                            isInOperation = true
                            btnDone.text = "="
                            updateDisplay()
                        }
                    }
                    else -> {
                        if (label == "0" && currentInput == "0") return@setOnClickListener
                        currentInput += label
                        updateDisplay()
                    }
                }
            }
        }

        btnDone.setOnClickListener {
            if (btnDone.text == "=") {
                if (previousInput.isNotEmpty() && currentInput.isNotEmpty()) {
                    val result = when (operator) {
                        "+" -> previousInput.toDouble() + currentInput.toDouble()
                        "-" -> previousInput.toDouble() - currentInput.toDouble()
                        "*" -> previousInput.toDouble() * currentInput.toDouble()
                        "/" -> if (currentInput.toDouble() != 0.0) previousInput.toDouble() / currentInput.toDouble() else 0.0
                        else -> 0.0
                    }
                    currentInput = result.toString()
                    previousInput = ""
                    operator = ""
                    isInOperation = false
                    btnDone.text = "Xong"
                    tvPrice.text = formatPrice(currentInput.toDouble())
                }
            } else {
                if (currentInput.isNotEmpty()) tvPrice.text = formatPrice(currentInput.toDouble())
                alertDialog.dismiss()
            }
        }


        alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        alertDialog.show()
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
        tvPrice.text = item.Price?.let { formatPrice(it.toDouble()) } ?: ""
        tvUnit.text = item.UnitID
        checkboxInactive.isChecked = item.Inactive == true
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
