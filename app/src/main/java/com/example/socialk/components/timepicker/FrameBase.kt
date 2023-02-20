package com.example.socialk.components.timepicker

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
/**
 * Defines module-wide values.
 */
object BaseValues {

    val CONTENT_DEFAULT_PADDING: PaddingValues
        @Composable
        get() = PaddingValues(
            start = 8.dp,
            end =8.dp,
            top =8.dp
        )
}

private const val TABLET_THRESHOLD = 800
/**
 * Determines whether the current screen should use landscape mode.
 *
 * @return `true` if the screen height is less than the [TABLET_THRESHOLD] in landscape mode, `false` otherwise.
 */
@Composable
fun shouldUseLandscape(): Boolean =
    LocalConfiguration.current.screenHeightDp < TABLET_THRESHOLD
/**
 * Base component for the content structure of a dialog.
 * @param header The content to be displayed inside the dialog that functions as the header view of the dialog.
 * @param horizontalContentPadding The horizontal padding that is applied to the content.
 * @param layout The content to be displayed inside the dialog between the header and the buttons.
 * @param layoutHorizontalAlignment The horizontal alignment of the layout's children.
 * @param layoutLandscape The content to be displayed inside the dialog between the header and the buttons when the device is in landscape mode.
 * @param layoutLandscapeVerticalAlignment The vertical alignment of the layout's children in landscape mode.
 * @param buttonsVisible Display the buttons.
 * @param buttons The content to be displayed inside the dialog that functions as the buttons view of the dialog.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FrameBase(
    header: Header? = null,
    config: TimeConfig? = null,
    horizontalContentPadding: PaddingValues = BaseValues.CONTENT_DEFAULT_PADDING,
    layout: @Composable ColumnScope.(LibOrientation) -> Unit,
    layoutHorizontalAlignment: Alignment.Horizontal = Alignment.Start,
    layoutLandscape: @Composable (RowScope.() -> Unit)? = null,
    layoutLandscapeVerticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    buttonsVisible: Boolean = true,
    buttons: @Composable (ColumnScope.() -> Unit)? = null,
) {
    val layoutDirection = LocalLayoutDirection.current
    val shouldUseLandscapeLayout = shouldUseLandscape()
    val currentOrientation = LocalConfiguration.current.orientation
    val isDeviceLandscape = currentOrientation == Configuration.ORIENTATION_LANDSCAPE
    val deviceOrientation =
        if (config?.orientation != LibOrientation.PORTRAIT && isDeviceLandscape) LibOrientation.LANDSCAPE else LibOrientation.PORTRAIT
    val layoutType = when (config?.orientation) {
        null -> {
            when {
                // Only if auto orientation is currently landscape, content for landscape exists
                // and the device screen is not larger than a typical phone.
                isDeviceLandscape
                        && layoutLandscape != null
                        && shouldUseLandscapeLayout -> LibOrientation.LANDSCAPE
                else -> LibOrientation.PORTRAIT
            }
        }
        LibOrientation.LANDSCAPE -> if (layoutLandscape != null) LibOrientation.LANDSCAPE else LibOrientation.PORTRAIT
        else -> config.orientation
    }

    Column(
        modifier = Modifier.wrapContentSize(),
        horizontalAlignment = Alignment.End
    ) {

        header?.takeUnless { deviceOrientation == LibOrientation.LANDSCAPE }?.let {
            // Display header
            Column(modifier = Modifier) {
                HeaderComponent(
                    header = header,
                    contentHorizontalPadding = PaddingValues(
                        start = horizontalContentPadding.calculateStartPadding(layoutDirection),
                        end = horizontalContentPadding.calculateEndPadding(layoutDirection),
                    )
                )
            }
        } ?: run {
            // If no header is defined, add extra spacing to the content top padding
            Spacer(
                modifier = Modifier
                    .height(8.dp)
            )
        }

        val contentModifier = Modifier
            .padding(
                PaddingValues(
                    start = horizontalContentPadding.calculateStartPadding(
                        layoutDirection
                    ),
                    end = horizontalContentPadding.calculateEndPadding(layoutDirection),
                    // Enforce default top spacing
                    top = 8.dp,
                )
            )
        when (layoutType) {
            LibOrientation.PORTRAIT -> {
                Column(
                    modifier = contentModifier,
                    horizontalAlignment = layoutHorizontalAlignment,
                    content = { layout(deviceOrientation) }
                )
            }
            LibOrientation.LANDSCAPE -> {
                Row(
                    modifier = contentModifier,
                    verticalAlignment = layoutLandscapeVerticalAlignment,
                    content = layoutLandscape!!
                )
            }
            else -> Unit
        }

        buttons?.let { buttons ->
            if (buttonsVisible) {
                Column(modifier = Modifier) {
                    buttons.invoke(this)
                }
            } else Spacer(
                modifier = Modifier
                    .height(20.dp)
            )
        }
    }
}