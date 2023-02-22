package com.example.socialk.UserProfile

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.socialk.*
import com.example.socialk.R
import com.example.socialk.TabRowItem
import com.example.socialk.di.UserViewModel
import com.example.socialk.model.Response
import com.example.socialk.model.User
import com.example.socialk.model.UserData
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class UserProfileEvent {
    object GoToProfile : UserProfileEvent()
    object LogOut : UserProfileEvent()
    object GoToSettings : UserProfileEvent()
    object GoToEditProfile : UserProfileEvent()
    object GoToHome : UserProfileEvent()
    object GoToSearch : UserProfileEvent()
    object GoBack : UserProfileEvent()
    class GoToChat(user: User) : UserProfileEvent() {
        val user: User = user
    }

    class InviteUser(user: User) : UserProfileEvent() {
        val user: User = user
    }

    class RemoveInvite(user: User) : UserProfileEvent() {
        val user: User = user
    }
}
data class TabRowItem(
    val title: String,
    val screen: @Composable () -> Unit
)
val tabRowItems = listOf(
    TabRowItem(
        title = "Live activities",
        screen = { LiveActivities(text = "Live activities") },
    ),
    TabRowItem(
        title = "Memories",
        screen = { Memories(text = "Memories") },
    ),

    )
//user here is the searched user profile
@OptIn(ExperimentalPagerApi::class)
@Composable
fun UserProfileScreen(
    viewModel: UserProfileViewModel,
    user: User?,
    userViewModel: UserViewModel?,
    onEvent: (UserProfileEvent) -> Unit
) {
    val user_flow = userViewModel?.userState?.collectAsState()
    val retrieved_user= remember{ mutableStateOf<User?>(user) }
    user_flow?.value.let {
        when (it) {
            is Response.Success -> {
                retrieved_user.value=it.data
            }
            is Response.Failure -> {}
            is Response.Loading -> {}
        }
    }
    Surface(
        modifier = Modifier
            .fillMaxSize(), color = SocialTheme.colors.uiBackground
    ) {
        val pagerState = rememberPagerState()
        val coroutineScope = rememberCoroutineScope()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {

            ProfileScreenHeading(
                onClickBack = { onEvent(UserProfileEvent.GoToHome) },
                onClickSettings = { onEvent(UserProfileEvent.GoToSettings) }, title = "Profile"
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (retrieved_user.value != null) {
                retrieved_user.value.let {user->
//TODO:HARDCODED URL
                    //PROFILE BOX
                    profileInfo(
                        profileUrl = user?.pictureUrl,
                        username = user?.username!!,
                        name = user?.name!!
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    //BIBLIOGRAPHY
                    Text(
                        modifier = Modifier.padding(start = 12.dp),
                        text = "Biblography very long textvery long textvery long textve\n" +
                                "asdasdasdasdasdasdasdasfssdfgsdfg\n" +
                                " textvery long textdfghghhdfghhdfghghvery long ",
                        style = TextStyle(
                            fontSize = 16.sp, fontFamily = Inter, fontWeight = FontWeight.Light
                        ),
                        color = SocialTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    //SCORES
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ScoreElement(label = "Memories", value = 27)
                        ScoreElement(label = "Social Score", value = 12321213)
                        ScoreElement(label = "Friends", value = 12)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {

                        if (UserData.user!!.friends_ids.contains(user.id)) {

                        } else {
                            viewModel.inviteEventState.value.let {
                                if (it) {
                                    profileButton(
                                        label = "Remove",
                                        iconDrawable = R.drawable.ic_remove,
                                        onClick = {
                                            onEvent(UserProfileEvent.RemoveInvite(user))
                                            viewModel.inviteRemoved()
                                        })

                                } else {
                                    profileButton(
                                        label = "Invite",
                                        iconDrawable = R.drawable.ic_add,
                                        onClick = {
                                            onEvent(UserProfileEvent.InviteUser(user))
                                            viewModel.inviteSent()
                                        })
                                }
                            }
                        }
                        //CHAT ACCESSSIBLE ONLY IF FRIENDS !!!!!
                        if (user.friends_ids.containsKey(UserData.user!!.id)) {
                            profileButton(
                                onClick = { onEvent(UserProfileEvent.GoToChat(user)) },
                                label = "Message",
                                iconDrawable = R.drawable.ic_chat
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        contentColor = SocialTheme.colors.textPrimary,
                        containerColor = SocialTheme.colors.uiBackground

                    ) {
                        tabRowItems.forEachIndexed { index, item ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },

                                text = {
                                    Text(
                                        text = item.title,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }

                            )
                        }
                    }
                    HorizontalPager(
                        count = tabRowItems.size,
                        state = pagerState,
                    ) {
                        tabRowItems[pagerState.currentPage].screen()
                    }
                }


            }else{

            }

        }

    }

}