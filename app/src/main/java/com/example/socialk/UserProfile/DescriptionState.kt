package com.example.socialk.UserProfile

import com.example.socialk.PickUsername.UsernameState
import com.example.socialk.signinsignup.TextFieldState
import com.example.socialk.signinsignup.textFieldStateSaver


class DescriptionState : TextFieldState(validator = ::isDescriptionValid,
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


private fun usernameValidationError(description: String): String {
    return "Invalid description: $description"
}
private fun isDescriptionValid(description: String): Boolean {
    return description.length<=150
}

private fun usernameFieldError(): String {
    return "Description is incorrect"
}
val DescriptionStateSaver = textFieldStateSaver(DescriptionState())