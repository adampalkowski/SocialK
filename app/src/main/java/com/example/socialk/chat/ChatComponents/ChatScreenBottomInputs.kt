package com.example.socialk.chat.ChatComponents

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialk.R
import com.example.socialk.chat.ChatEvent
import com.example.socialk.signinsignup.TextFieldState
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatScreenBottomInputs(
    modifier: Modifier? = Modifier,
    keyboardController: SoftwareKeyboardController?,
    onEvent: (ChatEvent) -> Unit, textState: TextFieldState, highlight: Boolean
) {
    var chatTextFieldFocused by remember { mutableStateOf(false) }
    var textSendAvailable by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically
        ) {

            if (!chatTextFieldFocused) {
                Spacer(modifier = Modifier.width(12.dp))

                        ChatButton(onEvent = {onEvent(ChatEvent.OpenLocationDialog)}, R.drawable.ic_pin_drop)


                Spacer(modifier = Modifier.width(6.dp))

                    ChatButton(
                        onEvent = { onEvent(ChatEvent.OpenGallery) },
                        R.drawable.ic_photo_library
                    )


                Spacer(modifier = Modifier.width(6.dp))

                    ChatButton(onEvent = {
                        onEvent(ChatEvent.Highlight)

                    }, R.drawable.ic_highlight, selected = highlight)
            } else {
                Spacer(modifier = Modifier.width(6.dp))
                ChatButton(onEvent = {
                    chatTextFieldFocused = false
                    keyboardController?.hide()
                }, R.drawable.ic_right_close)
            }
            Spacer(modifier = Modifier.width(6.dp))

            Card(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(48.dp, 128.dp).animateContentSize (),
                border = BorderStroke(1.dp, color = SocialTheme.colors.uiFloated),
                shape = RoundedCornerShape(8.dp),
                backgroundColor = SocialTheme.colors.uiBackground,
                elevation = 0.dp
            ) {

                    Box(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {

                        BasicTextField(modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged {
                                if (it.isFocused) {
                                    chatTextFieldFocused = true
                                }
                            }, decorationBox = { innerTextField ->
                            if (textState.text.isEmpty()) {
                                Text(
                                    text = "Aa",
                                    color = SocialTheme.colors.iconPrimary,
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontFamily = Inter,
                                        fontWeight = FontWeight.Normal
                                    )
                                )
                            }
                            innerTextField()
                        },
                            maxLines = 5, value = textState.text, onValueChange = {

                                textState.text = it
                                if (textState.text.length > 0) {
                                    chatTextFieldFocused = true
                                    textSendAvailable = true
                                }
                                if (textState.text.length == 0) {
                                    chatTextFieldFocused = false
                                    textSendAvailable = false
                                }
                            },
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = Inter,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }


            }
            if (!chatTextFieldFocused) {
                if (!textSendAvailable) {
                    Spacer(modifier = Modifier.width(6.dp))
                    ChatButton(onEvent = {onEvent(ChatEvent.LiveInvite)}, R.drawable.ic_person_waving)
                } else {
                    Spacer(modifier = Modifier.width(6.dp))
                    SendButton(
                        onEvent = { onEvent(ChatEvent.SendMessage(textState.text)) },
                        icon = R.drawable.ic_send,
                        available = textSendAvailable
                    )

                }
            } else {
                Spacer(modifier = Modifier.width(6.dp))
                SendButton(
                    onEvent = { onEvent(ChatEvent.SendMessage(textState.text)) },
                    icon = R.drawable.ic_send,
                    available = textSendAvailable
                )
            }
            Spacer(modifier = Modifier.width(12.dp))


        }
    }
}