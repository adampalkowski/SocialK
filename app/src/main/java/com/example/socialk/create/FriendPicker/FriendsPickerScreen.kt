package com.example.socialk.create.FriendPicker

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.internal.enableLiveLiterals
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialk.R
import com.example.socialk.components.GroupPicker
import com.example.socialk.components.ScreenHeading
import com.example.socialk.components.UserPicker
import com.example.socialk.create.CreateEvent
import com.example.socialk.create.Divider
import com.example.socialk.di.ChatViewModel
import com.example.socialk.di.UserViewModel
import com.example.socialk.model.Activity
import com.example.socialk.model.Chat
import com.example.socialk.model.User
import com.example.socialk.model.UserData
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme

sealed class FriendsPickerEvent() {
    object GoBack : FriendsPickerEvent()
    class CreateActivity(val selected_ids: ArrayList<String>) : FriendsPickerEvent()
    class UpdateInvites(val selected_ids: ArrayList<String>) : FriendsPickerEvent()
    class CreateGroup(val selected_ids: ArrayList<String>) : FriendsPickerEvent()
}


@Composable
fun FriendsPickerScreen(
    type: String,
    chatViewModel: ChatViewModel,
    userViewModel: UserViewModel,
    onEvent: (FriendsPickerEvent) -> Unit
) {
    val _selected_list = rememberSaveable { mutableStateOf(listOf<User>()) }
    val selected_list by remember { _selected_list }

    val _selected_group_list = rememberSaveable { mutableStateOf(listOf<Chat>()) }
    val selected_group_list by remember { _selected_group_list }
    val all_friends = rememberSaveable { mutableStateOf(false) }

    fun addUser(user: User) {
        val newList = ArrayList(selected_list)
        newList.add(user)
        Log.d("FRIENDSPICKER", "ADD UER")

        _selected_list.value = newList
    }

    fun removeUser(user: User) {
        val newList = ArrayList(selected_list)
        newList.remove(user)
        _selected_list.value = newList
    }

    fun addGroup(chat: Chat) {
        val newList = ArrayList(selected_group_list)
        newList.add(chat)
        Log.d("FRIENDSPICKER", "ADD G")
        _selected_group_list.value = newList
    }

    fun removeGroup(chat: Chat) {
        val newList = ArrayList(selected_group_list)
        newList.remove(chat)
        _selected_group_list.value = newList
    }

    fun clearUsers() {
        _selected_list.value = ArrayList()
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(SocialTheme.colors.uiBackground), color = SocialTheme.colors.uiBackground
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                ScreenHeading(
                    onClick = { onEvent(FriendsPickerEvent.GoBack) },
                    title = "Select users"
                )
                GroupPicker(modifier = Modifier, chatViewModel, onEvent = { event ->
                    when (event) {
                        is CreateEvent.GroupSelected -> {
                            addGroup(event.chat)
                        }
                        is CreateEvent.GroupUnSelected -> {
                            removeGroup(event.chat)
                        }
                        else->{}
                    }
                })
                UserPicker(modifier = Modifier, onEvent = { event ->
                    when (event) {
                        is CreateEvent.UserSelected -> {
                            addUser(user = event.user)
                        }
                        is CreateEvent.UserUnSelected -> {
                            removeUser(user = event.user)
                        }
                        is CreateEvent.AllFriendsSelected -> {
                            all_friends.value = !all_friends.value
                            clearUsers()
                        }
                        else->{}
                    }

                }, userViewModel = userViewModel)
            }
            PickDisplay(
                type!!,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                selected_list,
                selected_group_list,
                all_friends.value,
                onEvent = onEvent
            )

        }


    }
}

