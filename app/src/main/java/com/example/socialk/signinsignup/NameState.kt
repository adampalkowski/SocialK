package com.example.socialk.signinsignup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue


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
private fun setInitialText(text: String): String {
    return text
}
val NameStateSaver = textFieldStateSaver(NameState())