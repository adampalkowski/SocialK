package com.example.socialk.create

import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.socialk.R
import com.example.socialk.home.cardHighlited
import com.example.socialk.home.cardnotHighlited
import com.example.socialk.ui.theme.SocialTheme

@Composable
fun activityPickerCreate(
    isDark: Boolean,
    modifier: Modifier = Modifier,
    onEvent: (CreateEvent) -> Unit
) {
    Box(modifier =Modifier.fillMaxWidth().padding(horizontal = 12.dp) ){

    IconButton(
        onClick ={ onEvent(CreateEvent.GoBack)},
        modifier = Modifier.align(Alignment.CenterStart)
        ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = null,
            tint =  SocialTheme.colors.iconInteractive,
        )
    }
    Row(
        modifier = Modifier
            .height(48.dp)
            .padding(horizontal = 8.dp).align(Alignment.Center), horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        cardnotHighlited(text = "Live", onEvent = { onEvent(CreateEvent.GoToLive) })
        Spacer(Modifier.width(4.dp))
        cardHighlited(text = "Activities", isDark = isDark)
        Spacer(Modifier.width(4.dp))
        cardnotHighlited(text = "Event", onEvent = { onEvent(CreateEvent.GoToEvent) })
    }
    }

}
