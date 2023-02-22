package com.example.socialk.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.socialk.model.Activity
import com.example.socialk.model.UserData
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme

@Composable
fun ActivityDialog(onDismiss:()->Unit,onConfirm:(Int)->Unit,onCancel:(Int)->Unit,
    title:String,info:String,icon:Int,activity: Activity
){
        Dialog(onDismissRequest =onDismiss) {
            Card(shape= RoundedCornerShape(16.dp)) {
                Box(modifier = Modifier.background(color=SocialTheme.colors.uiBackground),){
                 ActivityItem(
                     activity = activity,
                     username = activity.creator_username,
                     profilePictureUrl =activity.creator_profile_picture ,
                     timeLeft = activity.time_left,
                     title = "ASDHASIDAHSDIASDHASKJDHASkd",
                     description ="ASDASJDGASIDAGSHDASJdakd" ,
                     date = activity.date,
                     timePeriod = activity.start_time + " - " + activity.end_time,
                     custom_location = "ADJASDHKASDHASDJKASd",
                     liked =activity.participants_usernames.containsKey(UserData.user!!.id),
                     onEvent = {},
                     location = activity.location
                 )
                }

            }

        }


    }