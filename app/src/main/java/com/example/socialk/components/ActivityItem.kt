package com.example.socialk.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.socialk.R
import com.example.socialk.chat.ChatComponents.ChatButton
import com.example.socialk.chat.checkIfToday
import com.example.socialk.di.UserRepositoryImpl
import com.example.socialk.home.ActivityEvent
import com.example.socialk.model.Activity
import com.example.socialk.model.UserData
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme
import com.example.socialk.ui.theme.Typography
import java.time.LocalDate

sealed class ActivityItemEvent {
    class LikedActivity(activity: Activity) : ActivityItemEvent() {
        val activity = activity
    }

    class NotLikedActivity(activity: Activity) : ActivityItemEvent() {
        val activity = activity
    }
    class DisplayPicture(val photo_url: String,val activity_id: String) : ActivityItemEvent()
    class OpenActivityChat(activity: Activity) : ActivityItemEvent() {
        val activity = activity
    }
    class OpenCamera(val activity_id:String): ActivityItemEvent()
}
/*
@OptIn(ExperimentalMaterialApi::class)
@Composable
 fun SwipableActivity( activity: Activity,
                                username: String,
                                profilePictureUrl: String,
                                timeLeft: String,
                                title: String,
                                description: String,
                                date: String,
                                timePeriod: String,
                                custom_location: String,
                                location: String,
                                liked: Boolean,
                                onEvent: (ActivityEvent) -> Unit) {
    val squareSize = 129.dp
    val swipeableState = rememberSwipeableState(0)
    val sizePx = with(LocalDensity.current) { squareSize.toPx() }
    val anchors = mapOf(0f to 0, sizePx to 1,
        -sizePx to -1
    )// Maps anchor points (in px) to states

    Box(
        modifier = Modifier
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(1f) },
                orientation = Orientation.Horizontal,
            )
            .background(Color.LightGray)
    ) {
        ActivityItem(modifier=   Modifier.background(color=SocialTheme.colors.uiBackground)
            .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) },
            activity = activity,
            username =username ,
            profilePictureUrl =profilePictureUrl ,
            timeLeft = timeLeft,
            title = title,
            description =description ,
            date = date,
            timePeriod =timePeriod ,
            custom_location = custom_location,
            location = location,
            liked =liked ,
            onEvent =onEvent
        )

    }
    swipeableState

}
*/



@Composable
fun ActivityItem(modifier:Modifier=Modifier,
    activity: Activity,
    username: String,
    profilePictureUrl: String,
    timeLeft: String,
    title: String,
    description: String,
    date: String,
    timePeriod: String,
    custom_location: String,
    location: String,
    liked: Boolean,
    onEvent: (ActivityEvent) -> Unit,lockPhotoButton:Boolean=false,
                 onLongClick:()->Unit= {}
) {
    var liked = rememberSaveable { mutableStateOf(liked) }
    Box(
        modifier = Modifier.pointerInput(Unit){detectTapGestures(onLongPress = {onLongClick()})}
            .padding(start = 12.dp)
            .padding(end = 12.dp)
            .padding(vertical = 12.dp)

    ) {
        Column() {
            //ACtivity top content
            ActivityItemCreatorBox(onClick = {onEvent(ActivityEvent.GoToProfile(activity.creator_id))},pictureUrl=activity.creator_profile_picture,username=activity.creator_username,timeLeft=activity.time_left,onSettingsClick={ onEvent(ActivityEvent.OpenActivitySettings(activity))})

            Spacer(modifier = Modifier.height(12.dp))
            //TEXT AND CONTROLS ROW
            Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                ActivityTextBox(modifier=Modifier.weight(1f),title,description)

                controls(onEvent = { event ->
                    when (event) {
                        is ActivityItemEvent.NotLikedActivity -> {
                            liked.value = true
                            onEvent(ActivityEvent.ActivityLiked(event.activity))
                        }
                        is ActivityItemEvent.OpenActivityChat -> {
                            onEvent(ActivityEvent.OpenActivityChat(event.activity))
                        }
                        is ActivityItemEvent.OpenCamera -> {
                            Log.d("activityRepositoryImpl","open camera event")
                            onEvent(ActivityEvent.OpenCamera(event.activity_id))
                        }
                        is ActivityItemEvent.DisplayPicture -> {
                            onEvent(ActivityEvent.DisplayPicture(photo_url = event.photo_url,event.activity_id))
                        }
                        else->{}
                    }
                }, activity, liked.value,lockPhotoButton)
            }

            //DETAILS
            Spacer(modifier = Modifier.height(12.dp))
            //todo either custom location or latlng
            ActivityDetailsBar(min=activity.minUserCount,max=activity.maxUserCount,
                onEvent = onEvent, activity.participants_profile_pictures,
                location = location,
                custom_location = custom_location,
                date = date,
                timePeriod = timePeriod
            )
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
fun ActivityTextBox(modifier: Modifier,title: String, description: String) {
    Column(
        modifier = modifier
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
}

@Composable
fun ActivityItemCreatorBox(onClick:()->Unit,pictureUrl:String,username:String,timeLeft: String,onSettingsClick:()->Unit) {
    Row(
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap ={ onClick() }) },
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(pictureUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_person),
            contentDescription = "image sent",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
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
                text ="Starts in "+timeLeft,
                style = com.example.socialk.ui.theme.Typography.subtitle1,
                textAlign = TextAlign.Center,
                color = SocialTheme.colors.textPrimary
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { onSettingsClick()}) {
            Icon(
                painter = painterResource(id = R.drawable.ic_more),
                contentDescription = null,
                tint = SocialTheme.colors.iconPrimary
            )

        }
        Spacer(modifier = Modifier.width(12.dp))
    }
}


