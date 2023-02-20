package com.example.socialk.components.timepicker

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp

/**
 * A component that displays a character of the date pattern.
 * @param text The character that was found in the date pattern.
 */
@Composable
internal fun PickerDateCharacterComponent(text: String) {
    Text(
        modifier = Modifier
            .clip(MaterialTheme.shapes.extraSmall)
            .padding(start = 8.dp)
            .padding(bottom = 8.dp)
            .padding(end = 8.dp),
        text = text
    )
}
