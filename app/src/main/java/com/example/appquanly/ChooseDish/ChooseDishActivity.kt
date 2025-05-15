package com.example.appquanly.ChooseDish.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appquanly.CashRegiter.CalculatorDialogFragment
import com.example.appquanly.ChooseDish.contract.ChooseDishContract
import com.example.appquanly.ChooseDish.presenter.ChooseDishPresenter
import com.example.appquanly.R
import com.example.appquanly.SalePutIn.SaleeActivity

class ChooseDishActivity : AppCompatActivity(), ChooseDishContract.View {

    private lateinit var presenter: ChooseDishContract.Presenter

    private lateinit var tvTotalMoney: TextView
    private lateinit var btnBack: ImageView
    private lateinit var btnSetting: ImageView
    private lateinit var btnAvatar: ImageView
    private lateinit var btnCollectMoney: TextView
    private lateinit var edit_x: TextView
    private lateinit var edit_store: TextView

    companion object {
        const val REQUEST_CODE_CALCULATOR = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_dish) // Thay bằng tên file XML của bạn

        presenter = ChooseDishPresenter(this)

        tvTotalMoney = findViewById(R.id.tvTongTien)
        btnBack = findViewById(R.id.img_back)
        btnSetting = findViewById(R.id.seting)
        btnAvatar = findViewById(R.id.avatar)
        btnCollectMoney = findViewById(R.id.btnCollectMoney)
        edit_x = findViewById(R.id.edt_x)
        edit_store = findViewById(R.id.editStore)

        btnBack.setOnClickListener {
            presenter.onBackClick()
            finish() // hoặc hành động khác
        }

        btnCollectMoney.setOnClickListener {
            presenter.onCollectMoneyClick()
        }

        btnSetting.setOnClickListener {
            presenter.onSettingClick()
        }

        btnAvatar.setOnClickListener {
            presenter.onAvatarClick()
        }
        edit_x.setOnClickListener {
            presenter.onBackClick()
            finish()
        }

        edit_store.setOnClickListener {
            val intent = Intent(this,SaleeActivity::class.java)
            startActivity(intent)
        }

        // Giả sử thêm sản phẩm (bạn gọi hàm này khi thêm sản phẩm thật)
        // presenter.addProduct(100000.0)
    }

    override fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun openCalculator() {
        val calculatorDialog = CalculatorDialogFragment { result ->
            showMessage("Giá trị máy tính trả về: $result")
            // Cập nhật tổng tiền hoặc làm gì đó với result ở đây
            val total = result.toDoubleOrNull() ?: 0.0
            updateTotalMoney(total)
        }
        calculatorDialog.show(supportFragmentManager, "CalculatorDialog")
    }


    override fun updateTotalMoney(total: Double) {
        tvTotalMoney.text = total.toInt().toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CALCULATOR && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringExtra("result") ?: "0"
            showMessage("Giá trị máy tính trả về: $result")
            // Bạn có thể xử lý giá trị result ở đây
        }
    }
}
