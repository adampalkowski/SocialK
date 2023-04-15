package com.example.socialk.components

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.platform.LocalClipboardManager
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
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.socialk.ActivitySettingsContent
import com.example.socialk.ActivitySettingsEvent
import com.example.socialk.R
import com.example.socialk.chat.ChatComponents.ChatButton
import com.example.socialk.chat.checkIfToday
import com.example.socialk.di.UserRepositoryImpl
import com.example.socialk.model.Activity
import com.example.socialk.model.UserData
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme
import com.example.socialk.ui.theme.Typography
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed class ActivityItemEvent {
    class LikedActivity(activity: Activity) : ActivityItemEvent() {
        val activity = activity
    }
    class SendRequest(val activity: Activity) : ActivityItemEvent()

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

sealed class ActivityEvent() {
    class OpenActivitySettings(activity: Activity) : ActivityEvent() {
        val activity = activity
    }
    class SendRequest(val activity: Activity) : ActivityEvent()

    class GoToProfile(user_id: String) : ActivityEvent() {
        val user_id = user_id
    }

    class DisplayPicture(val photo_url: String, val activity_id: String) : ActivityEvent()
    class OpenActivityChat(activity: Activity) : ActivityEvent() {
        val activity = activity
    }

    class ActivityLiked(activity: Activity) : ActivityEvent() {
        val activity = activity
    }

    class ActivityUnLiked(activity: Activity) : ActivityEvent() {
        val activity = activity
    }

    class GoToMap(latlng: String) : ActivityEvent() {
        val latlng = latlng
    }

    class OpenCamera(val activity_id: String) : ActivityEvent()
    class ReportActivity(val activity_id: String) : ActivityEvent()
    class HideActivity(val activity_id: String,val user_id: String) : ActivityEvent()
    class DisplayParticipants(val activity: Activity) : ActivityEvent()
    class GoToFriendsPicker(val activity: Activity) : ActivityEvent()
    class LeaveActivity(val activity: Activity) : ActivityEvent()
}


@OptIn(ExperimentalAnimationApi::class)
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
    onEvent: (ActivityEvent) -> Unit,
                 onLongClick:()->Unit= {},
) {
    var liked = rememberSaveable { mutableStateOf(liked) }
    val displayParticipants = rememberSaveable { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current

    var openSettings by rememberSaveable { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .pointerInput(Unit) { detectTapGestures(onLongPress = { onLongClick() }) }

            .padding(vertical = 4.dp)

    ) {
        Column() {
            Card(modifier= Modifier
                .background(SocialTheme.colors.uiFloated)
                .padding(bottom = if (openSettings) 8.dp else 0.dp)
                ,shape= RoundedCornerShape(bottomEnd = if(openSettings) 0.dp else 0.dp,bottomStart = if(openSettings) 0.dp else 0.dp),
                elevation = if (openSettings) 4.dp else 0.dp) {
                Column(
                    Modifier
                        .background(color = SocialTheme.colors.uiBackground)
                        .padding(horizontal = 12.dp)){
                    TimeDivider(activity.start_time)
                    Spacer(modifier = Modifier.height(8.dp))

                    //ACtivity top content
                    ActivityItemCreatorBox(onClick = {onEvent(ActivityEvent.GoToProfile(activity.creator_id))},pictureUrl=activity.creator_profile_picture,username=activity.creator_username,timeLeft=activity.time_left,onSettingsClick={
                        openSettings= !openSettings
                        onEvent(ActivityEvent.OpenActivitySettings(activity))})

                    Spacer(modifier = Modifier.height(12.dp))
                    //TEXT AND CONTROLS ROW
                    Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                        ActivityTextBox(modifier=Modifier.weight(1f),title,description)

                        controls(onEvent = { event ->
                            when (event) {
                                is ActivityItemEvent.SendRequest -> {
                                    Log.d("Mapfragment","open  event")
                                    onEvent(ActivityEvent.SendRequest(event.activity))
                                }
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
                        }, activity, liked.value)
                    }

                    //DETAILS
                    Spacer(modifier = Modifier.height(4.dp))
                    //todo either custom location or latlng
                    ActivityDetailsBar(min=activity.minUserCount,max=activity.maxUserCount,
                        onEvent = onEvent, activity.participants_profile_pictures,activity.participants_ids,
                        location = location,
                        custom_location = custom_location,
                        date = date,
                        timePeriod = timePeriod
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                }

            }


            AnimatedVisibility(visible = openSettings,enter= scaleIn(),exit= scaleOut()) {

                    ActivitySettingsContent(LocalContext.current,clipboardManager,displayParticipants,leaveActivity = {liked.value=false}, onEvent =onEvent
                    ,closeSettings={  openSettings=false}, activity =activity,likedActivity=liked.value)
            }



        }
    }
}

@Composable
fun TimeDivider(startTime: String) {
    Row(verticalAlignment = CenterVertically, modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 32.dp)) {
        Card(shape = RoundedCornerShape(100.dp), modifier = Modifier
            .height(1.dp)
            .weight(1f)
            .background(color = SocialTheme.colors.uiFloated), backgroundColor = SocialTheme.colors.uiFloated, elevation = 0.dp) {
        }
        Text(text =startTime, color = SocialTheme.colors.iconPrimary, style = TextStyle(fontFamily = Inter, fontSize = 10.sp, fontWeight = FontWeight.Light))
        Card(shape = RoundedCornerShape(100.dp), modifier = Modifier
            .height(1.dp)
            .weight(1f)
            .background(color = SocialTheme.colors.uiFloated), backgroundColor = SocialTheme.colors.uiFloated, elevation = 0.dp) {
        }
    }
}

@Composable
fun ActivityTextBox(modifier: Modifier,title: String, description: String) {
    Column(
        modifier = modifier
            .padding(end = 8.dp), verticalArrangement = Arrangement.Top, horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            style = com.example.socialk.ui.theme.Typography.h3,
            fontWeight = FontWeight.Normal,
            color = SocialTheme.colors.textPrimary,
            textAlign = TextAlign.Left
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = description,
            style = com.example.socialk.ui.theme.Typography.h5,
            fontWeight = FontWeight.Light,
            color =  SocialTheme.colors.textPrimary,
            textAlign = TextAlign.Left
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ActivityItemCreatorBox(onClick:()->Unit,pictureUrl:String,username:String,timeLeft: String,onSettingsClick:()->Unit) {
    Row(
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap ={ onClick() }) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            model = pictureUrl,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable(onClick = onClick),
                    contentScale = ContentScale.Crop,
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
                color =  SocialTheme.colors.textPrimary
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
    onEvent: (ActivityEvent) -> Unit, participants_pictures: HashMap<String, String>,participants_ids:ArrayList<String>,
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
                if (participants_ids.size > 4) {
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
                                text = "+"+(participants_ids.size-4).toString(),
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
                Spacer(modifier = Modifier.width(24.dp))
            }
            if (min>0 || max >0)
            {
                item{
                    Icon(painterResource(id = R.drawable.ic_check_list_16),tint=SocialTheme.colors.iconPrimary, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    if(min>0){
                        Text(text="Min ${min.toString()}",style=TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 10.sp,color=SocialTheme.colors.textPrimary))
                    }
                    if(min>0 && max>0){
                        Spacer(modifier = Modifier.width(12.dp))

                    }
                    if(max>0){
                        Text(text="Max ${max.toString()}",style=TextStyle(fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 10.sp,color=SocialTheme.colors.textPrimary))

                    }
                    Spacer(modifier = Modifier.width(24.dp))
                }
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
fun controls(onEvent: (ActivityItemEvent) -> Unit, activity: Activity, liked: Boolean) {
    Column(
        modifier = Modifier
    ) {
        if (liked) {

        } else {
            if(activity.participants_ids.size<activity.maxUserCount){
                if(activity.awaitConfirmation){

                    ChatButton(
                        onEvent = {
                            onEvent(ActivityItemEvent.SendRequest(activity))

                        }, icon =
                        R.drawable.ic_loyalty
                        , iconTint =
                        SocialTheme.colors.iconPrimary
                    )
                }else{


                    ChatButton(
                        onEvent = {
                            onEvent(ActivityItemEvent.NotLikedActivity(activity))
                        }, icon =
                        R.drawable.ic_add_task
                        , iconTint =
                        SocialTheme.colors.iconPrimary
                    )
                }
            }


        }
        if(!activity.disableChat){
            if(activity.privateChat){
                if(activity.participants_usernames.containsKey(UserData.user!!.id)) {
                    Spacer(modifier = Modifier.height(6.dp))
                    ChatButton(
                        onEvent = { onEvent(ActivityItemEvent.OpenActivityChat(activity)) },
                        icon = R.drawable.ic_chat
                    )
                }
            }else{
                Spacer(modifier = Modifier.height(6.dp))
                ChatButton(
                    onEvent = { onEvent(ActivityItemEvent.OpenActivityChat(activity)) },
                    icon = R.drawable.ic_chat
                )
            }


        }

        Spacer(modifier = Modifier.height(6.dp))
       /* if (activity.pictures.containsKey(UserData.user!!.id)){
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

            if(!activity.disablePictures){
                ChatButton(
                    onEvent = {
                        if(lockPhotoButton){

                        }else{
                            onEvent(ActivityItemEvent.OpenCamera(activity.id))
                        }

                    },
                    icon = R.drawable.ic_add_photo)
            }else{

            }

        }
*/
    }

}
