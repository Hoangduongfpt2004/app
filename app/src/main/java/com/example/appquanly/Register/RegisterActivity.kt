package com.example.appquanly.Register


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.appquanly.R
import com.example.appquanly.Login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var edtPhoneOrEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var txtAgreement: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Thiết lập Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Ánh xạ view
        edtPhoneOrEmail = findViewById(R.id.edtPhoneOrEmail)
        edtPassword = findViewById(R.id.edtPassword)
        btnRegister = findViewById(R.id.btnRegister)
        txtAgreement = findViewById(R.id.txtAgreement)

        // Xử lý nút đăng ký
        btnRegister.setOnClickListener {
            val emailOrPhone = edtPhoneOrEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (emailOrPhone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            } else {
                // TODO: Thực hiện xử lý đăng ký ở đây (gọi API, lưu dữ liệu, v.v.)
                Toast.makeText(this, "Đăng ký thành công với: $emailOrPhone", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Xử lý sự kiện nhấn mũi tên quay lại
    override fun onSupportNavigateUp(): Boolean {
        // Quay về trang chủ chính (MainActivity)
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish() // Kết thúc RegisterActivity
        return true
    }
}