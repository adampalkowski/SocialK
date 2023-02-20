package com.example.socialk.components.timepicker

import androidx.annotation.StringRes
import com.example.socialk.R


enum class UnitType(val isDate: Boolean) {
    DAY(true),
    MONTH(true),
    YEAR(true),
    HOUR(false),
    MINUTE(false),
    SECOND(false),
    AM_PM(false)
}

/**
 * A representation of a unit with a placeholder, the options that can be selected and the current value.
 * @param placeholderRes The resource of a text that will be used as placeholder text.
 * @param options The list of of options that can be selected.
 * @param value The current selected value.
 */
internal sealed class UnitSelection(
    @StringRes open val placeholderRes: Int? = null,
    open val options: List<com.example.socialk.components.timepicker.UnitOptionEntry> = listOf(),
    open val value: com.example.socialk.components.timepicker.UnitOptionEntry? = null,
    open val type: com.example.socialk.components.timepicker.UnitType? = null
) {

    /**
     * Representation of the selection between am and pm for the 12HourFormat.
     */
    data class AmPm(
        override val value: com.example.socialk.components.timepicker.UnitOptionEntry? = null,
        override val options: List<com.example.socialk.components.timepicker.UnitOptionEntry>,
    ) : UnitSelection(
        type = UnitType.AM_PM
    )

    /**
     * Representation of the hours selection.
     * @param options The list of of options that can be selected.
     * @param value The current selected value.
     */
    data class Hour(
        override val value: com.example.socialk.components.timepicker.UnitOptionEntry? = null,
        override val options: List<com.example.socialk.components.timepicker.UnitOptionEntry>,
    ) : UnitSelection(
        placeholderRes = R.string.app_name,
        type = UnitType.HOUR
    )

    /**
     * Representation of the minutes selection.
     * @param options The list of of options that can be selected.
     * @param value The current selected value.
     */
    data class Minute(
        override val value: com.example.socialk.components.timepicker.UnitOptionEntry? = null,
        override val options: List<com.example.socialk.components.timepicker.UnitOptionEntry>,
    ) : UnitSelection(
        placeholderRes = R.string.app_name,
        type = UnitType.MINUTE
    )

    /**
     * Representation of the seconds selection.
     * @param options The list of of options that can be selected.
     * @param value The current selected value.
     */
    data class Second(
        override val value: com.example.socialk.components.timepicker.UnitOptionEntry? = null,
        override val options: List<com.example.socialk.components.timepicker.UnitOptionEntry>,
    ) : UnitSelection(
        placeholderRes = R.string.app_name,
        type = UnitType.SECOND
    )

    /**
     * Representation of the day selection.
     * @param options The list of of options that can be selected.
     * @param value The current selected value.
     */
    data class Day(
        override val value: com.example.socialk.components.timepicker.UnitOptionEntry? = null,
        override val options: List<com.example.socialk.components.timepicker.UnitOptionEntry>
    ) : UnitSelection(
        placeholderRes = R.string.app_name,
        type = UnitType.DAY
    )

    /**
     * Representation of the month selection.
     * @param options The list of of options that can be selected.
     * @param value The current selected value.
     */
    data class Month(
        override val value: com.example.socialk.components.timepicker.UnitOptionEntry? = null,
        override val options: List<com.example.socialk.components.timepicker.UnitOptionEntry>,
    ) : UnitSelection(
        placeholderRes = R.string.app_name,
        type = UnitType.MONTH
    )

    /**
     * Representation of the year selection.
     * @param options The list of of options that can be selected.
     * @param value The current selected value.
     */
    data class Year(
        override val value: com.example.socialk.components.timepicker.UnitOptionEntry? = null,
        override val options: List<com.example.socialk.components.timepicker.UnitOptionEntry>
    ) : UnitSelection(
        placeholderRes = R.string.app_name,
        type = UnitType.YEAR
    )
}