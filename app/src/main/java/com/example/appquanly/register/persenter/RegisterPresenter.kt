// RegisterPresenter.kt
package com.example.appquanly.register.persenter

import com.example.appquanly.register.model.RegisterModel
import com.example.appquanly.register.contract.RegisterView

class RegisterPresenter(private val view: RegisterView) {
    private val model = RegisterModel()

    fun handleRegister(emailOrPhone: String, password: String) {
        if (emailOrPhone.isEmpty() || password.isEmpty()) {
            view.showInputError()
        } else {
            val success = model.register(emailOrPhone, password)
            if (success) {
                view.showRegisterSuccess(emailOrPhone)
            } else {
                view.showRegisterFailure()
            }
        }
    }
}
