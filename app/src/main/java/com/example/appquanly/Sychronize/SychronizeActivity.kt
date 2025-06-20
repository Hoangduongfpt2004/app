package com.example.appquanly.Sychronize

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appquanly.R
import com.example.appquanly.SalePutIn.BaseDrawerActivity

class SychronizeActivity : BaseDrawerActivity(), Sync_DataContract.View {

    private lateinit var presenter: Sync_DataContract.Presenter
    private lateinit var edtSyncInput: EditText
    private lateinit var ivBack: ImageButton

    override fun getLayoutId(): Int {
        return R.layout.activity_synchroize
    }

    override fun getNavigationMenuItemId(): Int {
        // Giả sử id menu bạn đặt trong nav_menu.xml là dong_bo_du_lieu
        return R.id.dong_bo_du_lieu
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // BaseDrawerActivity sẽ setContentView theo getLayoutId()

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        edtSyncInput = findViewById(R.id.edtSyncInput)
        ivBack = findViewById(R.id.ivBack_One)

        presenter = SychronizePresenter(this)

        ivBack.setOnClickListener {
            navigateToSaleScreen()
        }

        edtSyncInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val code = edtSyncInput.text.toString().trim()
                presenter.onSyncClicked(code)

                Toast.makeText(this,"Đồng Bộ Thành Công", Toast.LENGTH_SHORT).show()

                true
            } else
                false
        }
    }

    override fun navigateToSaleScreen() {
        finish()
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
