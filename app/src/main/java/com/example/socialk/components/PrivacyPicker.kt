package com.example.socialk.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.socialk.R
import com.example.socialk.ui.theme.Inter
import com.example.socialk.ui.theme.SocialTheme

enum class PrivacyOption(val label: String, @DrawableRes val iconResId: Int) {
    PUBLIC("Public", R.drawable.ic_public),
    FRIENDS_ONLY("Friends Only", R.drawable.ic_handshake),
    PRIVATE("Private", R.drawable.ic_lock_closed)
}
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PrivacyPicker(
    modifier: Modifier = Modifier,
    selectedPrivacy: PrivacyOption,
    onPrivacySelected: (PrivacyOption) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        PrivacyOption.values().forEach { privacyOption ->
            val isSelected = privacyOption == selectedPrivacy
            Card(
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, color = if (isSelected) SocialTheme.colors.iconInteractive else  SocialTheme.colors.uiFloated ),
                elevation = 0.dp,
                backgroundColor = if (isSelected) SocialTheme.colors.iconInteractive else SocialTheme.colors.uiBackground,
                onClick = {onPrivacySelected(privacyOption) }
            ) {
                Box(
                    Modifier
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = privacyOption.iconResId),
                            contentDescription = null,
                            tint = if (isSelected) Color.White else SocialTheme.colors.iconPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = privacyOption.label,
                            style = TextStyle(
                                fontFamily = Inter,
                                fontWeight = FontWeight.Light,
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White else SocialTheme.colors.iconPrimary
                            )
                        )
                    }
                }
            }
        }
    }
}
