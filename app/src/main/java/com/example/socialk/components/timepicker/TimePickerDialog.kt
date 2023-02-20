package com.example.socialk.components.timepicker

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(state:SheetState, selection:TimeSelection, config:TimeConfig=TimeConfig(), header:Header? = null, properties: DialogProperties = DialogProperties()){
    DialogBase( state = state,
        properties = properties, content = {

            TimeView(  sheetState = state,
                selection = selection,
                config = config,
                header = header,)

        }){

    }
}