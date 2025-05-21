package com.example.appquanly.LoginMethod

class Login_MethodPresenter(private val view: Login_MethodContract.View) :
    Login_MethodContract.Presenter {

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
