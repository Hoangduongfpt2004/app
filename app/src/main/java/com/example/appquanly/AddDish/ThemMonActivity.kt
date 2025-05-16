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
import com.example.appquanly.R
import com.example.appquanly.UnitOfMeasure.DonViTinhActivity
import java.io.IOException
import java.util.UUID

class ThemMonActivity : AppCompatActivity(), ThemMonContract.View {
    private lateinit var btnColor: ImageView
    private lateinit var btnIcon: ImageView
    private lateinit var presenter: ThemMonContract.Presenter
    private var selectedColor: Int = Color.WHITE
    private var selectedIcon: Drawable? = null
    private var selectedIconName: String? = null // Lưu tên file icon
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

        // Ánh xạ các view
        btnColor = findViewById(R.id.btn_color)
        btnIcon = findViewById(R.id.btnIcon)
        tvPrice = findViewById(R.id.tvPrice)
        tvUnit = findViewById(R.id.tvUnit)
        ivBack = findViewById(R.id.ivBack)
        tvSave = findViewById(R.id.tvSave)
        btnSave = findViewById(R.id.btnSave)
        edtItemName = findViewById(R.id.edtItemName)

        // Xử lý sự kiện
        btnColor.setOnClickListener {
            presenter.onColorButtonClicked()
        }

        btnIcon.setOnClickListener {
            showIconPickerDialog()
        }

        tvPrice.setOnClickListener {
            showCalculatorDialog()
        }

        ivBack.setOnClickListener {
            finish()
        }

        tvUnit.setOnClickListener {
            val intent = Intent(this, DonViTinhActivity::class.java)
            startActivityForResult(intent, REQUEST_UNIT)
        }

        // Xử lý nút "Cất"
        val saveClickListener: () -> Unit = {
            val itemName = edtItemName.text.toString().trim()
            val price = tvPrice.text.toString().replace(",", "").replace(" ", "").toFloatOrNull()
            val unit = tvUnit.text.toString().trim()
            val color = if (selectedColor != Color.WHITE) String.format("#%06X", (0xFFFFFF and selectedColor)) else null
            val iconFileName = selectedIconName // Lưu tên file icon gốc

            presenter.addInventoryItem(itemName, price, unit, color, iconFileName)
        }

        tvSave.setOnClickListener { saveClickListener() }
        btnSave.setOnClickListener { saveClickListener() }
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
            return if (number % 1 == 0.0) number.toInt().toString() else number.toString()
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
                        if (!currentInput.contains(".")) currentInput += "."
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
                    tvPrice.text = formatNumberDisplay(currentInput)
                }
            } else {
                if (currentInput.isNotEmpty()) tvPrice.text = formatNumberDisplay(currentInput)
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

    override fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        setResult(RESULT_OK)
        finish()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showIconPickerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.activity_dialog_select_icon, null)
        val gridLayout = dialogView.findViewById<GridLayout>(R.id.gridIcons)

        val iconNames = getIconNamesFromAssets()

        if (iconNames.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy icon trong thư mục icondefault", Toast.LENGTH_SHORT).show()
        }

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
                        selectedIconName = iconName // Lưu tên file icon
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
            assets.list("icondefault")?.filter { it.endsWith(".png") || it.endsWith(".jpg") }?.map { "icondefault/$it" } ?: emptyList()
        } catch (e: IOException) {
            Toast.makeText(this, "Không thể đọc thư mục icondefault: ${e.message}", Toast.LENGTH_SHORT).show()
            emptyList()
        }
    }

    private fun loadDrawableFromAssets(iconName: String): Drawable? {
        return try {
            val inputStream = assets.open(iconName)
            Drawable.createFromStream(inputStream, null).also { inputStream.close() }
        } catch (e: IOException) {
            Toast.makeText(this, "Không thể tải icon $iconName: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }

    companion object {
        private const val REQUEST_UNIT = 100
    }
}