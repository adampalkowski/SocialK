package com.example.socialk

import android.widget.Space
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.socialk.chat.ChatComponents.SendButton
import com.example.socialk.chat.ChatEvent
import com.example.socialk.components.ScreenHeading
import com.example.socialk.create.ActivityTextFieldState
import com.example.socialk.create.ActivityTextStateSaver
import com.example.socialk.create.CreateEvent
import com.example.socialk.di.UserViewModel
import com.example.socialk.model.Response
import com.example.socialk.model.User
import com.example.socialk.model.UserData
import com.example.socialk.ui.theme.*
import kotlin.text.Typography

sealed class SearchEvent {
    object GoToProfile : SearchEvent()
    object LogOut : SearchEvent()
    object GoToSettings : SearchEvent()
    object GoToChat : SearchEvent()
    object GoBack : SearchEvent()
    class OnInviteAccepted (user:User): SearchEvent(){val user=user}
    class GoToUserProfile (user: User): SearchEvent(){val user=user}

}

@Composable
fun SearchScreen(userViewModel:UserViewModel?,onEvent: (SearchEvent) -> Unit) {

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = SocialTheme.colors.uiBackground),
        color = SocialTheme.colors.uiBackground
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //HEADING
            ScreenHeading(onClick = { onEvent(SearchEvent.GoBack) }, title = "Search")
            Spacer(modifier = Modifier.height(36.dp))
            //EDIT TEXT
            searchEditText(userViewModel,onEvent = onEvent)
            Spacer(modifier = Modifier.height(4.dp))
            //INFORMATION
            Text(
                text = "Search for new people by their username or \n share your profile link",
                textAlign = TextAlign.Center,
                style = com.example.socialk.ui.theme.Typography.h6,
                color = Color(0xFF666666)
            )
            //TEXT INVITES
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp), contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Invites",
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontFamily = Inter,
                        fontSize = 18.sp
                    ),
                    color = Color(0xff333333)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            //INVITES ROW
            LazyRow{
                userViewModel?.invitesStateFlow?.value.let { it->
                    when(it){
                                is Response.Success->{
                                    items(it.data) { item ->
                                        InviteCard(profileUrl = if (item.pictureUrl==null){"PIC"}else{item.pictureUrl!!},
                                            name =if (item.name==null){"NAME"}else{item.name!!},
                                            username =if (item.username==null){""}else{item.username!!} ,
                                            onClick = {onEvent(SearchEvent.OnInviteAccepted(item))})
                                        Spacer(modifier = Modifier.width(16.dp))
                                    }
                                }
                                is Response.Loading-> {
                                    item {
                                        Box(modifier =Modifier) {
                                            CircularProgressIndicator()

                                        }
                                    }
                                }
                                is Response.Failure->{

                                }
                    }
                }

            }



            Spacer(modifier = Modifier.height(12.dp))
            //TEXT YOU MAY WANT TO KNOW
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp), contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "You may want to know",
                    style = com.example.socialk.ui.theme.Typography.h3,
                    color = Color(0xff333333)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            //YOU MAY WANT TO KNOW ROW
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                ProposedCard(profileUrl = "https://firebasestorage.googleapis.com/v0/b/socialv2-340711.appspot.com/o/uploads%2F1662065348037.null?alt=media&token=40cebce4-0c53-470c-867f-d9d34cba63ab",
                    name = "AdamPałkowskiPałkowskiPa",
                    username = "AdamPaPa",
                    onClick = {})
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .height(120.dp)
                        .width(1.dp)
                        .background(color = SocialTheme.colors.uiFloated)
                )

                Spacer(modifier = Modifier.width(16.dp))
                ProposedCard(profileUrl = "https://firebasestorage.googleapis.com/v0/b/socialv2-340711.appspot.com/o/uploads%2F1662065348037.null?alt=media&token=40cebce4-0c53-470c-867f-d9d34cba63ab",
                    name = "Adam Pałkowski",
                    username = "adamo12321",
                    onClick = {})
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .height(120.dp)
                        .width(1.dp)
                        .background(color = SocialTheme.colors.uiFloated)
                )

                Spacer(modifier = Modifier.width(16.dp))
                ProposedCard(profileUrl = "https://firebasestorage.googleapis.com/v0/b/socialv2-340711.appspot.com/o/uploads%2F1662065348037.null?alt=media&token=40cebce4-0c53-470c-867f-d9d34cba63ab",
                    name = "Adam Pałkowski",
                    username = "adamo12321",
                    onClick = {})
            }

            Spacer(modifier = Modifier.height(48.dp))

            //TODO HARDCODED NAME
            ButtonLink(onClick = {}, username = "name")
        }
    }
    userViewModel?.isInviteAcceptedState?.value.let {
        when(it){
            is Response.Failure ->{

            }
            is Response.Loading ->{

            }
            is Response.Success ->{

            }
        }
    }

}

