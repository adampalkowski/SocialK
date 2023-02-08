package com.example.socialk.create

import com.example.socialk.signinsignup.TextFieldState
import com.example.socialk.signinsignup.textFieldStateSaver


class NumberTextFieldState :
    TextFieldState(validator = ::isTextValid, errorFor = ::textValidationError)

/**
 * Returns an error to be displayed or null if no error was found
 */


private fun textValidationError(number: String): String {
    return "Number is incorrect"
}

private fun isTextValid(number: String): Boolean {
    return number.length < 4
}

val NumberTextFieldStateSaver = textFieldStateSaver(NumberTextFieldState())