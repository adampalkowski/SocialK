package com.example.socialk

import android.widget.Space
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.socialk.chat.ChatEvent
import com.example.socialk.components.ScreenHeading
import com.example.socialk.ui.theme.*
import kotlin.text.Typography

sealed class SearchEvent {
    object GoToProfile : SearchEvent()
    object LogOut : SearchEvent()
    object GoToSettings : SearchEvent()
    object GoToChat : SearchEvent()
    object GoBack : SearchEvent()

}

@Composable
fun SearchScreen(onEvent: (SearchEvent) -> Unit) {
    Surface(modifier = Modifier
        .fillMaxSize()
        .background(color = SocialTheme.colors.uiBackground) ,color = SocialTheme.colors.uiBackground) {

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
        //HEADING
        ScreenHeading(onClick = { onEvent(SearchEvent.GoBack) }, title = "Search")
        Spacer(modifier = Modifier.height(36.dp))
        //EDIT TEXT
        searchEditText()
        Spacer(modifier = Modifier.height(4.dp))
        //INFORMATION
        Text(text = "Search for new people by their username or \n share your profile link",
            textAlign = TextAlign.Center, style = com.example.socialk.ui.theme.Typography.h6
        , color = Color(0xFF666666))
        //TEXT INVITES
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp), contentAlignment = Alignment.CenterStart){
            Text(text = "Invites", style = TextStyle(fontWeight = FontWeight.Medium, fontFamily =Inter , fontSize = 18.sp), color = Color(0xff333333))
        }
      
        Spacer(modifier = Modifier.height(24.dp))
        //INVITES ROW
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())){
            InviteCard(profileUrl ="https://firebasestorage.googleapis.com/v0/b/socialv2-340711.appspot.com/o/uploads%2F1662065348037.null?alt=media&token=40cebce4-0c53-470c-867f-d9d34cba63ab"
                , name = "AdamPałkowłkowskiPa", username = "AdawskiPa", onClick = {})
            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier
                .height(120.dp)
                .width(1.dp)
                .background(color = SocialTheme.colors.uiFloated))
            Spacer(modifier = Modifier.width(16.dp))
            InviteCard(profileUrl ="https://firebasestorage.googleapis.com/v0/b/socialv2-340711.appspot.com/o/uploads%2F1662065348037.null?alt=media&token=40cebce4-0c53-470c-867f-d9d34cba63ab"
                , name = "Adam Pałkowski", username = "adamo12321", onClick = {})
            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier
                .height(120.dp)
                .width(1.dp)
                .background(color = SocialTheme.colors.uiFloated))

            Spacer(modifier = Modifier.width(16.dp))
            InviteCard(profileUrl = "https://firebasestorage.googleapis.com/v0/b/socialv2-340711.appspot.com/o/uploads%2F1662065348037.null?alt=media&token=40cebce4-0c53-470c-867f-d9d34cba63ab"
                , name = "Adam Pałkowski", username = "adamo12321", onClick = {})
            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier
                .height(120.dp)
                .width(1.dp)
                .background(color = SocialTheme.colors.uiFloated))

            Spacer(modifier = Modifier.width(16.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
        //TEXT YOU MAY WANT TO KNOW
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp), contentAlignment = Alignment.CenterStart){
            Text(text = "You may want to know", style = com.example.socialk.ui.theme.Typography.h3, color = Color(0xff333333))
        }
        Spacer(modifier = Modifier.height(24.dp))
        //YOU MAY WANT TO KNOW ROW
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())){
            ProposedCard(profileUrl ="https://firebasestorage.googleapis.com/v0/b/socialv2-340711.appspot.com/o/uploads%2F1662065348037.null?alt=media&token=40cebce4-0c53-470c-867f-d9d34cba63ab"
                , name = "AdamPałkowskiPałkowskiPa", username = "AdamPaPa", onClick = {})
            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier
                .height(120.dp)
                .width(1.dp)
                .background(color = SocialTheme.colors.uiFloated))

            Spacer(modifier = Modifier.width(16.dp))
            ProposedCard(profileUrl ="https://firebasestorage.googleapis.com/v0/b/socialv2-340711.appspot.com/o/uploads%2F1662065348037.null?alt=media&token=40cebce4-0c53-470c-867f-d9d34cba63ab"
                , name = "Adam Pałkowski", username = "adamo12321", onClick = {})
            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier
                .height(120.dp)
                .width(1.dp)
                .background(color = SocialTheme.colors.uiFloated))

            Spacer(modifier = Modifier.width(16.dp))
            ProposedCard(profileUrl = "https://firebasestorage.googleapis.com/v0/b/socialv2-340711.appspot.com/o/uploads%2F1662065348037.null?alt=media&token=40cebce4-0c53-470c-867f-d9d34cba63ab"
                , name = "Adam Pałkowski", username = "adamo12321", onClick = {})
        }
        
        Spacer(modifier = Modifier.height(48.dp))

        //TODO HARDCODED NAME
        ButtonLink(onClick = {}, username = "name")
    }
    }

}

