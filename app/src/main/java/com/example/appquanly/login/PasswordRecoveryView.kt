package com.example.appquanly.login

// PasswordRecoveryView.kt
interface PasswordRecoveryView {
    fun showInvalidInputError()
    fun showPasswordRecoverySuccess(input: String)
    fun showPasswordRecoveryFailure()
}

