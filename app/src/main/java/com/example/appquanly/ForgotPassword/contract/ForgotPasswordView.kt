package com.example.appquanly.ForgotPassword.contract

// ForgotPasswordView.kt
interface ForgotPasswordView {
    fun showInvalidInputError()
    fun showPasswordRecoverySuccess(input: String)
    fun showPasswordRecoveryFailure()
}
