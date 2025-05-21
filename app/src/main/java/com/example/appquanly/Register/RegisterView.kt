
package com.example.appquanly.Register

interface RegisterView {
    fun showInputError()
    fun showRegisterSuccess(emailOrPhone: String)
    fun showRegisterFailure()
}
