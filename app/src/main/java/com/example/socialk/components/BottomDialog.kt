package com.example.socialk.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialk.R
import com.example.socialk.chat.ChatScreen
import com.example.socialk.chat.ChatScreenBottomInputs
import com.example.socialk.model.Activity
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun BottomDialog(state:ModalBottomSheetState=rememberModalBottomSheetState(ModalBottomSheetValue.Hidden), activity: Activity, type:String) {
    SocialTheme() {
        ModalBottomSheetLayout(sheetShape = RoundedCornerShape(12.dp),
            sheetState = state, sheetBackgroundColor = SocialTheme.colors.uiFloated,
            sheetContentColor = SocialTheme.colors.textPrimary, scrimColor = Color(0x7E313131),
            sheetContent = {
                if (type.equals("chat")){
                    Box(modifier = Modifier.background(color=SocialTheme.colors.uiBackground)){
                        Text(text = activity.title, style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.Medium, fontSize = 18.sp))


                    }
                }else{
                    Box(modifier = Modifier.background(color=SocialTheme.colors.uiBackground)){
                        Column() {
                            Spacer(modifier = Modifier.width(12.dp))
                            SettingsItem(text="Display participants",icon=R.drawable.ic_group_not_filled, onClick ={/*TODO*/} )
                            SettingsItem(text="Hide activity",icon=R.drawable.ic_visibility_off,  onClick ={/*TODO*/})
                            SettingsItem(text="Copy activity link",icon=R.drawable.ic_link,  onClick ={/*TODO*/}  )
                            SettingsItem(text="Share activity",icon=R.drawable.ic_share,  onClick ={/*TODO*/} )
                            SettingsItem(text="Delete posted picture",icon=R.drawable.ic_hide_image, onClick ={/*TODO*/})
                            SettingsItem(text="Suggest time change",icon=R.drawable.ic_update_time,  onClick ={/*TODO*/} )
                            SettingsItem(text="Suggest date change",icon=R.drawable.ic_update_date, onClick ={/*TODO*/} )
                            SettingsItem(text="Suggest location change",icon=R.drawable.ic_update_location, onClick ={/*TODO*/} )
                        }
                    }
                }


            }
        ) {

        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsItem(text: String, icon: Int, onClick: () -> Unit){
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(color = SocialTheme.colors.uiBackground),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = SocialTheme.colors.uiBackground,
        ),
        onClick = onClick
    ) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(12.dp))
            androidx.compose.material3.Icon(
                painter = painterResource(id = icon),
                tint = SocialTheme.colors.iconPrimary,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(12.dp))
            androidx.compose.material3.Text(
                text = text, color = Color.Black, fontSize = 14.sp,
                style = MaterialTheme.typography.body2, textAlign = TextAlign.Center
            )
            Spacer(Modifier.weight(1f))
            androidx.compose.material3.Icon(
                painter = painterResource(id = R.drawable.ic_right),
                tint = SocialTheme.colors.iconPrimary,
                contentDescription = null
            )
            Spacer(Modifier.width(24.dp))
        }

    }
}
