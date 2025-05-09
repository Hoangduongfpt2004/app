package com.example.appquanly.LoginPhoneandEmail.model

// PasswordRecoveryModel.kt
class PasswordRecoveryModel {
    fun recoverPassword(input: String): Boolean {
        // Xử lý khôi phục mật khẩu (gọi API, gửi email hoặc OTP...)
        return input.isNotEmpty()
    }
}
