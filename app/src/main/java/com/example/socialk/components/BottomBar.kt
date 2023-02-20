package com.example.socialk.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Space
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialk.*
import com.example.socialk.R
import com.example.socialk.ui.theme.SocialTheme
import java.util.*

private val TabHeight = 56.dp
private const val InactiveTabOpacity = 0.60f
private const val TabFadeInAnimationDuration = 150
private const val TabFadeInAnimationDelay = 100
private const val TabFadeOutAnimationDuration = 100

@Composable
fun BottomBarRow(     allScreens: List<Destinations>,
                      onTabSelected: (Destinations) -> Unit,
                      currentScreen: Destinations,transparent:Boolean){
   Surface(modifier = Modifier
       .height(TabHeight)
       .fillMaxWidth(),
       color = Color.Transparent
   ) {
       Row(modifier = Modifier
           .selectableGroup()
           .background(
                   brush = (if (transparent){ Brush.verticalGradient( listOf(Color.Transparent ,Color.Transparent ,SocialTheme.colors.uiBackground)) }else{ Brush.verticalGradient( colors = SocialTheme.colors.gradient6_1)})
           ), horizontalArrangement = Arrangement.SpaceEvenly) {
           allScreens.forEach { screen ->
               BottomTab(
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
fun BottomBarRowCustom(     allScreens: List<Destinations>,
                      onTabSelected: (Destinations) -> Unit,
                      currentScreen: Destinations){
    Surface(modifier = Modifier
        .height(TabHeight)
        .fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(modifier = Modifier
            .selectableGroup()
            .background(
                brush = Brush.verticalGradient(
                    colors = SocialTheme.colors.gradient6_1
                )

            ), horizontalArrangement = Arrangement.SpaceEvenly) {
            allScreens.forEach { screen ->
                BottomTab(
                    text = screen.route,
                    imageID = screen.icon,
                    onSelected = { onTabSelected(screen) },
                    selected = currentScreen == screen
                )
            }
            Spacer(modifier = Modifier.width(80.dp))
            SocialFab()

        }
    }
}

@Composable
fun BottomBar( onTabSelected: (Destinations) -> Unit,currentScreen: Destinations,transparent:Boolean=false){
    Box(modifier = Modifier
        .fillMaxWidth()
        .heightIn(56.dp)
        ,contentAlignment = Alignment.BottomCenter){
        BottomBarRow(allScreens = bottomTabRowScreens, onTabSelected = {screen->onTabSelected(screen)},currentScreen = currentScreen,transparent)
    }
}

@Composable
fun SocialFab(){
    Surface(modifier = Modifier.size(56.dp), color =Color(0xFFCCCEEC), shape = RoundedCornerShape(16.dp) ) {
        IconButton(onClick = { /*TODO*/ }) {
            Icon(painter = painterResource(id = R.drawable.ic_person_waving), contentDescription =null,Modifier.size(24.dp) )
        }
    }
}

@Composable
fun BottomTab(text: String,
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
            Icon(   modifier = Modifier.size(24.dp),
                painter= painterResource(id = imageID),
                contentDescription = text,
                tint = tabTintColor
            )
            if (selected) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(text.uppercase(Locale.getDefault()), color = SocialTheme.colors.iconInteractive, fontSize = 12.sp)
            }

    }
}
