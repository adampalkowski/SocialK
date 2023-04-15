package com.example.socialk

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialk.components.ActivityEvent
import com.example.socialk.components.BottomDialogEvent
import com.example.socialk.model.Activity
import com.example.socialk.model.UserData
import com.example.socialk.ui.theme.SocialTheme
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase

sealed class ActivitySettingsEvent {
    object ReportActivity : ActivitySettingsEvent()
    object HideActivity : ActivitySettingsEvent()
    class GoToFriendsPicker(val activity: Activity) : ActivitySettingsEvent()


    class LeaveActivity(activity: Activity) : ActivitySettingsEvent() {
        val activity = activity
    }

    object GoToProfile : ActivitySettingsEvent()
    object GoBack : ActivitySettingsEvent()
    object AlertHideActivity : ActivitySettingsEvent()
    object removeUserFromActivity : ActivitySettingsEvent()
    object HideBottomDialog : ActivitySettingsEvent()

}

@Composable
fun ActivitySettingsContent(
    context: Context,
    clipboardManager: androidx.compose.ui.platform.ClipboardManager,
    displayParticipants: MutableState<Boolean>,
    onEvent: (ActivityEvent) -> Unit,
    activity: Activity, closeSettings: () -> Unit,leaveActivity:() ->Unit,likedActivity:Boolean
) {
    Box(modifier = Modifier.background(color = SocialTheme.colors.uiFloated)) {
        Column(Modifier.padding(horizontal = 12.dp)) {
            Spacer(modifier = Modifier.height(4.dp))
            if (activity.enableActivitySharing) {
                SettingsItem(
                    text = "Invite other users to activity",
                    icon = R.drawable.ic_share,
                    onClick = {     onEvent(ActivityEvent.GoToFriendsPicker(activity))

                    })
                Spacer(modifier = Modifier.height(4.dp))
            }
            if (activity.participants_ids.contains(UserData.user!!.id)||likedActivity) {
                SettingsItem(text = "Leave activity", icon = R.drawable.ic_log_out, onClick = {
                    leaveActivity()
                    onEvent(ActivityEvent.LeaveActivity(activity))
                })
                Spacer(modifier = Modifier.height(4.dp))
            }
            SettingsItem(
                text = "Display participants",
                icon = R.drawable.ic_group_not_filled,
                onClick = {
                    onEvent(ActivityEvent.DisplayParticipants(activity))
                 })
            Spacer(modifier = Modifier.height(4.dp))

            SettingsItem(text = "Hide activity", icon = R.drawable.ic_visibility_off, onClick = {
                onEvent(
                    ActivityEvent.HideActivity(
                        activity_id = activity.id,
                        user_id = UserData.user!!.id
                    )
                )
            })
            Spacer(modifier = Modifier.height(4.dp))

            SettingsItem(text = "Copy activity link", icon = R.drawable.ic_link, onClick = {
                val dynamicLink = Firebase.dynamicLinks.dynamicLink {
                    link = Uri.parse("https://link.friendup.app/" + "Activity" + "/" + activity.id)
                    domainUriPrefix = "https://link.friendup.app/"
                    // Open links with this app on Android
                    androidParameters { }
                }
                val dynamicLinkUri = dynamicLink.uri
                val localClipboardManager = clipboardManager
                localClipboardManager.setText(AnnotatedString(dynamicLinkUri.toString()))
                closeSettings()
                Toast.makeText(context, "Copied activity link to clipboard", Toast.LENGTH_LONG)
                    .show()
            })
            Spacer(modifier = Modifier.height(4.dp))
            SettingsItem(text = "Report activity", icon = R.drawable.ic_report, onClick = {
                onEvent(ActivityEvent.ReportActivity(activity_id = activity.id))
            })
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsItem(text: String, icon: Int, onClick: () -> Unit) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(color = Color.Transparent),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = SocialTheme.colors.uiBackground,
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(color = SocialTheme.colors.uiBackground),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                painter = painterResource(id = icon),
                tint = SocialTheme.colors.iconPrimary,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text, color = SocialTheme.colors.textPrimary, fontSize = 14.sp,
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
