package com.example.appquanly.ForgotPassword

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appquanly.R

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var edtEmailOrPhone: EditText
    private lateinit var btnRecoverPassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        edtEmailOrPhone = findViewById(R.id.edtEmailOrPhone)
        btnRecoverPassword = findViewById(R.id.btnRecoverPassword)

        btnRecoverPassword.setOnClickListener {
            val input = edtEmailOrPhone.text.toString().trim()
            if (input.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email hoặc số điện thoại", Toast.LENGTH_SHORT).show()
            } else {
                // TODO: Gọi API hoặc xử lý logic khôi phục mật khẩu
                Toast.makeText(this, "Đã gửi yêu cầu đến $input", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
