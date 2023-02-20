package com.example.socialk.components.timepicker

/**
 * Base configs for dialog-specific configs.
 * @param icons The style of icons that are used for dialog/ view-specific icons.
 * @param orientation The orientation of the view or null for auto orientation.
 */
abstract class BaseConfigs(
    open val orientation: LibOrientation? = BaseConstants.DEFAULT_LIB_LAYOUT,
)