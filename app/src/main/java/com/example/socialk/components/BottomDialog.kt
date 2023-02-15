package com.example.socialk.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialk.R
import com.example.socialk.chat.*
import com.example.socialk.create.ActivityTextFieldState
import com.example.socialk.di.ChatViewModel
import com.example.socialk.model.Activity
import com.example.socialk.model.ChatMessage
import com.example.socialk.model.Response
import com.example.socialk.model.UserData
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

sealed class BottomDialogEvent {
    object GoToProfile : BottomDialogEvent()
    object LogOut : BottomDialogEvent()
    object GoToSettings : BottomDialogEvent()
    object GoToHome : BottomDialogEvent()
    object GoBack : BottomDialogEvent()
    object GoToChatUserSettings : BottomDialogEvent()
    class SendMessage(message: String) : BottomDialogEvent() {
        val message = message
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
fun BottomDialog(state:ModalBottomSheetState=rememberModalBottomSheetState(ModalBottomSheetValue.Hidden),
                 activity: Activity, type:String,onEvent:(BottomDialogEvent)->Unit,chatViewModel:ChatViewModel) {
    val coroutineScope = rememberCoroutineScope()
    BackHandler(state.isVisible) {
        coroutineScope.launch { state.hide() }
    }
    SocialTheme() {
        ModalBottomSheetLayout(sheetShape = RoundedCornerShape(12.dp),
            sheetState = state, sheetBackgroundColor = SocialTheme.colors.uiFloated,
            sheetContentColor = SocialTheme.colors.textPrimary, scrimColor = Color(0x7E313131),
            sheetContent = {
                if (type.equals("chat")){

                    Box(modifier = Modifier.background(color=SocialTheme.colors.uiBackground)){

                        val keyboardController = LocalSoftwareKeyboardController.current
                        val data = remember { mutableStateOf(ArrayList<ChatMessage>()) }
                        val textState = remember { ActivityTextFieldState() }
                        val added_data_state = remember { mutableStateOf(false) }
                        var messageOptionsVisibility by remember { mutableStateOf(false) }
                        Column() {
                            Text(text = activity.title, style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 18.sp))
                            Divider()

                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 12.dp)
                                    .defaultMinSize(minHeight = 300.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        keyboardController?.hide()
                                    },
                                reverseLayout = true
                            ) {
                                items(data.value) {
                                    if (it.sender_id== UserData.user!!.id){
                                        Spacer(modifier = Modifier.height(4.dp))
                                        ChatItemRight(textMessage =it.text, date=it.sent_time, onLongPress = {messageOptionsVisibility=true})
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }else{
                                        Spacer(modifier = Modifier.height(8.dp))
                                        ChatItemLeft(textMessage =it.text,date=it.sent_time, onLongPress = {messageOptionsVisibility=true}
                                            ,picture_url=it.sender_picture_url)
                                        Spacer(modifier = Modifier.height(8.dp))

                                    }

                                }

                            }
                            Divider()

                            ChatScreenBottomInputs(
                                modifier =Modifier.weight(1f),keyboardController,
                                onEvent = {
                                if (textState.text.length>0){
                                    onEvent(BottomDialogEvent.SendMessage(message = textState.text.trim()))

                                }
                                textState.text = ""
                            }, textState)

                        }

                        chatViewModel.messagesState.value.let {
                            when (it) {
                                is Response.Success -> {
                                    data.value =it.data
                                }
                                is Response.Loading -> {}
                                is Response.Failure -> {}
                            }
                        }

                    }
                }else{
                    Box(modifier = Modifier.background(color=SocialTheme.colors.uiBackground)){
                        Column() {
                            Spacer(modifier = Modifier.width(12.dp))
                            SettingsItem(text="Display participants",icon=R.drawable.ic_group_not_filled, onClick ={/*TODO*/} )
                            SettingsItem(text="Hide activity",icon=R.drawable.ic_visibility_off,  onClick ={/*TODO*/})
                            SettingsItem(text="Copy activity link",icon=R.drawable.ic_link,  onClick ={/*TODO*/}  )
                            SettingsItem(text="Share activity",icon=R.drawable.ic_share,  onClick ={/*TODO*/} )
                            SettingsItem(text="Delete posted picture",icon=R.drawable.ic_hide_image, onClick ={/*TODO*/})
                            SettingsItem(text="Suggest time change",icon=R.drawable.ic_update_time,  onClick ={/*TODO*/} )
                            SettingsItem(text="Suggest date change",icon=R.drawable.ic_update_date, onClick ={/*TODO*/} )
                            SettingsItem(text="Suggest location change",icon=R.drawable.ic_update_location, onClick ={/*TODO*/} )
                        }
                    }
                }


            }
        ){
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsItem(text: String, icon: Int, onClick: () -> Unit){
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(color = SocialTheme.colors.uiBackground),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = SocialTheme.colors.uiBackground,
        ),
        onClick = onClick
    ) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(12.dp))
            androidx.compose.material3.Icon(
                painter = painterResource(id = icon),
                tint = SocialTheme.colors.iconPrimary,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(12.dp))
            androidx.compose.material3.Text(
                text = text, color = Color.Black, fontSize = 14.sp,
                style = MaterialTheme.typography.body2, textAlign = TextAlign.Center
            )
            Spacer(Modifier.weight(1f))
            androidx.compose.material3.Icon(
                painter = painterResource(id = R.drawable.ic_right),
                tint = SocialTheme.colors.iconPrimary,
                contentDescription = null
            )
            Spacer(Modifier.width(24.dp))
        }

    }
}
