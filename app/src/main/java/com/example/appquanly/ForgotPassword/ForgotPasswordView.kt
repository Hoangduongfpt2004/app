package com.example.appquanly.ForgotPassword

// ForgotPasswordView.kt
interface ForgotPasswordView {
    fun showInvalidInputError()
    fun showPasswordRecoverySuccess(input: String)
    fun showPasswordRecoveryFailure()
}
