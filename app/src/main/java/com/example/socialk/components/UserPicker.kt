package com.example.socialk.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.socialk.R
import com.example.socialk.create.CreateEvent
import com.example.socialk.create.Divider
import com.example.socialk.create.Switch2
import com.example.socialk.di.ChatViewModel
import com.example.socialk.di.UserViewModel
import com.example.socialk.model.Chat
import com.example.socialk.model.Response
import com.example.socialk.model.User
import com.example.socialk.model.UserData
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme

@Composable
fun GroupPicker(modifier: Modifier, chatViewModel: ChatViewModel,    onEvent: (CreateEvent) -> Unit,) {
    val groups_flow = chatViewModel.groupsState.collectAsState()
    val displayGroups= remember {
        mutableStateOf(false)
    }
    Box(
        modifier = modifier

    ) {
        Column {
            Spacer(modifier = Modifier.height(12.dp))
            //TOP ROW INFORMATON
            Row(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_groups),
                    contentDescription = null,
                    tint = SocialTheme.colors.iconSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Groups",
                    fontFamily = Inter,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = SocialTheme.colors.iconSecondary
                )
                Spacer(modifier = Modifier.weight(1f))

            }

            Spacer(modifier = Modifier.height(12.dp))
            //GRID PICKER
            Divider()
            LazyRow(
                modifier = Modifier
                    .height(if (displayGroups.value){48.dp}else{0.dp})
                    .fillMaxWidth()
                    .background(color = Color(0xFFF4F4F4)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    Spacer(modifier = Modifier.width(24.dp))
                }
                groups_flow.value.let {
                    when (it) {
                        is Response.Success -> {
                            displayGroups.value = it.data.size>0
                            items(it.data) {

                                GroupPickerItem(chat = it, onEvent = onEvent)
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                        is Response.Loading -> {}
                        is Response.Failure -> {}
                    }
                }
                item {
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }
            Divider()

        }


    }
}


@Composable
fun UserPicker(
    modifier: Modifier,
    onEvent: (CreateEvent) -> Unit,
    userViewModel: UserViewModel,

) {
    val checkedState = remember { mutableStateOf(false) }
    val usersExist = remember { mutableStateOf(false) }

    val friends_flow = userViewModel.friendState.collectAsState()
    val more_friends_flow = userViewModel.friendMoreState.collectAsState()
    Box(
        modifier = modifier

    ) {
        Column {
            Spacer(modifier = Modifier.height(16.dp))
            //TOP ROW INFORMATON
            Row(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_group_not_filled),
                    contentDescription = null,
                    tint = SocialTheme.colors.iconSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Friends",
                    fontFamily = Inter,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = SocialTheme.colors.iconSecondary
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "All friends",
                    fontFamily = Inter,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = SocialTheme.colors.textInteractive
                )
                Spacer(modifier = Modifier.width(12.dp))
                Switch2(onCheckedChange = {    onEvent(CreateEvent.AllFriendsSelected)
                    checkedState.value = it })
              /*Switch(
                    checked = checkedState.value, colors = SwitchDefaults.colors(
                        checkedThumbColor = SocialTheme.colors.textPrimary,
                        checkedTrackColor = SocialTheme.colors.textPrimary,
                        disabledCheckedTrackColor = SocialTheme.colors.iconPrimary,
                        disabledCheckedThumbColor = SocialTheme.colors.iconPrimary,
                        disabledUncheckedThumbColor = SocialTheme.colors.iconPrimary,
                        disabledUncheckedTrackColor = SocialTheme.colors.iconPrimary,
                        uncheckedThumbColor = SocialTheme.colors.iconPrimary,
                        uncheckedTrackColor = SocialTheme.colors.iconPrimary
                    ),

                    onCheckedChange = {
                    }
                )*/

            }

            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(
                visible = !checkedState.value,
                enter = slideInHorizontally(),
                exit = slideOutHorizontally()
            ) {
                //GRID PICKER
                Divider()
                LazyColumn {
                    //todo paginate the friends and groups
                    friends_flow.value.let {
                        when (it) {
                            is Response.Success -> {
                                items(it.data) {

                                    UserPickerItem(user = it, onEvent = onEvent)
                                    Divider()
                                }

                                usersExist.value=true
                            }

                            is Response.Loading -> {}
                            is Response.Failure -> {}
                        }
                    }
                    more_friends_flow.value.let {
                        when (it) {
                            is Response.Success -> {
                                items(it.data) {

                                    UserPickerItem(user = it, onEvent = onEvent)
                                    Divider()
                                }
                                item {
                                    Spacer(modifier = Modifier.height(48.dp))
                                }

                            }
                            is Response.Loading -> {}
                            is Response.Failure -> {}
                        }
                    }
                    item {
                        LaunchedEffect(true) {
                            if (usersExist.value) {
                                userViewModel?.getMoreFriends(UserData.user!!.id)
                            }
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(12.dp))

            Divider()
        }

    }
}


@Composable
fun SelectedName(it: User) {
    Card(
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, color = SocialTheme.colors.uiFloated)
    ) {
        Box(
            modifier = Modifier
                .background(color = SocialTheme.colors.uiBackground)
                .padding(4.dp)
        ) {
            Text(
                text = it.username.toString(),
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Light,
                    fontSize = 10.sp
                ),
                color = SocialTheme.colors.textPrimary
            )
        }

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GroupPickerItem(chat: Chat, onEvent: (CreateEvent) -> Unit) {
    var selected: Boolean by rememberSaveable {
        mutableStateOf(false)
    }
    Card(
        shape = RoundedCornerShape(6.dp), onClick = {
            if (!selected) {
                onEvent(CreateEvent.GroupSelected(chat))
            } else {
                onEvent(CreateEvent.GroupUnSelected(chat))
            }
            selected = !selected  },
        border = BorderStroke(1.dp, color = SocialTheme.colors.uiFloated)
    ) {
        Box(
            modifier = Modifier
                .background(color = SocialTheme.colors.uiBackground)
                .padding(vertical = 8.dp, horizontal = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically){
                if(chat.chat_picture!=null && chat.chat_picture!!.isNotEmpty()){
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(chat.chat_picture)
                            .crossfade(true)
                            .build(),
                        placeholder = painterResource(R.drawable.ic_add_photo),
                        contentDescription = "image sent",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(36.dp).background(color=SocialTheme.colors.uiBackground)
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = chat.chat_name.toString(),
                    style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    ),color=SocialTheme.colors.textPrimary.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Checkbox(modifier = Modifier.size(20.dp),
                    checked = selected,
                    colors = androidx.compose.material3.CheckboxDefaults.colors(
                        checkedColor = Color.Black.copy(alpha = 0.8f),
                        uncheckedColor = Color.Black.copy(alpha = 0.8f)
                    ),
                    onCheckedChange = {
                        if (!selected) {
                            onEvent(CreateEvent.GroupSelected(chat))
                        } else {
                            onEvent(CreateEvent.GroupUnSelected(chat))
                        }
                        selected = !selected })
            }


        }
    }

}
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UserPickerItem(user: User, onEvent: (CreateEvent) -> Unit) {
    var selected: Boolean by rememberSaveable {
        mutableStateOf(false)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        elevation = 0.dp,
        onClick = {
            if (!selected) {
                onEvent(CreateEvent.UserSelected(user))
            } else {
                onEvent(CreateEvent.UserUnSelected(user))
            }
            selected = !selected
        }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = if (selected) {
                        SocialTheme.colors.uiBackground
                    } else {
                        //todo hardcoded color
                        Color(0xFFF4F4F4)
                    }
                )
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.pictureUrl)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.ic_person),
                    contentDescription = "user picture",
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = user.username.toString(),
                    style = TextStyle(
                        fontFamily = Inter,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = SocialTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.weight(1f))
                Checkbox(
                    checked = selected,
                    colors = androidx.compose.material3.CheckboxDefaults.colors(
                        checkedColor = SocialTheme.colors.iconInteractive,
                        uncheckedColor = Color.Black.copy(alpha = 0.8f)
                    ),
                    onCheckedChange = {
                        if (!selected) {
                            onEvent(CreateEvent.UserSelected(user))
                        } else {
                            onEvent(CreateEvent.UserUnSelected(user))
                        }
                        selected = !selected })
            }
        }
    }


}

