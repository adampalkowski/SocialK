package com.example.socialk.create

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Space
import androidx.compose.foundation.*
import com.example.socialk.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialk.Create
import com.example.socialk.Destinations
import com.example.socialk.bottomTabRowScreens
import com.example.socialk.components.BottomBar
import com.example.socialk.components.BottomBarRow
import com.example.socialk.home.HomeEvent
import com.example.socialk.home.cardHighlited
import com.example.socialk.home.cardnotHighlited
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme

sealed class CreateEvent {
    object GoToProfile : CreateEvent()
    object LogOut : CreateEvent()
    object GoToSettings : CreateEvent()
    object GoToHome : CreateEvent()
    object GoToLive : CreateEvent()
    object GoToEvent : CreateEvent()
    object GoToActivity : CreateEvent()
}

@Composable
fun CreateScreen(onEvent: (CreateEvent) -> Unit, bottomNavEvent: (Destinations) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(SocialTheme.colors.uiBackground), color = SocialTheme.colors.uiBackground
    ) {

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            activityPickerCreate(isSystemInDarkTheme(), onEvent = { event -> onEvent(event) })
            Spacer(modifier = Modifier.height(12.dp))
            createField(column = true, action = {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(12.dp),
                    backgroundColor = SocialTheme.colors.uiBackground,
                    border = BorderStroke(1.dp, color = Color(0xffD3D3D3))
                ) {
                    Box(modifier = Modifier) {
                        //TODO TEXT FIELDS SHOULD BE DONE THE SAME WAY AS IT IS IN THE LOGIN SECTION
                        TextField(
                            textStyle = TextStyle(fontSize = 14.sp),
                            value = "",
                            onValueChange = {},
                            placeholder = {
                                Text(
                                    color = Color(0xff757575),
                                    text = "What are you planning?"
                                )
                            },
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            )
                        )
                    }
                }


            }, title = "Text", icon = R.drawable.ic_edit)
            //TODO HARDCODED DATE, implement onclick
            createField(column = false, action = {
                ClickableText(text = AnnotatedString("06/01/22"), style = TextStyle(
                    fontSize = 18.sp,
                    color = Color(0xFF494949),
                    fontFamily = Inter,
                    fontWeight = FontWeight.SemiBold,
                ), onClick = {})
            }, title = "Date", icon = R.drawable.ic_calendar)
            createField(

                column = false,
                action = {
                    //TODO HARDCODED START TIME , implement onclick
                    ClickableText(text = AnnotatedString("10:48"), style = TextStyle(
                        fontSize = 18.sp,
                        color = Color(0xFF494949),
                        fontFamily = Inter,
                        fontWeight = FontWeight.SemiBold,
                    ), onClick = {})
                },
                title = "Start time",
                icon = R.drawable.ic_schedule
            )
            createField(
                column = false,
                action = {
                    //TODO HARDCODED  TIME length, implement onclick,hardoced color
                    ClickableText(text = AnnotatedString("1 hour"), style = TextStyle(
                        color = Color(0xFF494949),
                        fontSize = 18.sp,
                        fontFamily = Inter,
                        fontWeight = FontWeight.SemiBold,
                    ), onClick = {})
                },
                title = "Time length",
                icon = R.drawable.ic_hourglass
            )
            Spacer(modifier = Modifier.height(48.dp))
            CreateActivityButton(onClick = {})
            Spacer(modifier = Modifier.height(64.dp))
        }


        BottomBar(
            onTabSelected = { screen -> bottomNavEvent(screen) },
            currentScreen = Create
        )

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CreateActivityButton(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .height(56.dp)
            .width(300.dp),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color(0xFF494949),
        onClick = { onClick }
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Create activity",
                style = TextStyle(
                    color = SocialTheme.colors.textSecondary, fontSize = 18.sp,
                    fontFamily = Inter, fontWeight = FontWeight.Bold
                )
            )
        }
    }

}

@Preview
@Composable
fun previewFiendPicker() {
    friendPicker()
}

//todo FINISH THE FRIEND PICKER WITH SOME PICTURES
@Composable
fun friendPicker() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = "Friends",
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold
            )
        )

    }
}

@Composable
fun createField(action: @Composable () -> Unit, title: String, icon: Int, column: Boolean) {
    Box(
        modifier = Modifier
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (column) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 24.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        tint = SocialTheme.colors.iconSecondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        fontFamily = Inter,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = SocialTheme.colors.iconSecondary
                    )
                    Spacer(modifier = Modifier.weight(1f))

                }
                Spacer(modifier = Modifier.height(12.dp))
                action()
                Spacer(modifier = Modifier.height(12.dp))
                Spacer(
                    modifier = Modifier
                        .width(300.dp)
                        .height(1.dp)
                        .background(color = Color(0xFFE0E0E0))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(color = Color(0xFFE0E0E0))
                )
            } else {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        tint = SocialTheme.colors.iconSecondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        fontFamily = Inter,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = SocialTheme.colors.iconSecondary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    action()
                }

                Box(
                    modifier = Modifier
                        .width(300.dp)
                        .height(1.dp)
                        .background(color = Color(0xFFE0E0E0))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(color = Color(0xFFE0E0E0))
                )
            }

        }

    }
}

@Preview(showBackground = true)
@Composable
fun previewCreateField() {
    SocialTheme {
        createField(action = { textF() }, title = "Time", icon = R.drawable.ic_timer, column = true)

    }
}

@Composable
fun textF() {
    TextField(value = "s", onValueChange = {})
}


@Composable
fun activityPickerCreate(
    isDark: Boolean,
    modifier: Modifier = Modifier,
    onEvent: (CreateEvent) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 8.dp), horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        cardnotHighlited(text = "Live", onEvent = { onEvent(CreateEvent.GoToLive) })
        Spacer(Modifier.width(6.dp))
        cardHighlited(text = "Activities", isDark = isDark)
        Spacer(Modifier.width(6.dp))
        cardnotHighlited(text = "Event", onEvent = { onEvent(CreateEvent.GoToEvent) })
    }
}


@Preview(showBackground = true)
@Composable
fun previewCreateScreen() {
    SocialTheme {
        CreateScreen(onEvent = {}, bottomNavEvent = {})

    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun previewCreateScreenDark() {
    SocialTheme {
        CreateScreen(onEvent = {}, bottomNavEvent = {})

    }
}