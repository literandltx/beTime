package com.literandltx.betime.ui.screens.activity.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.literandltx.betime.R
import com.literandltx.betime.data.model.Label
import com.literandltx.betime.ui.shared.GenericModal
import kotlin.collections.forEach

@Composable
fun LabelSelectorModal(
    isOpen: Boolean,
    onDismissRequest: () -> Unit,
    labels: List<Label>,
    selectedLabels: Set<Label>,
    onLabelsChanged: (Set<Label>) -> Unit
) {
    GenericModal(
        isVisible = isOpen,
        onDismissRequest = onDismissRequest,
        title = "Labels"
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            labels.forEach { label ->
                val isSelected = selectedLabels.contains(label)
                val labelColor = Color(label.colorArgb)

                val outlineColor = if (isSelected) labelColor else labelColor.copy(alpha = 0.5f)
                val containerColor = if (isSelected) labelColor.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceContainer
                val textColor = if (isSelected) labelColor else MaterialTheme.colorScheme.onSurface
                val borderWidth = if (isSelected) 2.dp else 1.dp
                val shape = RoundedCornerShape(8.dp)

                Card(
                    onClick = {
                        val newSelected = if (isSelected) {
                            selectedLabels - label
                        } else {
                            selectedLabels + label
                        }
                        onLabelsChanged(newSelected)
                    },
                    shape = shape,
                    colors = CardDefaults.cardColors(
                        containerColor = containerColor,
                        contentColor = textColor
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        width = borderWidth,
                        brush = SolidColor(outlineColor)
                    ),
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                ) {
                    Box(
                        modifier = Modifier.padding(
                            horizontal = dimensionResource(R.dimen.timer_card_content_horizontal),
                            vertical = dimensionResource(R.dimen.timer_card_content_vertical)
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label.name,
                            color = textColor,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
