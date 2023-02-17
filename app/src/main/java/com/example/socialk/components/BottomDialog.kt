package com.example.socialk.components

import android.widget.ImageButton
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAbsoluteAlignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.socialk.R
import com.example.socialk.chat.*
import com.example.socialk.create.ActivityTextFieldState
import com.example.socialk.di.ChatViewModel
import com.example.socialk.model.Activity
import com.example.socialk.model.ChatMessage
import com.example.socialk.model.Response
import com.example.socialk.model.UserData
import com.example.socialk.settings.SettingsEvent
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
    object AlertHideActivity : BottomDialogEvent()
    object removeUserFromActivity : BottomDialogEvent()
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
    val openDialog = remember { mutableStateOf(false)  }
    val displayParticipants = rememberSaveable { mutableStateOf(false) }
    BackHandler(state.isVisible) {
        coroutineScope.launch { state.hide() }
    }
    SocialTheme() {
        ModalBottomSheetLayout(sheetShape = RoundedCornerShape(12.dp),
            sheetState = state, sheetBackgroundColor = SocialTheme.colors.uiFloated,
            sheetContentColor = SocialTheme.colors.textPrimary, scrimColor = Color(0x7E313131),
            sheetContent = {
                if (displayParticipants.value){
                    DisplayParticipants(activity,onEvent={event->when(event){is BottomDialogEvent.GoBack->{displayParticipants.value=false}} })
                }else{
                    ActivitySettingsContent(displayParticipants,onEvent={event->
                        when(event){
                            is BottomDialogEvent.AlertHideActivity->{
                                openDialog.value=true
                            }
                        }
                    })
                    if(openDialog.value){
                        SocialDialog(
                            onDismiss = { openDialog.value=false },
                            onConfirm = {

                                    onEvent(BottomDialogEvent.removeUserFromActivity)
                                    openDialog.value=false
                                    coroutineScope.launch { state.hide() }

                                        },
                            onCancel = { openDialog.value=false },
                            title = "Hide activity?",
                            info ="You will no longer be able to see the activity, unless someone invites you again." ,
                            icon =R.drawable.ic_visibility_off,
                            actionButtonText = "Hide"
                        )
                    }
                }


            }
        ){
        }

    }

}

@Composable
fun DisplayParticipants(activity: Activity,onEvent:(BottomDialogEvent)->Unit) {
    Column() {
        Box(modifier = Modifier.background(color=SocialTheme.colors.uiBackground)
            .fillMaxWidth()
            .padding(8.dp)) {
            IconButton(modifier = Modifier.align(Alignment.CenterStart),onClick = { onEvent(BottomDialogEvent.GoBack) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    tint = SocialTheme.colors.textPrimary,
                    contentDescription = null
                )
            }

            Text(text = "Participants", style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 16.sp ), color = SocialTheme.colors.textPrimary, modifier = Modifier.align(Alignment.Center))
        }
        activity.participants_profile_pictures.forEach{
            UserDisplay(it.value,activity.participants_usernames[it.key]!!)
        }
    }

}

@Composable
fun UserDisplay(value: String, s: String) {
    Box(modifier =Modifier
        .fillMaxWidth().background(color=SocialTheme.colors.uiBackground)
        .padding(horizontal = 24.dp, vertical = 8.dp)){
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberAsyncImagePainter(value),
                    contentDescription = "profile image" , contentScale = ContentScale.Crop, modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)

                )
                Spacer(modifier = Modifier.width(24.dp))
                Text(text = s, modifier = Modifier,style = TextStyle(fontFamily = Inter, fontSize = 14.sp, fontWeight = FontWeight.SemiBold))
            }

    }
}


@Composable
fun ActivitySettingsContent(displayParticipants: MutableState<Boolean>,onEvent: (BottomDialogEvent) -> Unit) {
    Box(modifier = Modifier.background(color=SocialTheme.colors.uiBackground)){
        Column() {
            Spacer(modifier = Modifier.width(12.dp))
            SettingsItem(text="Display participants",icon=R.drawable.ic_group_not_filled, onClick ={displayParticipants.value=!displayParticipants.value} )
            SettingsItem(text="Hide activity",icon=R.drawable.ic_visibility_off,  onClick ={onEvent(BottomDialogEvent.AlertHideActivity)})
            SettingsItem(text="Copy activity link",icon=R.drawable.ic_link,  onClick ={/*TODO*/}  )
            SettingsItem(text="Share activity",icon=R.drawable.ic_share,  onClick ={/*TODO*/} )
            SettingsItem(text="Delete posted picture",icon=R.drawable.ic_hide_image, onClick ={/*TODO*/})
            SettingsItem(text="Suggest time change",icon=R.drawable.ic_update_time,  onClick ={/*TODO*/} )
            SettingsItem(text="Suggest date change",icon=R.drawable.ic_update_date, onClick ={/*TODO*/} )
            SettingsItem(text="Suggest location change",icon=R.drawable.ic_update_location, onClick ={/*TODO*/} )
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
                text = text, color = SocialTheme.colors.textPrimary, fontSize = 14.sp,
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
