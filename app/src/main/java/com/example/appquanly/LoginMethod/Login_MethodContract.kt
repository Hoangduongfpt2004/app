package com.example.appquanly.LoginMethod

interface Login_MethodContract {
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
