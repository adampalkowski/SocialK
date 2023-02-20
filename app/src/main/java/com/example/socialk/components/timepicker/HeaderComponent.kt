package com.example.socialk.components.timepicker

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


/**
 * Header component of the dialog.
 * @param header Implementation of the header.
 */
@ExperimentalMaterial3Api
@Composable
fun HeaderComponent(
    header: Header,
    contentHorizontalPadding: PaddingValues,
) {
    when (header) {
        is Header.Custom -> header.header.invoke()
        is Header.Default -> DefaultHeaderComponent(header, contentHorizontalPadding)
    }
}

/**
 * The default header component for a dialog.
 * @param header The data of the default header.
 */
@Composable
private fun DefaultHeaderComponent(
    header: Header.Default,
    contentHorizontalPadding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(contentHorizontalPadding)
            .padding(top = 8.dp),
        horizontalAlignment = if (header.icon != null) Alignment.CenterHorizontally else Alignment.Start
    ) {
        header.icon?.let {
            IconComponent(
                modifier = Modifier
                    .size( 8.dp),
                iconSource = it,
                defaultTint = MaterialTheme.colorScheme.secondary
            )
        }
        Text(
            text = header.title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(
                    top = if (header.icon != null) 8.dp
                    else 0.dp
                ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = if (header.icon != null) TextAlign.Center else TextAlign.Start
        )
    }
}