package com.example.appquanly.login.view

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appquanly.R
import com.example.appquanly.LoginPhoneandEmail.view.LoginWithPhoneEmailActivity
import com.example.appquanly.register.view.RegisterActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val textFacebook = findViewById<TextView>(R.id.txt_Facebook)
        val textGoogle = findViewById<TextView>(R.id.txt_Google)
        val textPhoneEmail = findViewById<TextView>(R.id.txt_PhoneEmail)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        // Đăng nhập bằng Facebook
        textFacebook.setOnClickListener {
            Toast.makeText(this, "Đăng nhập bằng Facebook (chưa triển khai)", Toast.LENGTH_SHORT).show()
            // TODO: Tích hợp Facebook SDK
        }

        // Đăng nhập bằng Google
        textGoogle.setOnClickListener {
            Toast.makeText(this, "Đăng nhập bằng Google (chưa triển khai)", Toast.LENGTH_SHORT).show()
            // TODO: Tích hợp Google Sign-In
        }

        // Đăng nhập bằng SĐT hoặc email
        textPhoneEmail.setOnClickListener {
            val intent = Intent(this, LoginWithPhoneEmailActivity::class.java)
            startActivity(intent)
        }

        // Điều hướng đến đăng ký
        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
