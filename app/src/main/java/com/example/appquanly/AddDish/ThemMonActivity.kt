package com.example.appquanly.AddDish

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.appquanly.UnitOfMeasure.DonViTinhActivity
import com.example.appquanly.R
import java.io.IOException

class ThemMonActivity  : AppCompatActivity(), ThemMonContract.View {
    private lateinit var btnColor: ImageView
    private lateinit var btnIcon: ImageView
    private lateinit var presenter: ThemMonContract.Presenter
    private var selectedColor: Int = Color.WHITE
    private var selectedIcon: Drawable? = null
    private lateinit var alertDialog: AlertDialog
    private lateinit var tvPrice: TextView
    private lateinit var ivBack: ImageView
    private lateinit var tvSave: TextView
    private lateinit var btnSave: Button
    private lateinit var edtItemName: EditText
    private lateinit var tvUnit: TextView





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_dish)

        presenter = ThemMonPresenter(this)

        // Ánh xạ các view từ activity_them_mon.xml
        btnColor = findViewById(R.id.btn_color)
        btnIcon = findViewById(R.id.btnIcon)
        tvPrice = findViewById(R.id.tvPrice)
        tvUnit = findViewById(R.id.tvUnit)
        ivBack = findViewById(R.id.ivBack)
        tvSave = findViewById(R.id.tvSave)
        btnSave = findViewById(R.id.btnSave)
        edtItemName = findViewById(R.id.edtItemName)
        tvUnit = findViewById(R.id.tvUnit)

// Xử lý sự kiện cho các nút
        btnColor.setOnClickListener {
            presenter.onColorButtonClicked()
        }

        btnIcon.setOnClickListener {
            showIconPickerDialog()
        }

        tvPrice.setOnClickListener {
            showCalculatorDialog()
        }


