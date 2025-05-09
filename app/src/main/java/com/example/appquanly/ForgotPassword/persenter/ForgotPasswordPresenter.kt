package com.example.appquanly.ForgotPassword.persenter

import com.example.appquanly.ForgotPassword.model.ForgotPasswordModel
import com.example.appquanly.ForgotPassword.contract.ForgotPasswordView

// ForgotPasswordPresenter.kt
class ForgotPasswordPresenter(private val view: ForgotPasswordView) {

    private val model = ForgotPasswordModel()

    fun onRecoverPasswordClicked(input: String) {
        if (input.isEmpty()) {
            view.showInvalidInputError()
        } else {
            val success = model.recoverPassword(input)
            if (success) {
                view.showPasswordRecoverySuccess(input)
            } else {
                view.showPasswordRecoveryFailure()
            }
        }
    }
}
