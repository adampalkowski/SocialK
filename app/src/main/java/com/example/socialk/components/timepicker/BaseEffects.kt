package com.example.socialk.components.timepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect


@Composable
fun StateHandler(
    sheetState: com.example.socialk.components.timepicker.SheetState,
    baseState: BaseTypeState,
) {
    DisposableEffect(sheetState.reset) {
        if (sheetState.reset) {
            baseState.reset()
            sheetState.clearReset()
        }
        onDispose {}
    }
}