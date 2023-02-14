package com.example.socialk.settings

import android.provider.CalendarContract.Colors
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.socialk.R
import com.example.socialk.components.ScreenHeading
import com.example.socialk.components.SocialDialog
import com.example.socialk.model.UserData
import com.example.socialk.signinsignup.AuthViewModel
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.Ocean1
import com.example.socialk.ui.theme.SocialTheme

sealed class SettingsEvent {
    object GoToProfile : SettingsEvent()
    object LogOut : SettingsEvent()
    object GoToSettings : SettingsEvent()
    object GoToHome : SettingsEvent()

}

@Composable
fun SettingsScreen(viewModel: AuthViewModel?, onEvent: (SettingsEvent) -> Unit) {
    val openDialog = remember { mutableStateOf(false)  }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(SocialTheme.colors.uiBackground), color = SocialTheme.colors.uiBackground
    ) {

        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            ScreenHeading(title = "Settings",onClick={onEvent(SettingsEvent.GoToProfile)})
            Spacer(modifier = Modifier.height(12.dp))
            settingsDivider(text = "Account")
            settingsItem(
                text = "Reset your password",
                icon = R.drawable.ic_reset_passwrod,
                onClick = {})
            settingsItem(text = "Update email", icon = R.drawable.ic_mail, onClick = {})
            settingsItem(text = "Dark mode", icon = R.drawable.ic_dark_mode, onClick = {})
            settingsItem(text = "Notification", icon = R.drawable.ic_notification, onClick = {})
            settingsDivider(text = "Login")
            settingsItem(text = "Log out", icon = R.drawable.ic_log_out, onClick = {
                viewModel?.logout()
                onEvent(SettingsEvent.LogOut)
            })
            settingsItem(
                text = "Delete account",
                icon = R.drawable.ic_delete_forever,
                onClick = {openDialog.value=true })
        }
        if(openDialog.value){
            SocialDialog(
                onDismiss = { openDialog.value=false },
                onConfirm = {
                    viewModel?.deleteAccount(UserData.user!!.id)
                    viewModel?.deleteAuth()
                    onEvent(SettingsEvent.LogOut)
                            },

                onCancel = { openDialog.value=false },
                title = "Delete account?",
                info ="Confirm deleting the account, this will permanently remove he account and all the information connected with it" ,
                icon =R.drawable.ic_delete,
                actionButtonText = "Delete"
            )
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun settingsItem(text: String, icon: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(color = SocialTheme.colors.uiBackground), shape = RoundedCornerShape(0.dp), colors = CardDefaults.cardColors(
            containerColor =SocialTheme.colors.uiBackground,
        ), onClick = onClick
    ) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(24.dp))
            Icon(
                painter = painterResource(id = icon),
                tint = SocialTheme.colors.iconPrimary,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text, color = Color.Black, fontSize = 16.sp,
                style = MaterialTheme.typography.body2, textAlign = TextAlign.Center
            )
            Spacer(Modifier.weight(1f))
            Icon(
                painter = painterResource(id = R.drawable.ic_right),
                tint = SocialTheme.colors.iconPrimary,
                contentDescription = null
            )
            Spacer(Modifier.width(24.dp))
        }

    }
}

//todo hardcoded color
@Composable
fun settingsDivider(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)

            .background(color = SocialTheme.colors.uiBorder), contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(24.dp))
            Text(text = text, color = Color(0xFF5E5F66), style = MaterialTheme.typography.body2)

        }
    }
}


