package com.example.socialk.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.socialk.R

private val Montserrat = FontFamily(
    Font(R.font.montserrat_light, FontWeight.Light),
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold)
)
public val Inter = FontFamily(
    Font(R.font.inter_light, FontWeight.Light),
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_bold, FontWeight.SemiBold)
)

private val Karla = FontFamily(
    Font(R.font.karla_regular, FontWeight.Normal),
    Font(R.font.karla_bold, FontWeight.Bold)
)

// Set of Material typography styles to start with

val Typography =Typography(
    h1 = TextStyle(
        fontFamily = Inter,
        fontSize = 96.sp,
        fontWeight = FontWeight.Light,

    ),
    h2 = TextStyle(
        fontFamily = Inter,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
    ),
    h3 = TextStyle(
        fontFamily = Inter,
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,

    ),
    h4 = TextStyle(
        fontFamily = Inter,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
    ),
    h5 = TextStyle(
        fontFamily = Inter,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
    ),
    h6 = TextStyle(
        fontFamily = Inter,
        fontSize = 12.sp,
        fontWeight = FontWeight.ExtraLight,
    ),
    subtitle1 = TextStyle(
        fontFamily = Inter,
        fontSize = 10.sp,
        fontWeight = FontWeight.Light,

    ),
    subtitle2 = TextStyle(
        fontFamily = Karla,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,

    ),
    body1 = TextStyle(
        fontFamily = Inter,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,

    ),
    body2 = TextStyle(
        fontFamily = Inter,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,

    ),
    button = TextStyle(
        fontFamily = Inter,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
    ),
    caption = TextStyle(
        fontFamily = Karla,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,

    ),
    overline = TextStyle(
        fontFamily = Montserrat,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,

    )
)