@Composable
fun PickDisplay(
    type: String,
    modifier: Modifier,
    selectedList: List<User>,
    selectedGroupList: List<Chat>,
    all_friends: Boolean,
    onEvent: (FriendsPickerEvent) -> Unit,
) {
    var enabledButton by rememberSaveable {
        mutableStateOf(false)
    }
    enabledButton = selectedList.isNotEmpty() || selectedGroupList.isNotEmpty() || all_friends
    //todo hardcoded color
    Box(
        modifier = modifier
            .background(color = Color.Transparent)
            .height(72.dp)
    ) {
        Box(
            modifier = modifier
                .background(color = Color(0xFFF4F4F4))
                .padding(vertical = 8.dp)
        ) {
            Column() {
                Divider()
                LazyRow(
                    Modifier
                ) {
                    item {
                        Spacer(modifier = Modifier.width(24.dp))
                    }
                    item {
                        if (all_friends) {
                            Column {
                                Text(
                                    text = "All friends" + ", ",
                                    style = TextStyle(
                                        fontFamily = Inter,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp, textDecoration = TextDecoration.Underline
                                    ), color = SocialTheme.colors.textPrimary.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }

                    items(selectedGroupList) {
                        PickedItem(it)
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    items(selectedList) {
                        PickedItem(it)
                        Spacer(modifier = Modifier.width(4.dp))

                    }
                }
            }


            Spacer(modifier = Modifier.height(24.dp))

        }
        IconSocialButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 24.dp), enabled = enabledButton,
            text = if(type.equals("activity")){"Create activity"}else if(type.equals("group")){"Create group"}else{"Invite users"},
            backGroundColor = SocialTheme.colors.iconInteractive,
            onEvent = {
                var selected_list: ArrayList<String> = arrayListOf()
                if (all_friends) {
                    selected_list.addAll(UserData.user!!.friends_ids.keys)

                } else {
                    selected_list.addAll(selectedList.map { it.id })

                }
                for (el in selectedGroupList) {
                    selected_list.addAll(el.members)
                }
                if (type.equals("activity")) {
                    onEvent(FriendsPickerEvent.CreateActivity(selected_list))
                } else if(type.equals("update")){
                      onEvent(FriendsPickerEvent.UpdateInvites(selected_list))
                }else if (type.equals("group")) {
                    onEvent(FriendsPickerEvent.CreateGroup(selected_list))
                } else {
                    onEvent(FriendsPickerEvent.CreateActivity(selected_list))
                }}  ,
                textColor = SocialTheme.colors.textSecondary,
                elevation = 2.dp,
                icon = R.drawable.ic_done,
                iconTint = SocialTheme.colors.textSecondary,
                borderColor = SocialTheme.colors.iconInteractive
                )
            }

    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun IconSocialButton(
        enabled: Boolean,
        modifier: Modifier = Modifier,
        icon: Int = R.drawable.ic_add,
        iconTint: Color = SocialTheme.colors.iconPrimary,
        text: String,
        onEvent: () -> Unit,
        shape: Dp = 100.dp,
        backGroundColor: Color = SocialTheme.colors.uiBackground,
        elevation: Dp = 4.dp,
        textColor: Color = SocialTheme.colors.textPrimary,
        textStyle: TextStyle = TextStyle(
            fontFamily = Inter,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        ),
        borderColor: Color = SocialTheme.colors.uiFloated
    ) {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(shape),
            border = BorderStroke(1.dp, color = borderColor),
            elevation = if (enabled) {
                elevation
            } else {
                0.dp
            },
            onClick = {
                if (enabled) {
                    onEvent()
                } else {
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .background(color = backGroundColor)
                    .padding(vertical = 12.dp, horizontal = 24.dp)
            ) {
                Row {
                    Icon(
                        painter = painterResource(id = icon),
                        tint = if (enabled) {
                            iconTint
                        } else {
                            iconTint
                        },
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = text, style = textStyle, color = if (enabled) {
                            textColor
                        } else {
                            textColor
                        }
                    )
                }
            }
        }


    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun PickedItem(it: Chat) {

        Text(
            text = it.chat_name.toString() + ", ",
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            ), color = SocialTheme.colors.textPrimary.copy(alpha = 0.6f)
        )


    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun PickedItem(it: User) {

        Text(
            text = it.username.toString() + ", ",
            style = TextStyle(
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            ), color = SocialTheme.colors.textPrimary.copy(alpha = 0.6f)
        )

    }