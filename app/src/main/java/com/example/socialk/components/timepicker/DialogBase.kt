package com.example.socialk.components.timepicker

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
/**
 * Base component for a dialog.
 * @param state The state of the sheet.
 * @param properties DialogProperties for further customization of this dialog's behavior.
 * @param onDialogClick Listener that is invoked when the dialog was clicked.
 * @param content The content to be displayed inside the dialog.
 */

@Composable
fun DialogBase(
    state: SheetState = rememberSheetState(true),
    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit,
    onDialogClick: (() -> Unit)? = null,
) {
    LaunchedEffect(Unit) {
        state.markAsEmbedded()
    }


    if (!state.visible) return
    val boxInteractionSource = remember { MutableInteractionSource() }
    val contentInteractionSource = remember { MutableInteractionSource() }
    Dialog(onDismissRequest = state::dismiss, properties = properties) {
        Box(contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = boxInteractionSource,
                    indication = null,
                    onClick = { if (properties.dismissOnClickOutside) state.dismiss() }
                )) {
            Surface(modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .clickable(
                    indication = null,
                    interactionSource = contentInteractionSource,
                    onClick = { onDialogClick?.invoke() }
                ), shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                content = content)
        }

    }

}