package com.example.socialk.create

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.example.socialk.signinsignup.TextFieldState
import com.example.socialk.ui.theme.SocialTheme


@Composable
fun ActivityTextField(hint:String,
    textState: TextFieldState= remember { ActivityTextFieldState()},
    focusManager: FocusManager,
    imeAction: ImeAction = ImeAction.Done,
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
                text = hint
            )

        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            cursorColor = SocialTheme.colors.textPrimary
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction
        ) , keyboardActions = KeyboardActions (onDone = {focusManager.clearFocus()}) )

}