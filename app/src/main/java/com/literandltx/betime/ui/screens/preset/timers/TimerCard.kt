package com.literandltx.betime.ui.screens.preset.timers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.literandltx.betime.R
import com.literandltx.betime.ui.shared.DeleteIcon

@Composable
fun TimerCard(
    text: String,
    isEditing: Boolean,
    isActive: Boolean,
    onSelectClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    val outlineColor = if (isActive) {
        primaryColor
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f)
    }

    val containerColor = if (isActive) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }

    val textColor = if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .padding(
                top = dimensionResource(R.dimen.timer_card_top_padding),
                end = dimensionResource(R.dimen.timer_card_end_padding)
            )
    ) {
        TimerCardContent(
            text = text,
            isActive = isActive,
            containerColor = containerColor,
            textColor = textColor,
            shape = shape,
            onSelectClick = onSelectClick
        )

        if (isEditing) {
            TimerDeleteBadge(
                isActive = isActive,
                primaryColor = primaryColor,
                outlineColor = outlineColor,
                onDeleteClick = onDeleteClick
            )
        }
    }
}

@Composable
private fun TimerCardContent(
    text: String,
    isActive: Boolean,
    containerColor: Color,
    textColor: Color,
    shape: Shape,
    onSelectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onSelectClick,
        modifier = modifier,
        shape = shape,
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(R.dimen.card_elevation)
        ),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = textColor
        ),
        border = BorderStroke(
            width = if (isActive) 1.dp else 0.dp,
            color = if (isActive) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }
        ),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(
                    horizontal = dimensionResource(R.dimen.timer_card_content_horizontal),
                    vertical = dimensionResource(R.dimen.timer_card_content_vertical)
                )
        ) {
            Text(
                text = text,
                color = textColor,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun BoxScope.TimerDeleteBadge(
    isActive: Boolean,
    primaryColor: Color,
    outlineColor: Color,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val badgeOffset = dimensionResource(R.dimen.timer_delete_badge_offset)
    val circleColor = if (isActive) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val borderColor = if (isActive) {
        primaryColor.copy(alpha = 0.4f)
    } else {
        outlineColor
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .align(Alignment.TopEnd)
            .offset(x = badgeOffset, y = -badgeOffset)
            .size(dimensionResource(R.dimen.timer_delete_badge_size))
            .clip(CircleShape)
            .background(circleColor, CircleShape)
            .border(1.dp, borderColor, CircleShape)
            .clickable(onClick = onDeleteClick)
    ) {
        DeleteIcon(
            alphaAnim = 1f,
            contentDescription = stringResource(R.string.deleteAction),
            modifier = Modifier.padding(dimensionResource(R.dimen.timer_delete_badge_padding))
        )
    }
}
