package com.example.socialk.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialk.components.HomeScreenHeading
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme
import kotlin.math.roundToInt

@Composable
fun RangeScreen(onEvent:(SettingsEvent)->Unit){
    Surface(Modifier.fillMaxSize(),color=SocialTheme.colors.uiBackground) {
        var distanceInKm  by remember { mutableStateOf(5f) }
        val minValue = 5f
        val maxValue = 250f
        //slider for range pick
        Column(Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            HomeScreenHeading(onEvent = { onEvent(SettingsEvent.GoBack)}, title = "Range")
            com.example.socialk.chat.ChatComponents.Divider()
            Text(text = "Use the slider below to select the distance range in which you want to find public activities. The activities within the selected range will be displayed on the map. You can adjust the range at any time to find new activities or refine your search.")
            Spacer(modifier = Modifier.height(48.dp))
            Text(text = distanceInKm .toString()+" km",style= TextStyle(fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),color= SocialTheme.colors.textPrimary)

            Slider(modifier=Modifier.padding(horizontal = 24.dp),value = distanceInKm , onValueChange = {  newValue ->
                distanceInKm = newValue.roundToInt().coerceIn(minValue.roundToInt(), maxValue.roundToInt()).toFloat() },
                valueRange = minValue..maxValue,   steps = (maxValue - minValue).roundToInt(),)
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.End) {
                ClickableText(text = AnnotatedString("Confirm"), style = TextStyle(color=SocialTheme.colors.textPrimary,
                    fontFamily = Inter , fontWeight = FontWeight.Medium , fontSize = 14.sp
                ), onClick ={

                    //todo finish settings the range to saved preferences
                    onEvent(SettingsEvent.GoToSettings) })
            }
        }
    }

}