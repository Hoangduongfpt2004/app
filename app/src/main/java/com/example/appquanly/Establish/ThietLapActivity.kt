package com.example.appquanly.Establish

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appquanly.R
import com.example.appquanly.Establish.contract.ThietLapContract
import com.example.appquanly.Establish.presenter.ThietLapPresenter

class ThietLapActivity : AppCompatActivity(), ThietLapContract.View {

    private lateinit var presenter: ThietLapContract.Presenter
    private lateinit var layoutRestoreData: LinearLayout
    private lateinit var tvRestoreDescription: TextView  // 👈 thêm dòng này

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_establish)

        presenter = ThietLapPresenter(this)

        layoutRestoreData = findViewById(R.id.layoutRestoreData)
        tvRestoreDescription = findViewById(R.id.tvRestoreDescription)

        // 👉 Đặt phần text có chữ màu đỏ
        val text = "Chức năng sẽ tiến hành xóa dữ liệu trên thiết bị của bạn và thay thế bằng dữ liệu đã lưu trên máy chủ."
        val spannable = SpannableString(text)
        val redPart = "xóa dữ liệu trên thiết bị của bạn"
        val start = text.indexOf(redPart)
        val end = start + redPart.length
        if (start >= 0) {
            spannable.setSpan(
                ForegroundColorSpan(Color.RED),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        tvRestoreDescription.text = spannable

        layoutRestoreData.setOnClickListener {
            presenter.onRestoreDataClicked()
        }
    }

    override fun showRestoreDataDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Xác nhận lấy lại toàn bộ dữ liệu")

        val fullText = "Chức năng sẽ tiến hành xóa dữ liệu trên thiết bị của bạn và thay thế bằng dữ liệu đã lưu trên máy chủ. Bạn có chắc chắn muốn thực hiện chức năng này không?"
        val spannable = SpannableString(fullText)

        val redPart = "xóa dữ liệu trên thiết bị của bạn"
        val start = fullText.indexOf(redPart)
        val end = start + redPart.length
        if (start >= 0) {
            spannable.setSpan(
                ForegroundColorSpan(Color.RED),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        builder.setMessage(spannable)

        builder.setNegativeButton("KHÔNG") { dialog, _ ->
            dialog.dismiss()
        }

        builder.setPositiveButton("CÓ") { dialog, _ ->
            showRestoreSuccess()
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.BLUE)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.RED)
    }

    override fun showRestoreSuccess() {
        Toast.makeText(this, "Đã xác nhận lấy lại dữ liệu", Toast.LENGTH_SHORT).show()
        // TODO: Gọi xử lý đồng bộ dữ liệu tại đây
    }
}
