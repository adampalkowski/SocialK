package com.example.socialk.components.timepicker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.io.Serializable

/**
 * The base class for the use-case view states.
 */
abstract class BaseTypeState : Serializable {

    open var inputDisabled by mutableStateOf(false)

    /**
     * Disables the input for the use-case view state.
     */
    fun disableInput() {
        inputDisabled = true
    }

    abstract fun reset()
}
