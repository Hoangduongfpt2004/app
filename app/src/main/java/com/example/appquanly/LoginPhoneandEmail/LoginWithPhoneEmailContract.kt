package com.example.appquanly.LoginPhoneandEmail

interface LoginWithPhoneEmailContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showLoginSuccess()
        fun showLoginError(message: String)
    }

    interface Presenter {
        fun handleLogin(username: String, password: String)
    }
}
