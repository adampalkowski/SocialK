package com.example.socialk.components.timepicker

import androidx.annotation.StringRes
import java.io.Serializable


/**
 * A class that acts as a value item that can be selected for a unit.
 * @param value The actual value.
 * @param label The textual representation of the value.
 * @param labelRes The textual representation of the value by resource.
 */
data class UnitOptionEntry(
    val value: Int,
    val label: String? = null,
    @StringRes val labelRes: Int? = null
) : Serializable
