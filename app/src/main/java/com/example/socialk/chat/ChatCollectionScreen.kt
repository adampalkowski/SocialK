package com.example.socialk.chat

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.socialk.*
import com.example.socialk.R
import com.example.socialk.components.BottomBar
import com.example.socialk.di.ChatViewModel
import com.example.socialk.model.Chat
import com.example.socialk.model.UserData
import com.example.socialk.ui.theme.SocialTheme
import com.example.socialk.ui.theme.Typography
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


sealed class ChatCollectionEvent {
    object GoToProfile : ChatCollectionEvent()
    object LogOut : ChatCollectionEvent()
    object GoToSettings : ChatCollectionEvent()
    object GoToAddPeople : ChatCollectionEvent()
    object GoToSearch : ChatCollectionEvent()
    class GoToChat (chat: Chat): ChatCollectionEvent(){val chat=chat}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatCollectionScreen(
    onEvent: (ChatCollectionEvent) -> Unit,
    bottomNavEvent: (Destinations) -> Unit,
    chatViewModel: ChatViewModel
) {

    val data = remember { mutableStateOf(ArrayList<Chat>()) }
    val added_data_state = remember { mutableStateOf(false) }
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
                    .background(color = SocialTheme.colors.uiFloated)
                    .fillMaxWidth()
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {

                items(data.value.reversed()) {
                    chatItem(
                        onClick = { onEvent(ChatCollectionEvent.GoToChat(it)) },
                        profileUrl = if (it.chat_picture != null) {
                            it.chat_picture
                        } else if (it.user_one_username.equals(UserData.user!!.username)) {
                            if (it.user_two_username != null) {
                                if (it.user_two_profile_pic != null) {
                                    it.user_two_profile_pic
                                } else {
                                    ""
                                }
                            } else {
                                ""
                            }
                        } else if (it.user_two_username.equals(UserData.user!!.username)) {
                            if (it.user_one_username != null) {
                                if (it.user_one_profile_pic != null) {
                                    it.user_one_profile_pic
                                } else {
                                    ""
                                }
                            } else {
                                ""
                            }
                        } else {
                            ""
                        },
                        name = if (it.chat_name != null) {
                            it.chat_name
                        } else if (it.user_one_username.equals(UserData.user!!.username)) {
                            if (it.user_two_username != null) {
                                it.user_two_username
                            } else {
                                ""
                            }
                        } else if (it.user_two_username.equals(UserData.user!!.username)) {
                            if (it.user_one_username != null) {
                                it.user_one_username
                            } else {
                                ""
                            }
                        } else {
                            ""
                        },
                        lastMessage = it.recent_message,
                        lastMessageDate = it.recent_message_time
                    )
                }


            }
            Spacer(modifier = Modifier.height(64.dp))


        }

        BottomBar(onTabSelected = { screen -> bottomNavEvent(screen) }, currentScreen = Chats)
        //todo hardoced color
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp), contentAlignment = Alignment.TopEnd
        ) {
            Card(
                modifier = Modifier
                    .size(56.dp),
                border = BorderStroke(1.dp, color = SocialTheme.colors.uiFloated),
                shape = RoundedCornerShape(16.dp),
                onClick = { onEvent(ChatCollectionEvent.GoToSearch) }
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
    chatViewModel.chatCollectionsState.value.let {
        when (it) {
            is com.example.socialk.model.Response.Success -> {
                data.value = it.data
            }
            is com.example.socialk.model.Response.Loading -> {}
            is com.example.socialk.model.Response.Failure -> {}
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun chatItem(
    onClick: () -> Unit,
    profileUrl: String?,
    name: String?,
    lastMessage: String?,
    lastMessageDate: String?
) {
    Card(onClick = onClick, colors = CardDefaults.cardColors(containerColor = Color.Transparent)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(profileUrl)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.ic_person),
                    contentDescription = "image sent",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier .size(48.dp)
                        .clip(CircleShape)
                )


                    Spacer(modifier = Modifier.width(16.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween) {


                    Column(modifier=Modifier.weight(1f).padding(end=12.dp)) {
                        Text(
                            text = if (name != null) {
                                if(name.length>20){
                                    name.substring(0,20)+"..."
                                }else{
                                    name
                                }

                            } else {
                                ""
                            },
                            style = Typography.h2
                        )


                    Text(
                        text = if (lastMessage != null) {
                            if(lastMessage.length>30){
                                lastMessage.substring(0,30)+"..."
                            }else{
                                lastMessage
                            }
                        } else {
                            ""
                        },
                        style = Typography.h6
                    )
                }
                val parts= lastMessageDate?.split(" ")
                val date = parts?.get(0)
                val time = parts?.get(1)
                val today:Boolean= checkIfToday(date)
                
                Text(modifier = Modifier   .align(Alignment.CenterVertically),
                    text = if (lastMessageDate != null) {
                        if (today){
                            if (time==null){
                                ""
                            }else{
                                time.trim()
                            }

                        }else{
                            if (date==null){
                                ""
                            }else{
                                date.trim()
                            }
                        }

                    } else {
                        ""
                    },
                    style = Typography.h5
                )
            }
            }

        }
    }
    Box(
        modifier = Modifier
            .height(1.dp)
            .background(color = SocialTheme.colors.uiFloated)
            .fillMaxWidth()
    )

}
fun checkIfToday(date:String?):Boolean{
    if(date ==null){
        return false
    }else{
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dateToCompare = LocalDate.parse(date.toString(), formatter)
        return dateToCompare==currentDate
    }

}

