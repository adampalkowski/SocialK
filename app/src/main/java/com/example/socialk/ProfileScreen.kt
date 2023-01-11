package com.example.socialk

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.socialk.chat.ChatEvent
import com.example.socialk.components.BottomBar
import com.example.socialk.components.BottomBarRow
import com.example.socialk.components.ScreenHeading
import com.example.socialk.home.HomeEvent
import com.example.socialk.home.cardHighlited
import com.example.socialk.home.cardnotHighlited
import com.example.socialk.settings.SettingsEvent
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.Ocean1
import com.example.socialk.ui.theme.SocialTheme
import com.example.socialk.ui.theme.Typography
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

sealed class ProfileEvent {
    object GoToProfile : ProfileEvent()
    object LogOut : ProfileEvent()
    object GoToSettings : ProfileEvent()
    object GoToEditProfile : ProfileEvent()
    object GoToHome : ProfileEvent()
    object GoToSearch : ProfileEvent()
}

data class TabRowItem(
    val title: String,
    val screen: @Composable () -> Unit,
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

// PROFILE PICTURE, NAME, USERNAME BOX
@Composable
fun profileInfo(profileUrl: String, username: String, name: String) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp)
    ) {
        //TODO HERE GOES THE LOADED PROFILE PICTURE
        Row(horizontalArrangement = Arrangement.Start) {

            Image(
                painter = rememberAsyncImagePainter(profileUrl),
                contentDescription = "profile image", modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = name, color = SocialTheme.colors.textPrimary, style = Typography.h5
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = username, color = SocialTheme.colors.textPrimary, style = TextStyle(
                        fontFamily = Inter,
                        fontWeight = FontWeight.Light,
                        fontSize = 14.sp,
                    )
                )
            }
        }
    }
}

//TODO : FINISH COMPONENT
@Composable
fun Memories(
    text: String,
) {
    Box(
        modifier = Modifier,
        contentAlignment = Alignment.Center,
    ) {

        Text(
            text ="MEMORIEsMEMORIEsMEMORIEsMEMORIEsMEMORIEs",
            style =TextStyle(fontSize = 100.sp),
            color = SocialTheme.colors.textPrimary
        )
    }
}
//TODO : FINISH COMPONENT
@Composable
fun LiveActivities(
    text: String,
) {
    Box(
        modifier = Modifier
        ,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text ="AcitivtiesAcitivtiesAcitivtiesAcitivtiesAcitivtiesAcitivtiesAcitivtiesAcitivties",
            style =TextStyle(fontSize = 100.sp),
            color = SocialTheme.colors.textPrimary
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun profileButton(onClick: () -> Unit, label: String, iconDrawable: Int) {
    Card(modifier = Modifier.background(color=SocialTheme.colors.uiBackground),
        border = BorderStroke(1.dp,color=SocialTheme.colors.uiFloated),
        shape = RoundedCornerShape(12.dp), onClick =onClick) {
        Box(modifier = Modifier
            .background(color = SocialTheme.colors.uiBackground)
            .padding(12.dp)) {
            Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = iconDrawable),
                    tint = SocialTheme.colors.iconPrimary,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label, color = SocialTheme.colors.iconPrimary, style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = Inter,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }

}

@Composable
fun ScoreElement(label: String, value: Int) {
    Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label, style = TextStyle(
                fontSize = 14.sp, fontFamily = Inter, fontWeight = FontWeight.Normal
            ),  color = SocialTheme.colors.textPrimary
        )
        Text(
            text = value.toString(), style = TextStyle(
                fontSize = 14.sp, fontFamily = Inter, fontWeight = FontWeight.SemiBold
            ),color = SocialTheme.colors.textPrimary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenHeading(onClickBack: () -> Unit, title: String, onClickSettings: () -> Unit) {
    Box(
        modifier = Modifier
            .background(color = Color.Transparent)
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(top = 4.dp)
    ) {
        IconButton(
            onClick = onClickBack,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                tint = SocialTheme.colors.iconInteractive,
            )
        }
        Text(
            text = title,
            fontFamily = Inter,
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            textAlign = TextAlign.Center,
            color = SocialTheme.colors.textPrimary
        )

        //settings button
        Card(
            modifier = Modifier
                .size(56.dp)
                .align(Alignment.CenterEnd)
                .background(color = SocialTheme.colors.uiBackground),
            border = BorderStroke(1.dp, color = SocialTheme.colors.uiFloated),
            shape = RoundedCornerShape(16.dp),onClick = onClickSettings

        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = SocialTheme.colors.uiBackground),
                contentAlignment = Alignment.Center
            ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings),
                        tint = SocialTheme.colors.iconPrimary,
                        contentDescription = null
                    )
            }
        }

    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProfileScreen(onEvent: (ProfileEvent) -> Unit, bottomNavEvent: (Destinations) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxSize(), color = SocialTheme.colors.uiBackground
    ) {
        val pagerState = rememberPagerState()
        val coroutineScope = rememberCoroutineScope()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp).verticalScroll(rememberScrollState())
        ) {

            ProfileScreenHeading(
                onClickBack = { onEvent(ProfileEvent.GoToHome) },
                onClickSettings = { onEvent(ProfileEvent.GoToSettings) }, title = "Profile"
            )
            Spacer(modifier = Modifier.height(12.dp))

            //TODO:HARDCODED URL
            //PROFILE BOX
            profileInfo(
                profileUrl = "https://firebasestorage.googleapis.com/v0/b/socialv2-340711.appspot.com/o/uploads%2F1662065348037.null?alt=media&token=40cebce4-0c53-470c-867f-d9d34cba63ab",
                username = "adadmo_12",
                name = "Adam Pałkowski"
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
                color =  SocialTheme.colors.textPrimary
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
                profileButton(
                    label = "Edit profile",
                    iconDrawable = R.drawable.ic_edit_profile,
                    onClick = {onEvent(ProfileEvent.GoToEditProfile)})
                Spacer(modifier = Modifier.width(8.dp))
                profileButton(label = "Groups", iconDrawable = R.drawable.ic_groups, onClick = {})
                Spacer(modifier = Modifier.width(8.dp))
                profileButton(
                    label = "Add friends",
                    iconDrawable = R.drawable.ic_add_person,
                    onClick = {onEvent(ProfileEvent.GoToSearch)})
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
        BottomBar(onTabSelected = { screen -> bottomNavEvent(screen) }, currentScreen = Profile)
    }
}
@Preview
@Composable
fun previewProfileScreen() {
    SocialTheme() {
        ProfileScreen(onEvent = {}, bottomNavEvent = {})
    }
}