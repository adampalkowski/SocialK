package com.example.socialk.create

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.socialk.home.cardHighlited
import com.example.socialk.home.cardnotHighlited

@Composable
fun activityPickerCreate(
    isDark: Boolean,
    modifier: Modifier = Modifier,
    onEvent: (CreateEvent) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 8.dp), horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        cardnotHighlited(text = "Live", onEvent = { onEvent(CreateEvent.GoToLive) })
        Spacer(Modifier.width(6.dp))
        cardHighlited(text = "Activities", isDark = isDark)
        Spacer(Modifier.width(6.dp))
        cardnotHighlited(text = "Event", onEvent = { onEvent(CreateEvent.GoToEvent) })
    }
}
