// RegisterModel.kt
package com.example.appquanly.Register

class RegisterModel {
    fun register(emailOrPhone: String, password: String): Boolean {
        // Giả sử đăng ký thành công nếu cả 2 field đều không rỗng
        return emailOrPhone.isNotEmpty() && password.isNotEmpty()
    }
}
