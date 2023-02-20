package com.example.socialk.components.timepicker

import java.time.LocalTime
import java.time.format.FormatStyle
import java.util.Locale

sealed class TimeSelection(
    open val locale: Locale = Locale.getDefault(),
    open val timeFormatStyle: FormatStyle? = null,
    open val withButtonView: Boolean = true,
    open val extraButton: SelectionButton? = null,
    open val onExtraButtonClick: (() -> Unit)? = null,
    open val negativeButton: SelectionButton? = BaseConstants.DEFAULT_NEGATIVE_BUTTON,
    open val onNegativeClick: (() -> Unit)? = null,
    open val positiveButton: SelectionButton = BaseConstants.DEFAULT_POSITIVE_BUTTON,
) {

    /**
     * Select a time.
     * @param withButtonView Show the dialog with the buttons view.
     * @param extraButton An extra button that can be used for a custom action.
     * @param onExtraButtonClick The listener that is invoked when the extra button is clicked.
     * @param negativeButton The button that will be used as a negative button.
     * @param onNegativeClick The listener that is invoked when the negative button is clicked.
     * @param positiveButton The button that will be used as a positive button.
     * @param locale The locale that is used for the date and time format.
     * @param timeFormatStyle The style of the time format.
     * @param onPositiveClick The listener that returns the selected date.
     */
    data class Time(
        override val withButtonView: Boolean = true,
        override val extraButton: SelectionButton? = null,
        override val onExtraButtonClick: (() -> Unit)? = null,
        override val negativeButton: SelectionButton? = BaseConstants.DEFAULT_NEGATIVE_BUTTON,
        override val onNegativeClick: (() -> Unit)? = null,
        override val positiveButton: SelectionButton = BaseConstants.DEFAULT_POSITIVE_BUTTON,
        override val locale: Locale = Locale.getDefault(),
        override val timeFormatStyle: FormatStyle = FormatStyle.SHORT,
        val onPositiveClick: (LocalTime) -> Unit,
    ) : TimeSelection()
}