@Composable
fun ButtonLink(onClick: () -> Unit,username:String){
        Box(modifier = Modifier

            .padding(horizontal = 16.dp, vertical = 12.dp), contentAlignment = Alignment.Center){
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painter = painterResource(id = R.drawable.ic_link), contentDescription =null ,tint=Ocean8)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text ="friendupp.app/"+username , style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                ,color= Ocean8)
            }
        }

}
@Composable
fun ProposedCard(profileUrl:String,name:String,username:String,onClick:()->Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = rememberAsyncImagePainter(profileUrl),
            contentDescription = "invite profile image", modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(modifier = Modifier.width(120.dp), textAlign = TextAlign.Center,
            text = name, style = com.example.socialk.ui.theme.Typography.h5, color = SocialTheme.colors.textPrimary)
        Text(modifier = Modifier.width(120.dp), textAlign = TextAlign.Center
            ,text = username, style = com.example.socialk.ui.theme.Typography.h6, color = SocialTheme.colors.textPrimary)
        Spacer(modifier = Modifier.height(16.dp))
        searchActionButton("Invite",onClick=onClick)
    }
}


@Composable
fun InviteCard(profileUrl:String,name:String,username:String,onClick:()->Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = rememberAsyncImagePainter(profileUrl),
            contentDescription = "invite profile image", modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(modifier = Modifier.width(120.dp), textAlign = TextAlign.Center,
            text = name, style = com.example.socialk.ui.theme.Typography.h5, color = SocialTheme.colors.textPrimary)
        Text(modifier = Modifier.width(120.dp), textAlign = TextAlign.Center,
            text = username, style =com.example.socialk.ui.theme.Typography.h6, color = SocialTheme.colors.textPrimary)
        Spacer(modifier = Modifier.height(16.dp))
        searchActionButton("Accept",onClick=onClick)
    }
}

@Composable
fun searchActionButton(label:String,onClick: () -> Unit)
{
    Card(shape = RoundedCornerShape(8.dp), elevation = 0.dp, border = BorderStroke(1.dp,
        color= if(label.equals("Accept")){ if (isSystemInDarkTheme()) Black else Color(0xFFFFFFFF)}  else SocialTheme.colors.uiFloated)) {
        Box(modifier = Modifier
            .background(color = if (label.equals("Accept")){ if (isSystemInDarkTheme()) Ocean11 else Ocean8}
            else SocialTheme.colors.uiBackground)
            .padding(horizontal = 16.dp, vertical = 6.dp), contentAlignment = Alignment.Center){
            Text(text = label, style = com.example.socialk.ui.theme.Typography.button
                ,color=if(label.equals("Accept")) Color.White else SocialTheme.colors.textPrimary)
        }
    }

}


@Composable
fun searchEditText() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 36.dp)
    ) {
        Card(
            modifier = Modifier
             , shape = RoundedCornerShape(12.dp)
              , border = BorderStroke(1.dp,color=SocialTheme.colors.uiFloated)
        ) {
            Box(modifier = Modifier
                .background(color = SocialTheme.colors.uiBackground)
                .padding(horizontal = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add_person),
                        contentDescription = null,
                        tint = SocialTheme.colors.iconPrimary
                    )
                    TextField(modifier = Modifier.fillMaxWidth(),
                        value = "",
                        placeholder = { Text(text = "Search by username", style =  com.example.socialk.ui.theme.Typography.h4, color = Color(
                            0xFF939393
                        )
                        ) },
                        textStyle =com.example.socialk.ui.theme.Typography.h4,
                        colors = TextFieldDefaults.textFieldColors(textColor = SocialTheme.colors.iconPrimary
                            ,backgroundColor = Color.Transparent
                            , focusedIndicatorColor = Color.Transparent,unfocusedIndicatorColor = Color.Transparent),
                        onValueChange = {})
                }
            }
        }
    }
}


@Preview
@Composable
fun previewSearchScreen() {
    SocialTheme() {
        SearchScreen(onEvent = {})

    }
}