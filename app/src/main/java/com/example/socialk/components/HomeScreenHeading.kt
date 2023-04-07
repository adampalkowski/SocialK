package com.example.socialk.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.socialk.ProfileEvent
import com.example.socialk.ui.theme.SocialTheme
import com.example.socialk.ui.theme.Typography
import com.example.socialk.R

@Composable
fun HomeScreenHeading(onEvent: () -> Unit, title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp)
            .padding(top = 8.dp)
            .heightIn(48.dp), contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = {  onEvent()},
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    tint = SocialTheme.colors.iconInteractive,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = Typography.h3,
                color= SocialTheme.colors.textPrimary
            )
        }

    }
}