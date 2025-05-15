package com.example.appquanly.LoginPhoneandEmail

class LoginWithPhoneEmailPresenter(private val view: LoginWithPhoneEmailContract.View) :
    LoginWithPhoneEmailContract.Presenter {

    override fun handleLogin(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            view.showLoginError("Vui lòng nhập đầy đủ thông tin")
            return
        }
        if (password.length < 4) {
            view.showLoginError("Mật khẩu quá ngắn (tối thiểu 4 ký tự)")
            return
        }
        else if (password.length < 20) {
            view.showLoginError("Mật khẩu quá dài")
        }


    }
}
