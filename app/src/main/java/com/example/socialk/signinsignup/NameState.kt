package com.example.socialk.signinsignup

class NameState : TextFieldState(validator = ::isNameValid, errorFor = ::nameValidationError)

/**
 * Returns an error to be displayed or null if no error was found
 */


private fun nameValidationError(name: String): String {
    return "Invalid name: $name"
}
private fun isNameValid(name: String): Boolean {
    return name.isNotEmpty()
}
val NameStateSaver = textFieldStateSaver(NameState())