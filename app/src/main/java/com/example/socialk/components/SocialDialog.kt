package com.example.socialk.components

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
import com.example.socialk.R
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme

@Composable
fun SocialDialog(onDismiss:()->Unit,onConfirm:(Int)->Unit,onCancel:(Int)->Unit,title:String,info:String,icon:Int,actionButtonText:String="Delete"){
    Dialog(onDismissRequest =onDismiss) {
        Card(shape= RoundedCornerShape(16.dp)) {
            Box(modifier = Modifier.padding(24.dp),){

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(painter = painterResource(id = icon), contentDescription = null, tint = SocialTheme.colors.iconPrimary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = title,style= TextStyle(
                        fontWeight = FontWeight.Medium, fontSize = 20.sp, fontFamily = Inter
                    )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = info, style = TextStyle(
                        fontSize = 14.sp, fontWeight = FontWeight.Normal , fontFamily = Inter
                    )
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.End) {
                        ClickableText(text = AnnotatedString("Cancel")
                            , style = TextStyle(color= SocialTheme.colors.textPrimary,
                                fontFamily = Inter , fontWeight = FontWeight.Medium , fontSize = 14.sp
                            ), onClick = onCancel)
                        Spacer(modifier = Modifier.width(24.dp))
                        ClickableText(text = AnnotatedString("Delete"), style = TextStyle(color= Color.Red,
                            fontFamily = Inter , fontWeight = FontWeight.Medium , fontSize = 14.sp
                        ), onClick =onConfirm )
                    }
                }
            }

        }

    }


}