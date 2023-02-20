package com.example.socialk.components.timepicker

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.socialk.R
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.SnapOffsets
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior


/**
 * A picker component that will build up a date or time selection.
 * @param config The general configuration for the dialog.
 * @param isDate If the current picker is used for a date.
 * @param values
 * @param onValueChange The listener that is invoked when a value was selected.
 */
@Composable
internal fun PickerComponent(
    config: TimeConfig,
    isDate: Boolean,
    values: List<List<Any?>>,
    onValueChange: (UnitSelection, UnitOptionEntry) -> Unit,
) {
    val height = remember { mutableStateOf(0) }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .onGloballyPositioned { coordinates ->
                    if (height.value < coordinates.size.height) {
                        height.value = coordinates.size.height
                    }
                },
            verticalAlignment = Alignment.Bottom
        ) {
            values.forEachIndexed { index, segments ->
                segments.forEach { segment ->
                    when (segment) {
                        is String -> PickerDateCharacterComponent(text = ",")
                        is UnitSelection -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                UnitContainerComponent(
                                    config = config,
                                    unit = segment,
                                    height = height,
                                    onValueChange = { onValueChange(segment, it) },
                                )
                                if (!config.hideTimeCharacters
                                    && !isDate
                                    && index < values.lastIndex
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .clip(MaterialTheme.shapes.extraSmall),
                                        text = ":"
                                    )
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}


/**
 * The container of a unit that was found in the localized pattern.
 * It switches between the view and selection mode.
 * @param config The general configuration for the dialog view.
 * @param unit The unit of the value.
 * @param height The height of the component.
 * @param onValueChange The listener that returns the selected unit option item.
 */
@Composable
internal fun UnitContainerComponent(
    config: TimeConfig,
    unit: UnitSelection,
    height: MutableState<Int>,
    onValueChange: (UnitOptionEntry) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }
    val width = remember { mutableStateOf(0) }

    Row(verticalAlignment = Alignment.CenterVertically) {

        if (expanded.value) {
            SelectionContainerComponent(
                heightOffsetTopPadding = 8.dp,
                unit = unit,
                height = height,
                width = width,
                options = unit.options,
                onValueChange = {
                    onValueChange(it)
                    expanded.value = false
                })
        } else {
            ValueContainerComponent(
                config = config,
                unit = unit,
                width = width,
                expanded = expanded
            )
        }
    }
}


/**
 * The container view that builds up the value view. It consists of the label and the value.
 * @param config The general configuration for the dialog view.
 * @param unit The unit of the value.
 * @param width The width of the component.
 * @param expanded If the value-picker is displayed.
 */
@Composable
internal fun ValueContainerComponent(
    config: TimeConfig,
    unit: UnitSelection,
    width: MutableState<Int>,
    expanded: MutableState<Boolean>
) {
    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UnitLabel(unit = unit)
        Box {
            ValueComponent(
                unit = unit,
                width = width,
                onClick = { expanded.value = true }
            )
            if (unit.value == null) {
                ValueEmptyOverlayComponent(
                    config = config,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

/**
 * Component that is applied above the value component if no selection was made.
 * @param modifier The modifier that is applied to this component.
 */
@Composable
internal fun ValueEmptyOverlayComponent(
    config: TimeConfig,
    modifier: Modifier
) {
    Icon(
        modifier = modifier,
        painter = painterResource(id =  R.drawable.ic_more),
        contentDescription = null
    )
}
/**
 * The value component that is displayed if a selection was made.
 * @param unit The unit of the value.
 * @param width The width of the component.
 * @param onClick The listener that is invoked if this component was selected.
 */
@Composable
internal fun ValueComponent(
    unit: UnitSelection,
    width: MutableState<Int>,
    onClick: () -> Unit
) {
    Text(
        modifier = Modifier
            .onGloballyPositioned { coordinates ->
                if (width.value < coordinates.size.width) {
                    width.value = coordinates.size.width
                }
            }
            .clip(MaterialTheme.shapes.small)
            .background(
                if (unit.value != null) MaterialTheme.colorScheme.secondaryContainer
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
            .clickable { onClick() }
            .padding(8.dp),
        text = unit.value?.label
            ?: unit.value?.labelRes?.let { stringResource(id = it) }
            ?: unit.options.last().label?.map { "  " }?.joinToString(separator = "")
            ?: unit.options.last().labelRes?.let { stringResource(id = it) }
                ?.map { "  " }?.joinToString(separator = "")!!,
        style = MaterialTheme.typography.bodyLarge
    )
}

/**
 * The label that is displayed above the value unit.
 * @param unit The value unit
 */
@Composable
internal fun UnitLabel(unit: UnitSelection) {
    Text(
        modifier = Modifier.padding(bottom =8.dp),
        text = unit.placeholderRes?.let { stringResource(id = it) } ?: "",
        style = MaterialTheme.typography.labelMedium
    )
}
/**
 * The container of a selection.
 * @param unit The unit of the value.
 * @param width The width of the component.
 * @param height The height of the component.
 * @param options The list of options that the unit allows.
 * @param onValueChange The listener that returns the selected unit option item.
 * @param heightOffsetTopPadding The height offset that is applied to the top of this component.
 */
@OptIn(ExperimentalSnapperApi::class)
@Composable
internal fun SelectionContainerComponent(
    unit: UnitSelection,
    width: MutableState<Int>,
    height: MutableState<Int>,
    options: List<UnitOptionEntry>,
    onValueChange: (UnitOptionEntry) -> Unit,
    heightOffsetTopPadding: Dp
) {
    val itemHeight = remember { mutableStateOf(0) }
    val itemHeightDp = LocalDensity.current.run { itemHeight.value.toDp() }

    val animatedWidth = animateIntAsState(width.value)
    val getCurrentIndex = {
        val index = options.indexOfFirst { it.value == unit.value?.value }
        val scrollIndex = if (index >= 0) index else 0
        scrollIndex
    }

    val listState = rememberLazyListState(getCurrentIndex())
    val contentPadding = PaddingValues(0.dp, itemHeightDp)

    val behavior = rememberSnapperFlingBehavior(
        lazyListState = listState,
        snapOffsetForItem = SnapOffsets.Center,
    )
    LaunchedEffect(height.value, unit, options) {
        listState.scrollToItem(getCurrentIndex())
        // Issue: https://github.com/chrisbanes/snapper/issues/32
        listState.scroll { scrollBy(20f) }
    }

    LazyColumn(
        state = listState,
        flingBehavior = behavior,
        contentPadding = contentPadding,
        modifier = Modifier
            .height(LocalDensity.current.run { height.value.toDp() })
            .padding(top = heightOffsetTopPadding)
            .graphicsLayer { alpha = 0.99F }
            .drawWithContent {
                val colorStops = arrayOf(
                    0.0f to Color.Transparent,
                    0.3f to Color.Black,
                    0.7f to Color.Black,
                    1.0f to Color.Transparent
                )
                drawContent()
                drawRect(
                    brush = Brush.verticalGradient(*colorStops),
                    blendMode = BlendMode.DstIn
                )
            }
    ) {
        options.forEach { option ->
            item {
                SelectionValueItem(
                    modifier = Modifier
                        .widthIn(min = LocalDensity.current.run { animatedWidth.value.toDp() })
                        .onGloballyPositioned { coordinates ->
                            if (itemHeight.value < coordinates.size.height) {
                                itemHeight.value = coordinates.size.height
                            }
                        },
                    option = option,
                    onValueChange = onValueChange
                )
            }
        }
    }
}

/**
 * The value item component that can be selected.
 * @param modifier The modifier that is applied to this component.
 * @param option The option that the current component reflect.
 * @param onValueChange The listener that returns the new selection.
 */
@Composable
internal fun SelectionValueItem(
    modifier: Modifier = Modifier,
    option: UnitOptionEntry?,
    onValueChange: (UnitOptionEntry) -> Unit
) {
    Text(
        maxLines = 1,
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable { option?.let { onValueChange.invoke(it) } }
            .padding(vertical = 8.dp)
            .padding(horizontal = 8.dp),
        text = option?.labelRes?.let { stringResource(id = it) } ?: option?.label ?: "",
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyLarge
    )
}