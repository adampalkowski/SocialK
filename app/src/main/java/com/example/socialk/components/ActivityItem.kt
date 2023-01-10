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
import com.example.socialk.R
import com.example.socialk.ui.theme.*

@Composable
fun ActivityItem(
    username: String,
    profilePictureUrl: String,
    timeLeft: String,
    title: String,
    description: String,
    date: String,
    timePeriod: String,
    location: String
) {
    Box(
        modifier = Modifier
            .padding(start = 12.dp)
            .padding(end = 12.dp)
            .padding(vertical = 12.dp)
    ) {
        Column() {

            Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier) {
                    Text(
                        text = username,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = Inter,
                            fontWeight = FontWeight.Light
                        ),color= SocialTheme.colors.textPrimary
                    )
                    Text(
                        text = timeLeft, style = TextStyle(
                            fontFamily = Inter,
                            fontWeight = FontWeight.ExtraLight, fontSize = 10.sp
                        ), textAlign = TextAlign.Center,color= SocialTheme.colors.textPrimary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(painter = painterResource(id = R.drawable.ic_more), contentDescription = null,tint =SocialTheme.colors.iconPrimary)
                Spacer(modifier = Modifier.width(12.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row (modifier = Modifier, verticalAlignment = Alignment.CenterVertically){
                Column(modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)) {
                    Text(
                        modifier = Modifier, text = title, style = TextStyle(
                            fontSize = 18.sp, fontFamily = Inter, fontWeight = FontWeight.Normal
                        ),color=SocialTheme.colors.textPrimary, textAlign = TextAlign.Left
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = description, style = TextStyle(
                            fontSize = 14.sp, fontFamily = Inter, fontWeight = FontWeight.Light
                        ), color = SocialTheme.colors.iconPrimary,
                        textAlign = TextAlign.Left
                    )
                }
                controls()
            }
            Spacer(modifier = Modifier.height(12.dp))
            ActivityDetailsBar()
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(1.dp)
                .background(color = Color(0xFFE8E8E8)))


        }
    }
}

@Composable
fun ActivityDetailsBar() {

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
                Text(text ="Basen" , style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.ExtraLight, fontSize = 12.sp)
                    , color = SocialTheme.colors.textPrimary )
                Spacer(modifier = Modifier.width(24.dp))
                Icon(painter = painterResource(id = R.drawable.ic_date), contentDescription = null,tint =SocialTheme.colors.iconPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text ="12/12/2022", style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.ExtraLight, fontSize = 12.sp)
                    , color = SocialTheme.colors.textPrimary)
                Spacer(modifier = Modifier.width(24.dp))
                Icon(painter = painterResource(id = R.drawable.ic_timer), contentDescription = null, tint =SocialTheme.colors.iconPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text ="15:15 - 15:15", style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.ExtraLight, fontSize = 12.sp)
                    , color = SocialTheme.colors.textPrimary )
                Spacer(modifier = Modifier.width(24.dp))
            }
        }

}

@Composable
fun controls(){
    androidx.compose.material3.Card(
        modifier = Modifier
            .height(140.dp)
            .width(56.dp),
        shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, color = Color(0xFFEFEAFF))
    ) {

        val isDark = isSystemInDarkTheme()
        Box(
            modifier = Modifier
                .background(color = if (isDark) Color(0xFF25232A) else Color.White)
                .fillMaxSize()
        ) {

            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(painter = painterResource(id =R.drawable.ic_heart ), contentDescription = null, tint =SocialTheme.colors.iconPrimary)
                }

                IconButton(onClick = { /*TODO*/ }) {
                    Icon(painter = painterResource(id =R.drawable.ic_chat ), contentDescription = null, tint =SocialTheme.colors.iconPrimary)
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(painter = painterResource(id =R.drawable.ic_bookmark ), contentDescription = null,tint =SocialTheme.colors.iconPrimary)
                }
            }
        }

    }


}

@Preview(showBackground = true)
@Composable
fun previewActivityItem() {
    SocialTheme {
        ActivityItem(
            "Adamo",
            "URl",
            "Starts in 12 minutes",
                    "LONG TITLEVERY LONG TITLEVERY LONG TITLEVERY LONG TITLEVERY LONG TITLE",
                    "LONG TITLEVERY LONG TITLEVERY LONG TITLEVERY LONG TITLEVERY LONG TITLE",
            "DATE",
            "15:15 - 15:15",
            "location"
        )
    }
}


@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun previewActivityItemDark() {
    SocialTheme {
        ActivityItem(
            "Adamo",
            "URl",
            "Starts in 12 minutes",
            "LONG TITLEVERY LONG TITLEVERY LONG TITLEVERY LONG TITLEVERY LONG TITLE",
            "LONG TITLEVERY LONG TITLEVERY LONG TITLEVERY LONG TITLEVERY LONG TITLE",
            "DATE",
            "15:15 - 15:15",
            "location"
        )
    }
}