package com.example.socialk.components.timepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime

internal class TimeState(
    val selection: TimeSelection,
    val config: TimeConfig,
    stateData: DateTimeStateData? = null
) : BaseTypeState() {
    var timeSelection by mutableStateOf<LocalTime?>(stateData?.timeSelection)
    var valid by mutableStateOf(isValid())

    private var timePattern by mutableStateOf(getTimePatternValue())

    private var typeValues = stateData?.typeValues ?: getInitTypeValues()

    var timeValues by mutableStateOf(getLocalizedValues(config, timePattern, typeValues))

    var isTimeValid by mutableStateOf(checkTimeValid())

    private fun getInitTypeValues(): MutableMap<UnitType, UnitOptionEntry?> =
        mutableMapOf(
            // Date
            UnitType.DAY to null,
            UnitType.MONTH to null,
            UnitType.YEAR to null,
            // Time
            UnitType.SECOND to null,
            UnitType.MINUTE to null,
            UnitType.HOUR to null,
            UnitType.AM_PM to getAmPmOptions().first()
        )

    fun updateValue(
        unit: UnitSelection,
        entry: UnitOptionEntry
    ) {
        unit.type?.let { type ->
            typeValues[type] = entry
                timeValues = getLocalizedValues(config, timePattern, typeValues)
                isTimeValid = checkTimeValid()
        }
        checkValid()
    }


    private fun checkTimeValid(): Boolean {
        if (timePattern == null) return false
        val tmpTimeValues = typeValues.values.drop(3)
        val secondsValid = !containsSeconds(timePattern!!) || tmpTimeValues.take(3).last() != null
        val valid = secondsValid && tmpTimeValues.take(3).drop(1).all { it != null }
        val time = if (valid) getLocalTimeOf(
            tmpTimeValues.lastOrNull()?.value == 0,
            tmpTimeValues
        ) else null
        timeSelection = time
        return valid
    }



    private fun getTimePatternValue(): String? = selection.timeFormatStyle?.let {
        getLocalizedPattern(
            isDate = false,
            locale = selection.locale,
            formatStyle = it
        )
    }

    private fun checkValid() {
        valid = isValid()
    }

    private fun isValid(): Boolean = when (selection) {
        is TimeSelection.Time -> timeSelection != null
    }


    fun onFinish() {
        when (selection) {
            is TimeSelection.Time -> selection.onPositiveClick(timeSelection!!)

        }
    }

    override fun reset() {
        timeSelection = null
    }

    companion object {

        /**
         * [Saver] implementation.
         * @param selection The selection configuration for the dialog view.
         * @param config The general configuration for the dialog view.
         */
        fun Saver(
            selection: TimeSelection,
            config :TimeConfig
        ): Saver<TimeState, *> = Saver(
            save = { state ->
                TimeState.DateTimeStateData(
                    state.timeSelection,
                    state.typeValues
                )
            },
            restore = { data -> TimeState(selection, config, data) }
        )
    }

    /**
     * Data class that stores the important information of the current state
     * and can be used by the [Saver] to save and restore the state.
     */
    data class DateTimeStateData(
        val timeSelection: LocalTime?,
        val typeValues: MutableMap<UnitType, UnitOptionEntry?>
    ) : Serializable
}

/**
 * Create a DateTimeState and remember it.
 * @param selection The selection configuration for the dialog view.
 * @param config The general configuration for the dialog view.
 */
@Composable
internal fun rememberTimeState(
    selection: TimeSelection,
    config: TimeConfig,
): TimeState = rememberSaveable(
    inputs = arrayOf(selection, config),
    saver = TimeState.Saver(selection, config),
    init = { TimeState(selection, config) }
)