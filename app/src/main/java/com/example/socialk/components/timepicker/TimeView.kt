package com.example.socialk.components.timepicker

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope

/**
 * Date Time dialog for the use-case to select a date, time or both in a quick way.
 * @param sheetState The state of the sheet.
 * @param selection The selection configuration for the dialog view.
 * @param config The general configuration for the dialog view.
 * @param header The header to be displayed at the top of the dialog view.
 */
@ExperimentalMaterial3Api
@Composable
fun TimeView(sheetState: SheetState,
             selection: TimeSelection,
             config: TimeConfig = TimeConfig(),
             header: Header? = null,){
    val coroutine = rememberCoroutineScope()
    val TimeState = rememberTimeState(selection, config)
    com.example.socialk.components.timepicker.StateHandler(sheetState,TimeState)

    val processSelection: () -> Unit = {
        com.example.socialk.components.timepicker.BaseBehaviors.autoFinish(
            selection = selection,
            condition = TimeState.valid,
            coroutine = coroutine,
            onSelection = TimeState::onFinish,
            onFinished = sheetState::finish,
            onDisableInput = TimeState::disableInput
        )
    }
    LaunchedEffect(TimeState.isTimeValid) { processSelection() }
    LaunchedEffect(TimeState.isTimeValid) { processSelection() }

    com.example.socialk.components.timepicker.FrameBase(
        header = header,
        config = config,
        layout = {

                PickerComponent(
                    isDate = false,
                    values = TimeState.timeValues!!,
                    config = config,
                    onValueChange = TimeState::updateValue,
                )
        },
        buttonsVisible = selection.withButtonView
    ) {
        ButtonsComponent(
            onPositiveValid = TimeState.valid,
            selection = selection,
            onNegative = { selection.onNegativeClick?.invoke() },
            onPositive = TimeState::onFinish,
            onClose = sheetState::finish
        )
    }
}