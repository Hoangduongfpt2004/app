
package com.example.appquanly.register.contract

interface RegisterView {
    fun showInputError()
    fun showRegisterSuccess(emailOrPhone: String)
    fun showRegisterFailure()
}
