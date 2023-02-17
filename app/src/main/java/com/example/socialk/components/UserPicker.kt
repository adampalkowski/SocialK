package com.example.socialk.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.socialk.R
import com.example.socialk.create.CreateEvent
import com.example.socialk.create.Divider
import com.example.socialk.di.UserViewModel
import com.example.socialk.model.Response
import com.example.socialk.model.User
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme
import java.util.ArrayList


@Composable
fun UserPicker(modifier: Modifier, onEvent: (CreateEvent) -> Unit, userViewModel: UserViewModel,selected_list:List<User>) {



    val friends_flow = userViewModel.friendState.collectAsState()
    var friends_list = ArrayList<User>()
    Box(
        modifier = modifier
            .height(400.dp)
            .padding(vertical = 6.dp)
    ) {
        Column() {

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_group_not_filled),
                    contentDescription = null,
                    tint = SocialTheme.colors.iconSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Select group",
                    fontFamily = Inter,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = SocialTheme.colors.iconSecondary
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "All friends",
                    fontFamily = Inter,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = SocialTheme.colors.textInteractive
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row (modifier= Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)){
                selected_list.reversed().forEach {
                    SelectedName(it = it)
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyHorizontalGrid(rows = GridCells.Fixed(3)) {
                friends_flow.value.let {
                 when(it){
                     is Response.Success->{
                         items(it.data) {
                            UserItem(user = it, onEvent = onEvent

                            )
                        }

                     }
                     is Response.Loading->{}
                     is Response.Failure->{}
                 }
                 }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Divider()
        }

    }
}


@Composable
fun SelectedName(it: User) {
    Card(
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, color = SocialTheme.colors.uiFloated)
    ) {
        Box(modifier= Modifier
            .background(color = SocialTheme.colors.uiBackground)
            .padding(4.dp)){
            Text(text = it.username.toString(), style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Light, fontSize = 10.sp), color = SocialTheme.colors.textPrimary)
        }

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UserItem(user: User, onEvent: (CreateEvent) -> Unit) {
    var selected: Boolean by rememberSaveable {
        mutableStateOf(false)
    }
    Row() {
        Box(
            modifier = Modifier
                .padding(6.dp)
                .widthIn(40.dp, 120.dp)
        ) {
            Card(
                elevation = if (selected) {
                    2.dp
                } else {
                    0.dp
                },border = if(selected){BorderStroke(1.dp,color=SocialTheme.colors.uiFloated)}else{
                    BorderStroke(0.dp,color=SocialTheme.colors.uiBackground)
                },
                shape = RoundedCornerShape(12.dp),
                onClick = {
                    if(!selected){onEvent(CreateEvent.UserSelected(user))}else{onEvent(CreateEvent.UserUnSelected(user))}
                    selected = !selected

                }) {
                Box(
                    modifier = Modifier
                        .background(
                            color = SocialTheme.colors.uiBackground
                        )
                        .padding(8.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = rememberAsyncImagePainter(user.pictureUrl),
                            contentDescription = "profile image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = user.username.toString(),
                            color = SocialTheme.colors.textPrimary,
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Light,
                                fontFamily = Inter
                            )
                        )
                    }
                }
            }
        }

    }
}
