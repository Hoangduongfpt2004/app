package com.example.appquanly.login.contract

// PasswordRecoveryView.kt
interface PasswordRecoveryView {
    fun showInvalidInputError()
    fun showPasswordRecoverySuccess(input: String)
    fun showPasswordRecoveryFailure()
}
