package com.example.socialk.chat

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.socialk.*
import com.example.socialk.R
import com.example.socialk.components.BottomBar
import com.example.socialk.components.BottomBarRow
import com.example.socialk.signinsignup.AuthViewModel
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.Ocean1
import com.example.socialk.ui.theme.SocialTheme
import com.example.socialk.ui.theme.Typography


sealed class ChatEvent {
    object GoToProfile : ChatEvent()
    object LogOut : ChatEvent()
    object GoToSettings : ChatEvent()
    object GoToAddPeople : ChatEvent()
    object GoToSearch : ChatEvent()

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(onEvent: (ChatEvent) -> Unit, bottomNavEvent: (Destinations) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxSize(), color = SocialTheme.colors.uiBackground
    ) {


        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 48.dp)
                    .padding(top = 8.dp)
                    .heightIn(48.dp), contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Chats",
                    style = com.example.socialk.ui.theme.Typography.h3
                )

            }
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .background(color =SocialTheme.colors.uiFloated)
                    .fillMaxWidth()
            )

            Column(modifier = Modifier.verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                chatItem(
                    profileUrl = "https://firebasestorage.googleapis.com/v0/b/socialv2-340711.appspot.com/o/uploads%2F1662065348037.null?alt=media&token=40cebce4-0c53-470c-867f-d9d34cba63ab",
                    name = "Adam Pałkowski",
                    lastMessage = "To dopiero nie koniec",
                    lastMessageDate = "12:33"
                )
                Box(
                    modifier = Modifier
                        .height(1.dp)
                        .background(color = SocialTheme.colors.uiFloated).fillMaxWidth()
                )
                chatItem(
                    profileUrl = "https://firebasestorage.googleapis.com/v0/b/socialv2-340711.appspot.com/o/uploads%2F1662065348037.null?alt=media&token=40cebce4-0c53-470c-867f-d9d34cba63ab",
                    name = "Adam Pałkowski",
                    lastMessage = "To dopiero nie koniec",
                    lastMessageDate = "12:33"
                )
                Box(
                    modifier = Modifier
                        .height(1.dp)
                        .background(color =SocialTheme.colors.uiFloated).fillMaxWidth()
                )
                chatItem(
                    profileUrl = "https://firebasestorage.googleapis.com/v0/b/socialv2-340711.appspot.com/o/uploads%2F1662065348037.null?alt=media&token=40cebce4-0c53-470c-867f-d9d34cba63ab",
                    name = "Adam Pałkowski",
                    lastMessage = "To dopiero nie koniec",
                    lastMessageDate = "12:33"
                )
                Box(
                    modifier = Modifier
                        .height(1.dp)
                        .background(color = SocialTheme.colors.uiFloated).fillMaxWidth()
                )
                chatItem(
                    profileUrl = "https://firebasestorage.googleapis.com/v0/b/socialv2-340711.appspot.com/o/uploads%2F1662065348037.null?alt=media&token=40cebce4-0c53-470c-867f-d9d34cba63ab",
                    name = "Adam Pałkowski",
                    lastMessage = "To dopiero nie koniec",
                    lastMessageDate = "12:33"
                )
                Spacer(modifier = Modifier.height(64.dp))
            }
        }

        BottomBar(onTabSelected = { screen -> bottomNavEvent(screen) }, currentScreen = Chats)
        //todo hardoced color
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp), contentAlignment = Alignment.TopEnd ) {
            Card(
                modifier = Modifier
                    .size(56.dp)
                  ,
                border = BorderStroke(1.dp, color =SocialTheme.colors.uiFloated),
                shape = RoundedCornerShape(16.dp),
                onClick = { onEvent(ChatEvent.GoToSearch) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = SocialTheme.colors.uiBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add_person),
                        tint = SocialTheme.colors.iconPrimary,
                        contentDescription = null
                    )
                }
            }
        }


    }
}


@Composable
fun chatItem(profileUrl: String, name: String, lastMessage: String, lastMessageDate: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberAsyncImagePainter(profileUrl),
                contentDescription = "profile image", modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column() {
                Text(
                    text = name,
                    style = Typography.h2
                )
                Text(
                    text = lastMessage,
                    style =Typography.h5
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = lastMessageDate,
                style = Typography.h5
            )
        }


    }

}
