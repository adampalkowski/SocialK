package com.example.socialk.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.socialk.R
import com.example.socialk.chat.ChatButton
import com.example.socialk.components.timepicker.SheetState
import com.example.socialk.home.ActivityEvent
import com.example.socialk.home.HomeEvent
import com.example.socialk.model.Activity
import com.example.socialk.ui.theme.*
import kotlin.text.Typography

sealed class ActivityItemEvent{
    class LikedActivity(activity: Activity) : ActivityItemEvent(){val activity=activity}
    class NotLikedActivity (activity: Activity): ActivityItemEvent(){val activity=activity}
    class OpenActivityChat(activity: Activity): ActivityItemEvent(){val activity=activity}
}

@Composable
fun ActivityItem(
    activity: Activity,
    username: String,
    profilePictureUrl: String,
    timeLeft: String,
    title: String,
    description: String,
    date: String,
    timePeriod: String,
    custom_location: String,
    liked: Boolean,
    onEvent: (ActivityEvent) -> Unit
) {


    var liked = rememberSaveable { mutableStateOf(liked) }
    Box(
        modifier = Modifier
            .padding(start = 12.dp)
            .padding(end = 12.dp)
            .padding(vertical = 12.dp)
    ) {
        Column() {
            //ACtivity top content
            Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberAsyncImagePainter(profilePictureUrl),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier) {
                    Text(
                        text = username,
                        style = com.example.socialk.ui.theme.Typography.h5,
                        fontWeight = FontWeight.Light,
                        color = SocialTheme.colors.textPrimary
                    )
                    Text(
                        text = timeLeft,
                        style = com.example.socialk.ui.theme.Typography.subtitle1,
                        textAlign = TextAlign.Center,
                        color = SocialTheme.colors.textPrimary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { onEvent(ActivityEvent.OpenActivitySettings(activity)) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_more),
                        contentDescription = null,
                        tint = SocialTheme.colors.iconPrimary
                    )

                }
                Spacer(modifier = Modifier.width(12.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            //TEXT AND CONTROLS ROW
            Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp), verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = title,
                        style = com.example.socialk.ui.theme.Typography.h3,
                        fontWeight = FontWeight.Normal,
                        color = SocialTheme.colors.textPrimary,
                        textAlign = TextAlign.Left
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = description,
                        style = com.example.socialk.ui.theme.Typography.h5,
                        fontWeight = FontWeight.Light,
                        color = SocialTheme.colors.iconPrimary,
                        textAlign = TextAlign.Left
                    )
                }
                controls(onEvent = {event-> when(event){
                    is ActivityItemEvent.LikedActivity->{liked.value=false
                        onEvent(ActivityEvent.ActivityUnLiked(event.activity))
                    }
                    is ActivityItemEvent.NotLikedActivity->{liked.value=true
                        onEvent(ActivityEvent.ActivityLiked(event.activity))
                    }
                    is ActivityItemEvent.OpenActivityChat->{onEvent(ActivityEvent.OpenActivityChat(event.activity))}
                } }, activity, liked.value)
            }

            //DETAILS
            Spacer(modifier = Modifier.height(12.dp))
            //todo either custom location or latlng
            ActivityDetailsBar(location = custom_location, date = date, timePeriod = timePeriod)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(1.dp)
                    .background(color = SocialTheme.colors.uiFloated)
            )


        }
    }
}


@Composable
fun ActivityDetailsBar(location: String, date: String, timePeriod: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(color = Color.Transparent)
    ) {

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .align(CenterStart), verticalAlignment = CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_location),
                contentDescription = null,
                tint = SocialTheme.colors.iconPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = location,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.ExtraLight,
                    fontSize = 12.sp
                ),
                color = SocialTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.width(24.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_date),
                contentDescription = null,
                tint = SocialTheme.colors.iconPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = date,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.ExtraLight,
                    fontSize = 12.sp
                ),
                color = SocialTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.width(24.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_timer),
                contentDescription = null,
                tint = SocialTheme.colors.iconPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = timePeriod,
                style = TextStyle(
                    fontFamily = Inter,
                    fontWeight = FontWeight.ExtraLight,
                    fontSize = 12.sp
                ),
                color = SocialTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.width(24.dp))
        }
    }

}


@Composable
fun controls(onEvent: (ActivityItemEvent) -> Unit, activity: Activity, liked: Boolean) {
    Column(
        modifier = Modifier
    ) {
        ChatButton(
            onEvent = {
                if (liked) {
                    onEvent(ActivityItemEvent.LikedActivity(activity))
                } else {
                    onEvent(ActivityItemEvent.NotLikedActivity(activity))

                }
            }, icon = if (liked) {
                R.drawable.ic_heart_red
            } else {
                R.drawable.ic_heart
            }, iconTint = if (liked) {
                Color.Red
            } else {
                SocialTheme.colors.iconPrimary
            }
        )
        Spacer(modifier = Modifier.height(6.dp))
        ChatButton(
            onEvent = { onEvent(ActivityItemEvent.OpenActivityChat(activity)) },
            icon = R.drawable.ic_chat
        )
        Spacer(modifier = Modifier.height(6.dp))
        ChatButton(onEvent = { /*TODO*/ }, icon = R.drawable.ic_bookmark)
    }

}
