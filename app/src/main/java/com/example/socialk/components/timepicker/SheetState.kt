package com.example.socialk.components.timepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
/**
 * Handles the base behavior of any use-case view and the dialog, if dialog is used.
 * @param visible If the dialog is initially visible.
 * @param embedded If the view is embedded (in a Dialog, PopUp, BottomSheet or another container that has its own state).
 * @param onCloseRequest The listener that is invoked when the dialog was closed through any cause.
 * @param onFinishedRequest The listener that is invoked when the dialog's use-case was finished by the user accordingly (negative, positive, selection).
 * @param onDismissRequest The listener that is invoked when the dialog was dismissed.
 */
class SheetState(
    visible: Boolean = false,
    embedded: Boolean = true,
    internal val onFinishedRequest: (com.example.socialk.components.timepicker.SheetState.() -> Unit)? = null,
    internal val onDismissRequest: (com.example.socialk.components.timepicker.SheetState.() -> Unit)? = null,
    internal val onCloseRequest: (com.example.socialk.components.timepicker.SheetState.() -> Unit)? = null,
) {
    internal var visible by mutableStateOf(visible)
    internal var embedded by mutableStateOf(embedded)
    internal var reset by mutableStateOf(false)
    /**
     * Display the dialog / view.
     */
    fun show() {
        visible = true
    }

    /**
     * Hide the dialog / view.
     */
    fun hide() {
        visible = false
        onDismissRequest?.invoke(this)
        onCloseRequest?.invoke(this)
    }

    internal fun clearReset() {
        reset = false
    }

    /**
     * Reset the current state data.
     */
    fun invokeReset() {
        reset = true
    }

    internal fun dismiss() {
        if (!embedded) visible = false
        onDismissRequest?.invoke(this)
        onCloseRequest?.invoke(this)
    }

    /**
     * Finish the use-case view.
     */
    fun finish() {
        /*
            We don't want to remove the view itself,
            but inform through the state that the use-case is done.
            The parent container (Dialog, PopUp, BottomSheet)
            can be hidden with the use-case view.
         */
        if (!embedded) visible = false
        onFinishedRequest?.invoke(this)
        onCloseRequest?.invoke(this)
    }

    internal fun markAsEmbedded() {
        embedded = false
    }
    /**
     * Data class that stores the important information of the current state
     * and can be used by the [Saver] to save and restore the state.
     */
    data class SheetStateData(val visible: Boolean, val embedded: Boolean) : java.io.Serializable
    companion object{

        /**
         * [Saver] implementation.
         * Lambda functions need to be passed to new sheet state as they can not be serialized.
         * @param onCloseRequest The listener that is invoked when the dialog was closed through any cause.
         * @param onFinishedRequest The listener that is invoked when the dialog's use-case was finished by the user accordingly (negative, positive, selection).
         * @param onDismissRequest The listener that is invoked when the dialog was dismissed.
         */
        fun Saver(
            onCloseRequest: (com.example.socialk.components.timepicker.SheetState.() -> Unit)?,
            onFinishedRequest: (com.example.socialk.components.timepicker.SheetState.() -> Unit)?,
            onDismissRequest: (com.example.socialk.components.timepicker.SheetState.() -> Unit)?
        ): Saver<com.example.socialk.components.timepicker.SheetState, *> = Saver(save = { state ->
            SheetStateData(visible = state.visible, embedded = state.embedded)
        }, restore = { data ->
            com.example.socialk.components.timepicker.SheetState(
                visible = data.visible, embedded = data.embedded, onCloseRequest = onCloseRequest,
                onFinishedRequest = onFinishedRequest,
                onDismissRequest = onDismissRequest
            )
        }
        )
    }


}
/**
 * Create a SheetState and remember it.
 * @param visible The initial visibility.
 * @param embedded if the use-case is embedded in a container (dialog, bottomSheet, popup, ...)
 * @param onCloseRequest The listener that is invoked when the dialog was closed through any cause.
 * @param onFinishedRequest The listener that is invoked when the dialog's use-case was finished by the user accordingly (negative, positive, selection).
 * @param onDismissRequest The listener that is invoked when the dialog was dismissed.
 */
@Composable
fun rememberSheetState(
    visible: Boolean = false,
    embedded: Boolean = true,
    onCloseRequest: (com.example.socialk.components.timepicker.SheetState.() -> Unit)? = null,
    onFinishedRequest: (com.example.socialk.components.timepicker.SheetState.() -> Unit)? = null,
    onDismissRequest: (com.example.socialk.components.timepicker.SheetState.() -> Unit)? = null,
): com.example.socialk.components.timepicker.SheetState = rememberSaveable(saver = com.example.socialk.components.timepicker.SheetState.Saver(
    onCloseRequest = onCloseRequest,
    onFinishedRequest = onFinishedRequest,
    onDismissRequest = onDismissRequest
), init = {
    com.example.socialk.components.timepicker.SheetState(
        visible = visible,
        embedded = embedded,
        onCloseRequest = onCloseRequest,
        onFinishedRequest = onFinishedRequest,
        onDismissRequest = onDismissRequest
    )
}
)
