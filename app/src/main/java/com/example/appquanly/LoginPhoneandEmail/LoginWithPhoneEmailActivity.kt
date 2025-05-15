package com.example.appquanly.LoginPhoneandEmail

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.appquanly.R

class LoginWithPhoneEmailActivity : AppCompatActivity(), LoginWithPhoneEmailContract.View {

    private lateinit var presenter: LoginWithPhoneEmailContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_with_phone_email)

        presenter = LoginWithPhoneEmailPresenter(this)

        val edtEmail = findViewById<EditText>(R.id.editEmail)
        val edtPassword = findViewById<EditText>(R.id.editPassword)
        val btnLogin = findViewById<Button>(R.id.buttonLogin)
        val txtForgotPassword = findViewById<TextView>(R.id.txtQuenMatKhau)

        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            presenter.handleLogin(email, password)
        }

        txtForgotPassword.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val inflater = LayoutInflater.from(this)
            val dialogView = inflater.inflate(R.layout.activity_forgot_password, null)
            builder.setView(dialogView)

            val edtEmailOrPhone = dialogView.findViewById<EditText>(R.id.edtEmailOrPhone)
            val btnRecoverPassword = dialogView.findViewById<Button>(R.id.btnRecoverPassword)

            btnRecoverPassword.setOnClickListener {
                val input = edtEmailOrPhone.text.toString().trim()
                if (input.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập email hoặc số điện thoại", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Đã gửi yêu cầu đến $input", Toast.LENGTH_SHORT).show()
                }
            }

            builder.setNegativeButton("Hủy") { dialog, _ -> dialog.dismiss() }
            builder.show()
        }
    }

    override fun showLoading() {}
    override fun hideLoading() {}
    override fun showLoginSuccess() {
        Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
    }

    override fun showLoginError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
