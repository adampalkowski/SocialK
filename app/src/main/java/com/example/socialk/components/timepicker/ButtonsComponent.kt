package com.example.socialk.components.timepicker

import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp


/**
 * Header component of the dialog.
 * @param selection The selection configuration for the dialog.
 * @param onPositive Listener that is invoked when the positive button is clicked.
 * @param onNegative Listener that is invoked when the negative button is clicked.
 * @param onPositiveValid If the positive button is valid and therefore enabled.
 */
@ExperimentalMaterial3Api
@Composable
fun ButtonsComponent(
    selection: TimeSelection,
    onPositive: () -> Unit,
    onNegative: () -> Unit,
    onClose: () -> Unit,
    onPositiveValid: Boolean = true,
) {

    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {

        selection.extraButton?.let { extraButton ->
            SelectionButtonComponent(
                modifier = Modifier
                    .wrapContentWidth(),
                button = extraButton,
                onClick = { selection.onExtraButtonClick?.invoke() },
                testTag =null,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        selection.negativeButton?.let { negativeButton ->
            SelectionButtonComponent(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(horizontal =8.dp),
                button = negativeButton,
                onClick = { onNegative(); onClose() },
                testTag = null,
            )
        }

        SelectionButtonComponent(
            modifier = Modifier
                .wrapContentWidth(),
            button = selection.positiveButton,
            onClick = { onPositive(); onClose() },
            enabled = onPositiveValid,
            testTag = null,
        )
    }
}

/**
 * A helper component to setup a button.
 * @param modifier The modifier that is applied to the button.
 * @param button The data that is used to build this button.
 * @param onClick Listener that is invoked when the button is clicked.
 * @param enabled Controls the enabled state of this button. When false, this component will not respond to user input, and it will appear visually disabled and disabled to accessibility services.
 * @param testTag The text that is used for the test tag.
 */
@Composable
private fun SelectionButtonComponent(
    modifier: Modifier,
    button: SelectionButton,
    onClick: () -> Unit,
    enabled: Boolean = true,
    testTag: String?
) {
    val buttonContent: @Composable RowScope.() -> Unit = {
        button.icon?.let { icon ->
            IconComponent(
                modifier = Modifier
                    .size(8.dp),
                iconSource = icon,
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        when {
            button.text != null -> Text(text = button.text)
            button.textRes != null -> Text(text = stringResource(button.textRes))
            button.annotatedString != null -> Text(text = button.annotatedString)
            else -> throw IllegalStateException("Please correct your setup. The text is missing for a button.")
        }
    }

    when (button.type) {
        ButtonStyle.TEXT ->
            TextButton(
                modifier = modifier,
                onClick = onClick,
                enabled = enabled,
                content = buttonContent
            )
        ButtonStyle.FILLED ->
            Button(
                modifier = modifier,
                onClick = onClick,
                enabled = enabled,
                content = buttonContent
            )
        ButtonStyle.ELEVATED ->
            ElevatedButton(
                modifier = modifier,
                onClick = onClick,
                enabled = enabled,
                content = buttonContent
            )
        ButtonStyle.OUTLINED ->
            OutlinedButton(
                modifier = modifier,
                onClick = onClick,
                enabled = enabled,
                content = buttonContent
            )
    }

}


/**
 * Icon component that is displayed in various places in a dialog.
 * @param modifier The modifier that is applied to this icon.
 * @param iconSource The icon that is used.
 * @param tint The color that is used to tint the icon.
 * @param defaultTint The default color that is used.
 */
@Composable
fun IconComponent(
    modifier: Modifier,
    iconSource: IconSource,
    tint: Color? = null,
    defaultTint: Color? = null,
) {

    val actualTint = tint ?: iconSource.tint ?: defaultTint ?: LocalContentColor.current

    val resolvedPainterDrawableRes = iconSource.drawableRes?.let { painterResource(id = it) }
    (iconSource.painter ?: resolvedPainterDrawableRes)?.let {
        Icon(
            modifier = modifier,
            painter = it,
            contentDescription = iconSource.contentDescription,
            tint = actualTint
        )
    }

    iconSource.bitmap?.let {
        Icon(
            modifier = modifier,
            bitmap = it,
            contentDescription = iconSource.contentDescription,
            tint = actualTint
        )
    }

    iconSource.imageVector?.let {
        Icon(
            modifier = modifier,
            imageVector = it,
            contentDescription = iconSource.contentDescription,
            tint = actualTint
        )
    }

}
