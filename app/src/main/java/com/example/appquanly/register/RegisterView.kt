
package com.example.appquanly.register

interface RegisterView {
    fun showInputError()
    fun showRegisterSuccess(emailOrPhone: String)
    fun showRegisterFailure()
}
