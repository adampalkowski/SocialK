package com.example.socialk.components.timepicker

import java.time.LocalDate

class TimeConfig (
    open val orientation: LibOrientation? = BaseConstants.DEFAULT_LIB_LAYOUT,
    val hideDateCharacters: Boolean = false,
    val hideTimeCharacters: Boolean = false,
    val minYear: Int = Constants.DEFAULT_MIN_YEAR,
    val maxYear: Int = Constants.DEFAULT_MAX_YEAR,
    )