@Composable
fun ButtonLink(onClick: () -> Unit, username: String) {
    Box(
        modifier = Modifier

            .padding(horizontal = 16.dp, vertical = 12.dp), contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_link),
                contentDescription = null,
                tint = Ocean8
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "friendupp.app/" + username,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                ),
                color = Ocean8
            )
        }
    }

}

@Composable
fun ProposedCard(profileUrl: String, name: String, username: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = rememberAsyncImagePainter(profileUrl),
            contentDescription = "invite profile image", modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            modifier = Modifier.width(120.dp),
            textAlign = TextAlign.Center,
            text = name,
            style = com.example.socialk.ui.theme.Typography.h5,
            color = SocialTheme.colors.textPrimary
        )
        Text(
            modifier = Modifier.width(120.dp),
            textAlign = TextAlign.Center,
            text = username,
            style = com.example.socialk.ui.theme.Typography.h6,
            color = SocialTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(16.dp))
        searchActionButton("Invite", onClick = onClick)
    }
}


@Composable
fun InviteCard(profileUrl: String, name: String, username: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = rememberAsyncImagePainter(profileUrl),
            contentDescription = "invite profile image", modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            modifier = Modifier.width(120.dp),
            textAlign = TextAlign.Center,
            text = name,
            style = com.example.socialk.ui.theme.Typography.h5,
            color = SocialTheme.colors.textPrimary
        )
        Text(
            modifier = Modifier.width(120.dp),
            textAlign = TextAlign.Center,
            text = username,
            style = com.example.socialk.ui.theme.Typography.h6,
            color = SocialTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(16.dp))
        searchActionButton("Accept", onClick = onClick)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun searchActionButton(label: String, onClick: () -> Unit) {
    Card(onClick=onClick,
        shape = RoundedCornerShape(8.dp), elevation = 0.dp, border = BorderStroke(
            1.dp,
            color = if (label.equals("Accept")) {
                if (isSystemInDarkTheme()) Black else Color(0xFFFFFFFF)
            } else SocialTheme.colors.uiFloated
        )
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (label.equals("Accept")) {
                        if (isSystemInDarkTheme()) Ocean11 else Ocean8
                    } else SocialTheme.colors.uiBackground
                )
                .padding(horizontal = 16.dp, vertical = 6.dp), contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = com.example.socialk.ui.theme.Typography.button,
                color = if (label.equals("Accept")) Color.White else SocialTheme.colors.textPrimary
            )
        }
    }

}


@Composable
fun searchEditText(userViewModel: UserViewModel?,onEvent: (SearchEvent) -> Unit) {
    val usernameTextSaver by rememberSaveable(stateSaver = ActivityTextStateSaver) {
        mutableStateOf(ActivityTextFieldState())
    }
    var textSendAvailable by remember { mutableStateOf(false) }
    val userFlow=userViewModel?.userState?.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {


            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, color = SocialTheme.colors.uiFloated)
            ) {
                Box(
                    modifier = Modifier
                        .background(color = SocialTheme.colors.uiBackground)
                        .padding(horizontal = 16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add_person),
                            contentDescription = null,
                            tint = SocialTheme.colors.iconPrimary
                        )
                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = usernameTextSaver.text,
                            placeholder = {
                                Text(
                                    text = "Search by username",
                                    style = com.example.socialk.ui.theme.Typography.h4,
                                    color = Color(
                                        0xFF939393
                                    )
                                )
                            },
                            textStyle = com.example.socialk.ui.theme.Typography.h4,
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = SocialTheme.colors.textPrimary,
                                backgroundColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            onValueChange = {
                                textSendAvailable = it.length > 0

                                if (it.length < 30) {
                                    usernameTextSaver.text = it
                                }


                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            maxLines = 2,
                            singleLine = true
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            SendButton(onEvent = {
                if(textSendAvailable){
                if (usernameTextSaver.text.isNotEmpty()) {
                    userViewModel?.getUserByUsername(usernameTextSaver.text.trim())
                }
                }

            }, icon = R.drawable.ic_search, available =textSendAvailable )
        }
        userFlow?.value.let {
            when(it){
                is Response.Success->  {
                    onEvent(SearchEvent.GoToUserProfile(it.data))
                    userViewModel?.resetUserValue()
                }
                is Response.Failure->{
                    Toast.makeText(LocalContext.current,
                        "Failed to find user with given username",Toast.LENGTH_LONG).show()

                }

            }
        }

    }

}