@Composable
fun ActivityDetailsBar(min:Int,max:Int,
    onEvent: (ActivityEvent) -> Unit, participants_pictures: HashMap<String, String>,
    location: String?,
    custom_location: String?,
    date: String,
    timePeriod: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(color = Color.Transparent)
    ) {

        LazyRow(
            modifier = Modifier
                .align(CenterStart),
            verticalAlignment = CenterVertically,

        ) {
            item {
                Row(      horizontalArrangement = Arrangement.spacedBy((-10).dp)) {
                    participants_pictures.values.toList().take(4).reversed().forEachIndexed { index, it ->
                        AsyncImage(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .zIndex(participants_pictures.values.toList().size - index.toFloat()),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(it)
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(R.drawable.ic_person),
                            contentDescription = "participant picture",
                            contentScale = ContentScale.Crop,

                            )}
                }
                if (participants_pictures.values.toList().size > 4) {
                    Card(modifier = Modifier.size(32.dp), shape = CircleShape) {
                        AsyncImage(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(participants_pictures.values.toList().get(4))
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(R.drawable.ic_person),
                            contentDescription = "participant picture",
                            contentScale = ContentScale.Crop,
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f))
                        ) {
                            // Content
                            Text(
                                modifier = Modifier.align(Center),
                                textAlign = TextAlign.Center,
                                text = "+"+(participants_pictures.values.toList().size-4).toString(),
                                style = TextStyle(
                                    fontFamily = Inter,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Normal
                                ),
                                color = SocialTheme.colors.textSecondary,

                                )
                        }

                    }
                }



            }


            item {
                Spacer(modifier = Modifier.width(32.dp))
            }
            item {
            //two cases
                // one custom location exists then write text
                // two lat lng location exists then display button to preview location

                if (custom_location == null || custom_location.isEmpty()) {
                    if (location == null || location.isEmpty()) {

                    } else {
                        //latlng location
                        Icon(
                            painter = painterResource(id = R.drawable.ic_location),
                            contentDescription = "location icon",
                            tint = SocialTheme.colors.iconPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        ClickableText(
                            text = AnnotatedString("See map"),
                            onClick = {

                                onEvent(ActivityEvent.GoToMap(location))
                            },
                            style = TextStyle(
                                fontFamily = Inter,
                                fontWeight = FontWeight.ExtraLight,
                                fontSize = 12.sp, color = SocialTheme.colors.textPrimary
                            ),
                        )
                        Spacer(modifier = Modifier.width(24.dp))

                    }

                } else {
                    //custom location 
                    Icon(
                        painter = painterResource(id = R.drawable.ic_location),
                        contentDescription = "custom location icon",
                        tint = SocialTheme.colors.iconPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = custom_location,
                        style = Typography.h6,
                        color = SocialTheme.colors.textPrimary
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                }






                Icon(
                    painter = painterResource(id = R.drawable.ic_date),
                    contentDescription = null,
                    tint = SocialTheme.colors.iconPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                val dateORTODAY = if(checkIfToday(date)){"Today"}else{date}
                Text(
                    text = dateORTODAY,
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

}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun controls(onEvent: (ActivityItemEvent) -> Unit, activity: Activity, liked: Boolean,lockPhotoButton: Boolean) {
    Column(
        modifier = Modifier
    ) {
        if (liked) {

        } else {
            ChatButton(
                onEvent = {
                        onEvent(ActivityItemEvent.NotLikedActivity(activity))

                }, icon =
                    R.drawable.ic_add_task
               , iconTint =
                    SocialTheme.colors.iconPrimary
            )

        }

        Spacer(modifier = Modifier.height(6.dp))
        ChatButton(
            onEvent = { onEvent(ActivityItemEvent.OpenActivityChat(activity)) },
            icon = R.drawable.ic_chat
        )
        Spacer(modifier = Modifier.height(6.dp))
        if (activity.pictures.containsKey(UserData.user!!.id)){
                val photo_url=activity.pictures.get(UserData.user!!.id)
            Card(onClick = {onEvent(ActivityItemEvent.DisplayPicture(photo_url!!,activity.id))}) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photo_url)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.ic_add_photo),
                    contentDescription = "image sent",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp).background(color=SocialTheme.colors.uiBackground)
                )
            }
        }else{

            ChatButton(
                onEvent = {
                    if(lockPhotoButton){

                    }else{
                        onEvent(ActivityItemEvent.OpenCamera(activity.id))
                    }

               },
                icon = R.drawable.ic_add_photo)
        }



    }

}
