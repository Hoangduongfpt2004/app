package com.example.appquanly.login

// LoginPresenter.kt
class LoginPresenter(private val view: LoginView) {

    private val model = LoginModel()

    fun onFacebookLoginClicked() {
        if (model.loginWithFacebook()) {
            // Đăng nhập thành công, chuyển hướng
        } else {
            view.showFacebookLoginFailed()
        }
    }

    fun onGoogleLoginClicked() {
        if (model.loginWithGoogle()) {
            // Đăng nhập thành công, chuyển hướng
        } else {
            view.showGoogleLoginFailed()
        }
    }

    fun onPhoneEmailLoginClicked() {
        if (model.loginWithPhoneOrEmail()) {
            view.showPhoneEmailLogin()
        } else {
            // Hiển thị lỗi nếu cần
        }
    }
}
