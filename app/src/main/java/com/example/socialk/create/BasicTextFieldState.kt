package com.example.socialk.create

import com.example.socialk.signinsignup.TextFieldState
import com.example.socialk.signinsignup.textFieldStateSaver


class BasicTextFieldState: TextFieldState(validator = ::isTextValid, errorFor = ::textValidationError)

/**
 * Returns an error to be displayed or null if no error was found
 */


private fun textValidationError(name:String): String {
    return "Text field is incorrect"
}
private fun isTextValid(name: String): Boolean {
    return true
}
val  BasicTextFieldStateSaver = textFieldStateSaver(BasicTextFieldState())