package com.example.socialk.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
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
import com.example.socialk.home.ActivityEvent
import com.example.socialk.home.HomeEvent
import com.example.socialk.model.Activity
import com.example.socialk.ui.theme.*
import kotlin.text.Typography

@Composable
fun ActivityItem(activity: Activity,
                 username: String,
                 profilePictureUrl: String,
                 timeLeft: String,
                 title: String,
                 description: String,
                 date: String,
                 timePeriod: String,
                 location: String,
                 onEvent:(ActivityEvent)->Unit
) {
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
                        style = com.example.socialk.ui.theme.Typography.h5, fontWeight = FontWeight.Light,color= SocialTheme.colors.textPrimary
                    )
                    Text(
                        text = timeLeft, style = com.example.socialk.ui.theme.Typography.subtitle1, textAlign = TextAlign.Center,color= SocialTheme.colors.textPrimary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = {onEvent(ActivityEvent.OpenActivitySettings(activity))}) {
                    Icon(painter = painterResource(id = R.drawable.ic_more), contentDescription = null,tint =SocialTheme.colors.iconPrimary)

                }
                Spacer(modifier = Modifier.width(12.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            //TEXT AND CONTROLS ROW
            Row (modifier = Modifier, verticalAlignment = Alignment.CenterVertically){
                Column(modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp), verticalArrangement = Arrangement.Top) {
                    Text( text = title, style = com.example.socialk.ui.theme.Typography.h3, fontWeight = FontWeight.Normal
                        ,color=SocialTheme.colors.textPrimary, textAlign = TextAlign.Left
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = description, style = com.example.socialk.ui.theme.Typography.h5,fontWeight = FontWeight.Light
                        , color = SocialTheme.colors.iconPrimary,
                        textAlign = TextAlign.Left
                    )
                }
                controls(onEvent=onEvent,activity)
            }

            //DETAILS
            Spacer(modifier = Modifier.height(12.dp))
            ActivityDetailsBar(location="",date=date,timePeriod=timePeriod)
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(1.dp)
                .background(color = SocialTheme.colors.uiFloated))


        }
    }
}

@Composable
fun ActivityDetailsBar(location:String,date:String,timePeriod:String) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(color = Color.Transparent) ){

            Row (modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .align(CenterStart)
, verticalAlignment = CenterVertically){
                Icon(painter = painterResource(id = R.drawable.ic_location), contentDescription = null, tint =SocialTheme.colors.iconPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text =location , style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.ExtraLight, fontSize = 12.sp)
                    , color = SocialTheme.colors.textPrimary )
                Spacer(modifier = Modifier.width(24.dp))
                Icon(painter = painterResource(id = R.drawable.ic_date), contentDescription = null,tint =SocialTheme.colors.iconPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text =date, style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.ExtraLight, fontSize = 12.sp)
                    , color = SocialTheme.colors.textPrimary)
                Spacer(modifier = Modifier.width(24.dp))
                Icon(painter = painterResource(id = R.drawable.ic_timer), contentDescription = null, tint =SocialTheme.colors.iconPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text =timePeriod, style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.ExtraLight, fontSize = 12.sp)
                    , color = SocialTheme.colors.textPrimary )
                Spacer(modifier = Modifier.width(24.dp))
            }
        }

}


@Composable
fun controls(onEvent: (ActivityEvent) -> Unit,activity: Activity){
    Column(      modifier = Modifier
        ) {
        ChatButton(onEvent = { /*TODO*/ }, icon =R.drawable.ic_heart )
        Spacer(modifier = Modifier.height(6.dp))
        ChatButton(onEvent = { onEvent(ActivityEvent.OpenActivityChat(activity)) }, icon =R.drawable.ic_chat )
        Spacer(modifier = Modifier.height(6.dp))
        ChatButton(onEvent = { /*TODO*/ }, icon =R.drawable.ic_bookmark )
    }

}
