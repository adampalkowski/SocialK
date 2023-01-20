package com.example.socialk.PickUsername

import com.example.socialk.signinsignup.TextFieldState
import com.example.socialk.signinsignup.textFieldStateSaver


class UsernameState : TextFieldState(validator = ::isUsernameValid,
    errorFor = ::usernameValidationError){
    override fun getError(): String? {
        return if (showErrors()) {
            usernameFieldError()
        } else {
            null
        }
    }
}

/**
 * Returns an error to be displayed or null if no error was found
 */


private fun usernameValidationError(username: String): String {
    return "Invalid username: $username"
}
private fun isUsernameValid(username: String): Boolean {
    return username.isNotEmpty() &&username.length<30
}

private fun usernameFieldError(): String {
    return "Username is incorrect"
}

val UsernameStateSaver = textFieldStateSaver(UsernameState())