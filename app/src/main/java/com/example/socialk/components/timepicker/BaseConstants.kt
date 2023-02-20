package com.example.socialk.components.timepicker

import androidx.compose.ui.unit.dp

/**
 * Defines module-wide constants.
 */
object BaseConstants {

    // Behaviours

    const val SUCCESS_DISMISS_DELAY = 600L

    val DEFAULT_LIB_LAYOUT: LibOrientation? = null // Auto orientation

    val KEYBOARD_HEIGHT_MAX = 300.dp
    const val KEYBOARD_RATIO = 0.8f

    val DYNAMIC_SIZE_MAX = 200.dp

    val DEFAULT_NEGATIVE_BUTTON = SelectionButton(
        textRes = android.R.string.cancel,
        type = ButtonStyle.TEXT
    )

    val DEFAULT_POSITIVE_BUTTON =
        SelectionButton(
            textRes = android.R.string.ok,
            type = ButtonStyle.TEXT
        )
}