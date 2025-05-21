package com.example.appquanly.Login

// PasswordRecoveryView.kt
interface PasswordRecoveryView {
    fun showInvalidInputError()
    fun showPasswordRecoverySuccess(input: String)
    fun showPasswordRecoveryFailure()
}

