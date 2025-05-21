package com.example.appquanly.Sychronize

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appquanly.R

class SychronizeActivity : AppCompatActivity(), Sync_DataContract.View {

    private lateinit var presenter: Sync_DataContract.Presenter
    private lateinit var edtSyncInput: EditText
    private lateinit var ivBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_synchroize)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ánh xạ View
        edtSyncInput = findViewById(R.id.edtSyncInput)
        ivBack = findViewById(R.id.ivBack_One)

        // Khởi tạo Presenter
        presenter = SychronizePresenter(this)

        // Xử lý nút quay lại
        ivBack.setOnClickListener {
            navigateToSaleScreen()
        }

        // Xử lý nhấn "Done" trong EditText
        edtSyncInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val code = edtSyncInput.text.toString().trim()
                presenter.onSyncClicked(code)

            Toast.makeText(this,"Đồng Bộ Thành Công",Toast.LENGTH_SHORT).show()

                true
            } else

                false
        }
    }

    override fun navigateToSaleScreen() {
        // Quay về màn hình bán hàng, ví dụ finish()
        finish()
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
