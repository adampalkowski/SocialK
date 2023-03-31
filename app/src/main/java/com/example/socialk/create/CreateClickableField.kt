package com.example.socialk.create

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme
import com.example.socialk.R
import com.example.socialk.editField
import com.example.socialk.signinsignup.TextFieldError
import com.example.socialk.signinsignup.TextFieldState

//todo : FIX for smaller screen sizez !!!!!!
@Composable
fun CreateClickableTextField(
    modifier: Modifier,
    onClick: (Int) -> Unit,
    title: String,
    iconSize:Int=28,
    text: String = "value",
    description: String,
    icon: Int,
    inActiveTextColor:Color=SocialTheme.colors.textInteractive.copy(alpha = 0.5f),
    descriptionTextSize: Int=12,
    titleTextSize:Int=16,
    interactiveTextSize:Int=16,
    iconTint: Color =SocialTheme.colors.textPrimary.copy(0.75f),
    titleColor: Color=SocialTheme.colors.textPrimary,
    descriptionColor: Color=SocialTheme.colors.textPrimary.copy(alpha=0.5f),
    interactiveTextColor: Color=Color(0xFF034FB4)
) {
    Column(modifier = modifier) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                modifier = Modifier.size(iconSize.dp),
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = iconTint
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = title,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium,
                    fontSize = titleTextSize.sp,
                    color = titleColor
                )
                Text(
                    text = description,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Light,
                    fontSize = descriptionTextSize.sp,
                    color = descriptionColor
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            ClickableText(
                text = AnnotatedString(text), style = TextStyle(
                    fontSize = interactiveTextSize.sp, fontFamily = Inter,
                    fontWeight = FontWeight.SemiBold, color = interactiveTextColor
                ), onClick = onClick,
            )
        }

        Divider()
    }
}

@Composable
fun EditTextField(
    hint: String = "Enter a name for your activity",
    hideKeyboard: Boolean = false,
    iconSize:Int=28,
    onFocusClear: () -> Unit = {},
    textState: TextFieldState = remember { BasicTextFieldState() },
    onClick: () -> Unit,
    focusManager: FocusManager,
    modifier: Modifier,
    title: String,
    icon: Int,
    titleTextSize:Int=16,
    iconTint: Color =SocialTheme.colors.textPrimary.copy(0.75f),
    titleColor: Color=SocialTheme.colors.textPrimary,
    maxLetters:Int=30,
    onSaveValueCall:(Boolean)->Unit={}
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier=Modifier.size(iconSize.dp),
                painter = painterResource(id = icon),
                contentDescription = null,
                tint =iconTint
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontFamily = Inter,
                fontWeight = FontWeight.Medium,
                fontSize = titleTextSize.sp,
                color = titleColor
            )
            Spacer(modifier = Modifier.weight(1f))

        }
        Spacer(modifier = Modifier.height(6.dp))
        val descriptionFocusRequester = remember { FocusRequester() }
        editField(
            label = hint,
            maxLetters = maxLetters,
            onImeAction = { },
            editTextState = textState,
            modifier = Modifier.padding(horizontal = 24.dp).focusRequester(descriptionFocusRequester),
            onSaveValueCall = onSaveValueCall
        )
        Spacer(modifier = Modifier.height(12.dp))

        Divider()
    }
}

@Composable
fun CustomField(
    modifier: Modifier,
    onClick: (Int) -> Unit,
    title: String,
    iconSize:Int=28,
    text: String = "value",
    description: String,
    icon: Int,
    inActiveTextColor:Color=SocialTheme.colors.textInteractive.copy(alpha = 0.5f),
    descriptionTextSize: Int=12,
    titleTextSize:Int=16,
    interactiveTextSize:Int=16,
    iconTint: Color =SocialTheme.colors.textPrimary.copy(0.75f),
    titleColor: Color=SocialTheme.colors.textPrimary,
    descriptionColor: Color=SocialTheme.colors.textPrimary.copy(alpha=0.5f),
    interactiveTextColor: Color=Color(0xFF034FB4),
    content: @Composable () -> Unit
) {
    Column(modifier = modifier) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                modifier = Modifier.size(iconSize.dp),
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = iconTint
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = title,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium,
                    fontSize = titleTextSize.sp,
                    color = titleColor
                )
                Text(
                    text = description,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Light,
                    fontSize = descriptionTextSize.sp,
                    color = descriptionColor
                )
            }

            Spacer(modifier = Modifier.weight(1f))

        }

        content()
        Spacer(modifier = Modifier.height(12.dp))
        Divider()
    }
}
@Composable
fun CustomLocationField(
    hint: String = "Enter a name for your activity",
    hideKeyboard: Boolean = false,
    iconSize:Int=28,
    onFocusClear: () -> Unit = {},
    textState: TextFieldState = remember { BasicTextFieldState() },
    onClick: () -> Unit,
    focusManager: FocusManager,
    modifier: Modifier,
    title: String,
    icon: Int,
    titleTextSize:Int=16,
    iconTint: Color =SocialTheme.colors.textPrimary.copy(0.75f),
    titleColor: Color=SocialTheme.colors.textPrimary,
    interactiveTextColor: Color=Color(0xFF034FB4),
    interactiveTextSize:Int=16,
    onSaveValueCall:(Boolean)->Unit= {},
    maxLetters:Int=50
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier=Modifier.size(iconSize.dp),
                painter = painterResource(id = icon),
                contentDescription = null,
                tint =iconTint
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontFamily = Inter,
                fontWeight = FontWeight.Medium,
                fontSize = titleTextSize.sp,
                color = titleColor
            )
            Spacer(modifier = Modifier.weight(1f))
           /* Icon(modifier=Modifier.size(24.dp),painter = painterResource(id = R.drawable.ic_map_not_filled), tint =interactiveTextColor, contentDescription =null,)
            ClickableText(
                text = AnnotatedString("Visit map"), style = TextStyle(
                    fontSize = interactiveTextSize.sp, fontFamily = Inter,
                    fontWeight = FontWeight.Normal, color = interactiveTextColor
                ), onClick = {  },
            )*/

        }
        Spacer(modifier = Modifier.height(6.dp))
        val descriptionFocusRequester = remember { FocusRequester() }
        editField(
            label = hint,
            maxLetters = maxLetters,
            onImeAction = { },
            editTextState = textState,
            modifier = Modifier.padding(horizontal = 24.dp).focusRequester(descriptionFocusRequester),
            onSaveValueCall = onSaveValueCall
        )
        textState.getError()?.let { error ->
            Row() {
                Spacer(modifier = Modifier.width(24.dp))
                TextFieldError(textError = error)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        Divider()
    }

    /*  if (hideKeyboard) {
          focusManager.clearFocus()
          // Call onFocusClear to reset hideKeyboard state to false
          onFocusClear()
      }*/

}
