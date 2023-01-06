package com.example.socialk.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialk.Destinations
import com.example.socialk.Home
import com.example.socialk.bottomTabRowScreens
import com.example.socialk.ui.theme.SocialTheme
import java.util.*
private const val InactiveTabOpacity = 0.60f
private const val TabFadeInAnimationDuration = 150
private const val TabFadeInAnimationDelay = 100
private const val TabFadeOutAnimationDuration = 100
private val TabHeight = 48.dp
@Composable
fun TopTabRow(allScreens: List<Destinations>,
              onTabSelected: (Destinations) -> Unit,
              currentScreen: Destinations
){
    Surface(modifier = Modifier
        .height(TabHeight)
        .fillMaxWidth(),
        color = Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier
            .selectableGroup()
            .background(
                color = Color.Transparent

            ), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            allScreens.forEach { screen ->
                TopTab(
                    text = screen.route,
                    imageID = screen.icon,
                    onSelected = { onTabSelected(screen) },
                    selected = currentScreen == screen
                )

            }
        }
    }
}

@Composable
fun TopTab(text: String,
              imageID: Int,
              onSelected: () -> Unit,
              selected: Boolean){
    val color = SocialTheme.colors.error
    val durationMillis = if (selected) TabFadeInAnimationDuration else TabFadeOutAnimationDuration
    //TODO :: ANIMATE THE TEXT POP UP when clicking on the icon
    val animSpec = remember {
        spring<Color>(
            stiffness = Spring.StiffnessMediumLow,
        )
    }
    val tabTintColor by animateColorAsState(
        targetValue = if (selected) SocialTheme.colors.iconInteractive else SocialTheme.colors.iconInteractiveInactive,
        animationSpec = animSpec
    )
    //to animate add to modifier .animateContentSize()
    Column(
        modifier = Modifier
            .padding(start = 16.dp, top = 16.dp, end = 16.dp)

            .height(TabHeight)
            .selectable(
                selected = selected,
                onClick = onSelected,
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = false,
                    radius = Dp.Unspecified,
                    color = Color.Unspecified
                )
            )
            .clearAndSetSemantics { contentDescription = text }, horizontalAlignment = Alignment.CenterHorizontally
    ) {

            Spacer(modifier = Modifier.height(2.dp).selectable(selected=selected, onClick = onSelected))
            Text(text.uppercase(Locale.getDefault()),
                color = tabTintColor,
                fontSize = 16.sp)

    }
}
@Preview
@Composable
fun previewTopBar(){
    SocialTheme() {
    }
}