package com.example.appquanly.ForgotPassword.persenter

import com.example.appquanly.LoginPhoneandEmail.model.PasswordRecoveryModel
import com.example.appquanly.login.contract.PasswordRecoveryView

// PasswordRecoveryPresenter.kt
class PasswordRecoveryPresenter(private val view: PasswordRecoveryView) {

    private val model = PasswordRecoveryModel()

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
