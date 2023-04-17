package com.example.socialk.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagLabelItem (modifier:Modifier=Modifier,title:String,icon:Int,selected:Boolean,checkedChange:(Boolean)->Unit){
    Card(border = BorderStroke(1.dp,SocialTheme.colors.uiFloated),shape= RoundedCornerShape(6.dp), onClick = { checkedChange(!selected) }) {
        Box(modifier = Modifier.background(color=SocialTheme.colors.uiBackground)){
            Row(verticalAlignment = Alignment.CenterVertically){
                Spacer(modifier = Modifier.width(12.dp))
                Icon(painter = painterResource(id = icon) , contentDescription =null, tint = SocialTheme.colors.iconInteractive)
                Spacer(modifier = Modifier.width(8.dp))

                Text(text =title , style = TextStyle(fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = SocialTheme.colors.textPrimary))
                Spacer(modifier = Modifier.weight(1f))
                androidx.compose.material3.Checkbox(
                    checked = selected,
                    colors = CheckboxDefaults.colors(
                        checkedColor = SocialTheme.colors.iconInteractive,
                        uncheckedColor =  SocialTheme.colors.textPrimary.copy(alpha=0.75f)
                    ),
                    onCheckedChange =checkedChange)
            }
        }
    }
    
}