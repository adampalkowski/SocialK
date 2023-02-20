package com.example.socialk.components.timepicker

import com.example.socialk.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month
import java.time.chrono.Chronology
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.util.*


internal fun detectUnit(
    config: TimeConfig,
    pattern: String,
    segment: String,
    unitValues: MutableMap<UnitType, UnitOptionEntry?>,
): UnitSelection? {
    val now = LocalDate.now()
    val month = unitValues[UnitType.MONTH]
    val date = LocalDate.of(
        /* year = */ unitValues[UnitType.YEAR]?.value ?: now.year,
        /* month = */ month?.value ?: now.monthValue,
        /* dayOfMonth = */ 1
    )
    return when {
        segment.contains(Constants.SYMBOL_SECONDS) ->
            UnitSelection.Second(
                value = unitValues[UnitType.SECOND],
                options = getMinutesSecondsOptions()
            )
        segment.contains(Constants.SYMBOL_MINUTES) ->
            UnitSelection.Minute(
                value = unitValues[UnitType.MINUTE],
                options = getMinutesSecondsOptions()
            )
        segment.contains(Constants.SYMBOL_HOUR, ignoreCase = true) ->
            UnitSelection.Hour(
                value = unitValues[UnitType.HOUR],
                options = getHoursOptions(pattern)
            )
        segment.contains(Constants.SYMBOL_DAY) ->
            UnitSelection.Day(
                value = unitValues[UnitType.DAY],
                options = getDayOptions(date, month)
            )
        segment.contains(Constants.SYMBOL_MONTH) ->
            UnitSelection.Month(
                value = unitValues[UnitType.MONTH],
                options = getMonthOptions(segment)
            )
        segment.contains(Constants.SYMBOL_24_HOUR_FORMAT) -> {
            UnitSelection.AmPm(
                value = unitValues[UnitType.AM_PM],
                options = getAmPmOptions()
            )
        }
        segment.contains(Constants.SYMBOL_YEAR, ignoreCase = true) ->
            UnitSelection.Year(
                value = unitValues[UnitType.YEAR],
                options = getYearOptions(config)
            )

        else -> null
    }
}

internal fun getLocalTimeOf(
    isAm: Boolean?,
    values: List<UnitOptionEntry?>,
) = runCatching {

    val hourValue = values[2]!!.value
    val minValue = values[1]!!.value
    val secValue = values[0]?.value ?: 0
    var actualHourValue = hourValue

    isAm?.let {
        if (isAm && actualHourValue >= 12 && minValue > 0) actualHourValue -= 12
        else if (!isAm && ((actualHourValue < 12 && minValue >= 0) || (actualHourValue == 12 && minValue == 0))) actualHourValue += 12
        if (actualHourValue == 24) actualHourValue = 0
    }

    LocalTime.of(
        actualHourValue,
        minValue,
        secValue
    )

}.getOrNull()

internal fun getLocalDateOf(
    values: List<UnitOptionEntry?>,
) = runCatching {
    LocalDate.of(
        values[2]!!.value,
        values[1]!!.value,
        values[0]!!.value
    )
}.getOrNull()

internal fun getLocalizedPattern(
    isDate: Boolean,
    formatStyle: FormatStyle,
    locale: Locale
): String = DateTimeFormatterBuilder.getLocalizedDateTimePattern(
    if (isDate) formatStyle else null,
    if (!isDate) formatStyle else null, Chronology.ofLocale(locale), locale
).toString()

internal fun getLocalizedValues(
    config: TimeConfig,
    pattern: String?,
    unitValues: MutableMap<UnitType, UnitOptionEntry?>
): List<List<Any?>>? {
    val values = pattern?.split(" ", ".", ":", "-")?.toTypedArray()
    return values?.map { value ->
        val segments = getLocalizedValueSegments(value)
        segments.map { segment ->
            if (!config.hideDateCharacters && segment.isEmpty()) segment
            else detectUnit(
                config = config,
                pattern = pattern,
                segment = segment,
                unitValues = unitValues
            )
        }
    }
}

internal fun getLocalizedValueSegments(segment: String): List<String> =
    segment.split(",", ".").dropLastWhile { it.isEmpty() }

internal fun is24HourFormat(pattern: String): Boolean =
    !containsAmPm(pattern)

private fun containsAmPm(pattern: String): Boolean =
    pattern.contains(Constants.SYMBOL_24_HOUR_FORMAT)

internal fun containsSeconds(pattern: String): Boolean = pattern.contains(Constants.SYMBOL_SECONDS)

fun getAmPmOptions() = listOf(
    UnitOptionEntry(value = 0, labelRes = R.string.app_name),
    UnitOptionEntry(value = 1, labelRes = R.string.app_name),
)

private fun getMinutesSecondsOptions(): List<UnitOptionEntry> {
    return (0..59).map {
        UnitOptionEntry(
            it,
            it.toString().padStart(2, '0')
        )
    }.toList()
}

private fun getHoursOptions(pattern: String): List<UnitOptionEntry> =
    if (is24HourFormat(pattern)) {
        (0..23).map { value ->
            UnitOptionEntry(
                value = value,
                label = value.toString().padStart(2, '0')
            )
        }.toList()
    } else {
        (1..12).map { value ->
            UnitOptionEntry(
                value = value,
                label = value.toString()
            )
        }.toList()
    }

private fun getDayOptions(date: LocalDate, month: UnitOptionEntry?): List<UnitOptionEntry> {
    val daysInMonth = date.lengthOfMonth()
    return (1..(if (month?.value != null) 31 else daysInMonth)).map {
        UnitOptionEntry(
            it,
            it.toString()
        )
    }
}

private fun getMonthOptions(pattern: String): List<UnitOptionEntry> {
    val occurrences = pattern.count { it.equals('m', ignoreCase = true) }
    return when {
        occurrences >= 3 -> {
            Month.values().map { month ->
                UnitOptionEntry(
                    month.value, LocalDate.now().withMonth(month.value)
                        .format(DateTimeFormatter.ofPattern(pattern))
                )
            }
        }
        else -> Month.values().map { it.value }.map {
            UnitOptionEntry(it, it.toString())
        }.toList()
    }
}

private fun getYearOptions(config: TimeConfig): List<UnitOptionEntry> =
    IntRange(config.minYear, config.maxYear.plus(1)).map { value ->
        UnitOptionEntry(
            value = value,
            label = value.toString()
        )
    }.reversed()