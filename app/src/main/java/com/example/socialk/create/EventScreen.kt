package com.example.socialk.create

import android.content.res.Configuration
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialk.Create
import com.example.socialk.Destinations
import com.example.socialk.R
import com.example.socialk.bottomTabRowScreens
import com.example.socialk.components.BottomBar
import com.example.socialk.components.BottomBarRow
import com.example.socialk.home.cardHighlited
import com.example.socialk.home.cardnotHighlited
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme

sealed class EventEvent{
    object GoToProfile : EventEvent()
    object LogOut : EventEvent()
    object GoToSettings : EventEvent()
    object GoToHome : EventEvent()
    object GoToLive : EventEvent()
    object GoToEvent : EventEvent()
    object GoToActivity: EventEvent()
}
@Composable
fun EventScreen (onEvent: (EventEvent) -> Unit, bottomNavEvent:(Destinations)->Unit){
    Surface(modifier = Modifier
        .fillMaxSize()
        .background(SocialTheme.colors.uiBackground),color= SocialTheme.colors.uiBackground
    ) {

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
                .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(12.dp))
            // TOP SECTION PICKER
            activityPickerEvent(isSystemInDarkTheme(), onEvent = { event -> onEvent(event) })

            Spacer(modifier = Modifier.height(12.dp))
            //TEXT FIELD
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
            //DATE FIELD
            //TODO HARDCODED DATE, implement onclick
            createField(column = false, action = {
                ClickableText(text = AnnotatedString("06/01/22"), style = TextStyle(
                    fontSize = 18.sp,
                    color = Color(0xFF494949),
                    fontFamily = Inter,
                    fontWeight = FontWeight.SemiBold,
                ), onClick = {})
            }, title = "Date", icon = R.drawable.ic_calendar)
            //DATE END FIELD
            createField(
                column = false,
                action = {
                    //TODO HARDCODED  TIME length, implement onclick,hardoced color
                    ClickableText(text = AnnotatedString("09/01/2024"), style = TextStyle(
                        color = Color(0xFF494949),
                        fontSize = 18.sp,
                        fontFamily = Inter,
                        fontWeight = FontWeight.SemiBold,
                    ), onClick = {})
                },
                title = "Date end",
                icon = R.drawable.ic_date_end
            )
            //TIME FIELD
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

            Spacer(modifier = Modifier.height(48.dp))
            CreateActivityButton(onClick = {})
            Spacer(modifier = Modifier.height(64.dp))
        }


        Box(modifier = Modifier
            .fillMaxWidth()
            .heightIn(56.dp)
            .padding(vertical = 16.dp), contentAlignment = Alignment.TopEnd){


        }

        BottomBar( onTabSelected = { screen->bottomNavEvent(screen)},currentScreen = Create)

    }
}
@Composable
fun activityPickerEvent(isDark:Boolean,modifier: Modifier = Modifier, onEvent: (EventEvent) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 8.dp), horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {


        cardnotHighlited(text = "Live", onEvent = { onEvent(EventEvent.GoToLive) })
        Spacer(Modifier.width(6.dp))
        cardnotHighlited(text = "Activity", onEvent = { onEvent(EventEvent.GoToActivity) })
        Spacer(Modifier.width(6.dp))
        cardHighlited(text = "Event", isDark = isDark)
    }
}

@Preview(showBackground = true)
@Composable
fun previewEventScreen () {
    SocialTheme{
        EventScreen(onEvent = {}, bottomNavEvent = {})
    }
}
@Preview(showBackground = true,uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun previewEventScreenDark(){
    SocialTheme{
        EventScreen(onEvent = {}, bottomNavEvent = {})
    }
}
