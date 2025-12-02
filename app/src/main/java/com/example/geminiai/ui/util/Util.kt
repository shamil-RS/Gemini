package com.example.geminiai.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.iconSetting(background: Color = Color.Transparent): Modifier =
    this then Modifier
        .size(45.dp)
        .aspectRatio(1f)
        .clip(CircleShape)
        .background(background)
        .border(1.dp, Color.Gray.copy(alpha = 0.5f), CircleShape)

@Composable
fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier =
    this then Modifier.clickable(
        interactionSource = MutableInteractionSource(),
        indication = null,
        onClick = { onClick() }
    )

@Composable
fun CustomIcons(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription: String = "",
    verticalPadding: Dp = 0.dp,
    horizontalPadding: Dp = 0.dp,
    tint: Color = Color.White
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier.padding(
            vertical = verticalPadding,
            horizontal = horizontalPadding
        ),
        tint = tint
    )
}

@Composable
fun CustomIconWithTextButton(
    imageVector: ImageVector,
    text: String,
    textColor: Color = Color.White,
    tint: Color = Color.White,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable { onClick() },
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {},
            content = {
                Icon(
                    imageVector = imageVector,
                    contentDescription = "",
                    tint = tint
                )
            }
        )
        Text(text = text, color = textColor)
    }
}

@Composable
fun CustomIconButton(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
    onClick: () -> Unit
) {


}