// Xử lý nút quay lại
        ivBack.setOnClickListener {
            finish()
        }
        tvUnit.setOnClickListener{
            val intent = Intent(this, DonViTinhActivity::class.java)
            startActivity(intent)
        }



        // Xử lý nút "Cất" trên thanh tiêu đề
        tvSave.setOnClickListener {
            val itemName = edtItemName.text.toString()
            val price = tvPrice.text.toString().replace(",", "").replace(" ", "").toDoubleOrNull() ?: 0.0
            val unit = tvUnit.text.toString()

            if (itemName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên món", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (unit.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn đơn vị tính", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Chuyển dữ liệu về MenuActivity
            val intent = Intent(this, ThemMonActivity::class.java)
            intent.putExtra("ITEM_NAME", itemName)
            intent.putExtra("PRICE", price)
            intent.putExtra("SELECTED_UNIT", unit)
            startActivity(intent)
            finish()
        }

        // Xử lý nút "CẤT" dưới cùng
        btnSave.setOnClickListener {
            val itemName = edtItemName.text.toString()
            val price = tvPrice.text.toString().replace(",", "").replace(" ", "").toDoubleOrNull() ?: 0.0
            val unit = tvUnit.text.toString()

            if (itemName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên món", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (unit.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn đơn vị tính", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Chuyển dữ liệu về MenuActivity
            val intent = Intent(this,   ThemMonActivity::class.java)
            intent.putExtra("ITEM_NAME", itemName)
            intent.putExtra("PRICE", price)
            intent.putExtra("SELECTED_UNIT", unit)
            startActivity(intent)
            finish()
        }
    }


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
            val number = value.toDoubleOrNull() ?: return "0"
            return if (number % 1 == 0.0) {
                number.toInt().toString()
            } else {
                number.toString()
            }
        }

        fun updateDisplay() {
            val displayText = if (isInOperation && previousInput.isNotEmpty() && currentInput.isEmpty()) {
                "${formatNumberDisplay(previousInput)} $operator"
            } else if (previousInput.isNotEmpty() && currentInput.isNotEmpty() && operator.isNotEmpty()) {
                "${formatNumberDisplay(previousInput)} $operator ${formatNumberDisplay(currentInput)}"
            } else {
                formatNumberDisplay(currentInput)
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
                        val value = currentInput.replace(",", "").toDoubleOrNull() ?: 0.0
                        currentInput = ((value - 1000).coerceAtLeast(0.0)).toInt().toString()
                        updateDisplay()
                    }
                    "Tăng" -> {
                        val value = currentInput.replace(",", "").toDoubleOrNull() ?: 0.0
                        currentInput = (value + 1000).toInt().toString()
                        updateDisplay()
                    }
                    "," -> {
                        if (!currentInput.contains(".")) {
                            currentInput += "."
                        }
                        updateDisplay()
                    }
                    "+", "-", "x", "/" -> {
                        if (currentInput.isNotEmpty()) {
                            previousInput = currentInput
                            currentInput = ""
                            operator = when (label) {
                                "x" -> "*"
                                else -> label
                            }
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
                // Tính toán và hiện phép tính
                if (previousInput.isNotEmpty() && currentInput.isNotEmpty()) {
                    val result = when (operator) {
                        "+" -> previousInput.toDouble() + currentInput.toDouble()
                        "-" -> previousInput.toDouble() - currentInput.toDouble()
                        "*" -> previousInput.toDouble() * currentInput.toDouble()
                        "/" -> if (currentInput.toDouble() != 0.0) {
                            previousInput.toDouble() / currentInput.toDouble()
                        } else {
                            0.0
                        }
                        else -> 0.0
                    }
                    val displayResult = "${formatNumberDisplay(previousInput)} $operator ${formatNumberDisplay(currentInput)} = ${formatNumberDisplay(result.toString())}"
                    edtDisplay.setText(displayResult)
                    currentInput = result.toString()
                    previousInput = ""
                    operator = ""
                    isInOperation = false
                    btnDone.text = "Xong"
                    tvPrice.text = formatNumberDisplay(currentInput)
                }
            } else {
                if (currentInput.isNotEmpty()) {
                    tvPrice.text = formatNumberDisplay(currentInput)
                }
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
        // Chỉ thay đổi nền, không đổi màu icon
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
            if (colorId != 0) {
                val color = ContextCompat.getColor(this, colorId)
                val imageView = ImageView(this).apply {
                    layoutParams = ViewGroup.MarginLayoutParams(size, size).apply {
                        setMargins(margin, margin, margin, margin)
                    }
                    background = ContextCompat.getDrawable(context, R.drawable.circle_color)?.mutate()
                    background?.setTint(color)
                    setOnClickListener {
                        alertDialog.dismiss()
                        presenter.onColorSelected(color)

                        // Cập nhật màu nền cho cả hai nút
                        applySelectedColorForIconBackground(color)
                    }
                }
                gridLayout.addView(imageView)
            }
        }

        alertDialog = AlertDialog.Builder(this)
            .setTitle("Chọn màu")
            .setView(dialogView)
            .setNegativeButton("Hủy", null)
            .create()

        alertDialog.show()
    }

    override fun applySelectedColor(color: Int) {
        selectedColor = color
        btnColor.setColorFilter(null)
    }

    private fun showIconPickerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.activity_dialog_select_icon, null)
        val gridLayout = dialogView.findViewById<GridLayout>(R.id.gridIcons)

        val iconNames = getIconNamesFromAssets()

        iconNames.forEach { iconName ->
            val iconDrawable = loadDrawableFromAssets(iconName)
            if (iconDrawable != null) {
                val imageView = ImageView(this).apply {
                    layoutParams = ViewGroup.MarginLayoutParams(
                        resources.getDimensionPixelSize(R.dimen.icon_size),
                        resources.getDimensionPixelSize(R.dimen.icon_size)
                    ).apply {
                        setMargins(16, 16, 16, 16)
                    }
                    setImageDrawable(iconDrawable)
                    setOnClickListener {
                        selectedIcon = iconDrawable
                        btnIcon.setImageDrawable(selectedIcon)
                        alertDialog.dismiss()
                    }
                }
                gridLayout.addView(imageView)
            }
        }

        alertDialog = AlertDialog.Builder(this)
            .setTitle("Chọn biểu tượng")
            .setView(dialogView)
            .setNegativeButton("Hủy", null)
            .create()

        alertDialog.show()
    }

    private fun getIconNamesFromAssets(): List<String> {
        return try {
            assets.list("")?.filter { it.endsWith(".png") || it.endsWith(".jpg") } ?: emptyList()
        } catch (e: IOException) {
            Toast.makeText(this, "Không thể đọc thư mục assets", Toast.LENGTH_SHORT).show()
            emptyList()
        }
    }

    private fun loadDrawableFromAssets(iconName: String): Drawable? {
        return try {
            val inputStream = assets.open(iconName)
            Drawable.createFromStream(inputStream, null)
        } catch (e: IOException) {
            null
        }
    }
}