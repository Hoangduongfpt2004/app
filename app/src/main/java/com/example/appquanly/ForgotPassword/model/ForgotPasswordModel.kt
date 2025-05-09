package com.example.appquanly.ForgotPassword.model

// ForgotPasswordModel.kt
class ForgotPasswordModel {
    fun recoverPassword(input: String): Boolean {
        // Xử lý khôi phục mật khẩu (gọi API, gửi email hoặc OTP...)
        // Ví dụ đơn giản là trả về true nếu input không rỗng
        return input.isNotEmpty()
    }
}
