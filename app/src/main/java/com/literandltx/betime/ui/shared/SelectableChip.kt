package com.literandltx.betime.ui.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SelectableChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color? = null,
    contentHorizontalPadding: Dp = 16.dp
) {
    val baseColor = borderColor ?: MaterialTheme.colorScheme.primary

    val outlineColor = if (isSelected) baseColor else baseColor.copy(alpha = 0.5f)
    val containerColor = if (isSelected) {
        baseColor.copy(alpha = 0.3f)
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }
    val textColor = if (isSelected) baseColor else MaterialTheme.colorScheme.onSurface
    val borderWidth = if (isSelected) 2.dp else 1.dp
    val shape = RoundedCornerShape(8.dp)

    Card(
        onClick = onClick,
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = textColor
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            width = borderWidth,
            brush = SolidColor(outlineColor)
        ),
        modifier = modifier.padding(horizontal = 4.dp, vertical = 0.dp)
    ) {
        Box(
            modifier = Modifier.padding(
                horizontal = contentHorizontalPadding,
                vertical = 12.dp
            ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}