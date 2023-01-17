package com.example.socialk.create

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.socialk.signinsignup.TextFieldState


@Composable
fun ActivityTextField(
    textState: TextFieldState,
    focusManager: FocusManager
) {
    val maxChar = 250
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                textState.onFocusChange(focusState.isFocused)
                if (!focusState.isFocused) {
                    textState.enableShowErrors()
                }
            },

        textStyle = TextStyle(fontSize = 14.sp),
        value = textState.text,
        onValueChange = {
            textState.text = it.take(maxChar)
            if (it.length > maxChar) {
                focusManager.clearFocus()// Or receive a lambda function
            }
        },
        isError = textState.showErrors(),
        placeholder = {
            Text(
                color = Color(0xff757575),
                text = "What are you planning?"
            )

        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent
        ),

        )

}