package com.example.socialk.create

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Space
import com.example.socialk.R
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.TextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialk.Create
import com.example.socialk.Destinations
import com.example.socialk.bottomTabRowScreens
import com.example.socialk.components.BottomBarRow
import com.example.socialk.home.HomeEvent
import com.example.socialk.home.cardHighlited
import com.example.socialk.home.cardnotHighlited
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme

sealed class CreateEvent{
    object GoToProfile : CreateEvent()
    object LogOut : CreateEvent()
    object GoToSettings : CreateEvent()
    object GoToHome : CreateEvent()
    object GoToLive : CreateEvent()
    object GoToEvent : CreateEvent()
    object GoToActivity: CreateEvent()
}
@Composable
fun CreateScreen (onEvent: (CreateEvent) -> Unit, bottomNavEvent:(Destinations)->Unit){
    Surface(modifier = Modifier
        .fillMaxSize()
        .background(SocialTheme.colors.uiBackground),color=SocialTheme.colors.uiBackground
    ) {

        Column(modifier = Modifier
            .fillMaxSize().padding(horizontal = 12.dp)
            ) {
            Spacer(modifier = Modifier.height(12.dp))
            activityPickerCreate(isSystemInDarkTheme(),onEvent= {event-> onEvent(event) })
            Spacer(modifier = Modifier.height(12.dp))
            createField(action = { }, title = "Time", icon = R.drawable.ic_timer)
            Spacer(modifier =Modifier.height(8.dp))
            createField(action = { }, title = "Date", icon = R.drawable.ic_date)
            Spacer(modifier =Modifier.height(8.dp))
            createField(action = {}, title = "Time", icon = R.drawable.ic_timer)
            Spacer(modifier =Modifier.height(8.dp))
            createField(action = {}, title = "Time", icon = R.drawable.ic_timer)

        }

        Box(modifier = Modifier
            .fillMaxWidth()
            .heightIn(56.dp)
            ,contentAlignment = Alignment.BottomCenter){
            BottomBarRow(allScreens = bottomTabRowScreens, onTabSelected = { screen->bottomNavEvent(screen)},currentScreen = Create)
        }
    }
}


@Composable
fun createField(action:@Composable () -> Unit,title: String,icon:Int){
    Card(modifier = Modifier, shape = RoundedCornerShape(8.dp)) {
        Box(modifier =Modifier.background(color= SocialTheme.colors.brandSecondary).padding(12.dp)){
            Row(modifier = Modifier, verticalAlignment =Alignment.CenterVertically){
                Spacer(Modifier.width(12.dp))
                Icon(painter = painterResource(id = icon), contentDescription =null,tint=SocialTheme.colors.iconPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, fontFamily = Inter, fontWeight = FontWeight.Light, fontSize = 18.sp, color = SocialTheme.colors.iconPrimary )
                Spacer(modifier = Modifier.weight(1f))
                action()
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun previewCreateField () {
    SocialTheme{
        createField(action = { textF()}, title = "Time", icon = R.drawable.ic_timer)

    }
}

@Composable
fun textF(){
    TextField(value = "s", onValueChange ={})
}




@Composable
fun activityPickerCreate(isDark:Boolean,modifier: Modifier = Modifier, onEvent: (CreateEvent) -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .padding(horizontal = 8.dp), horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
            cardnotHighlited(text="Live",onEvent= {onEvent(CreateEvent.GoToLive)})
            Spacer(Modifier.width(6.dp))
            cardHighlited(text = "Activities", isDark = isDark)
            Spacer(Modifier.width(6.dp))
            cardnotHighlited(text="Event",onEvent=  {onEvent(CreateEvent.GoToEvent)} )
        }
 }



@Preview(showBackground = true)
@Composable
fun previewCreateScreen () {
    SocialTheme{
        CreateScreen(onEvent = {}, bottomNavEvent = {})

    }
}
@Preview(showBackground = true,uiMode = UI_MODE_NIGHT_YES)
@Composable
fun previewCreateScreenDark(){
    SocialTheme{
        CreateScreen(onEvent = {}, bottomNavEvent = {})

    }
}