package com.literandltx.betime.ui.screens.preset.labels

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.literandltx.betime.R
import com.literandltx.betime.data.model.Label
import com.literandltx.betime.data.preferences.AppThemeMode
import com.literandltx.betime.data.preferences.AppThemeModeProvider
import com.literandltx.betime.ui.shared.DeleteIcon
import com.literandltx.betime.ui.shared.LabelIcon
import com.literandltx.betime.ui.shared.OptionsMenu
import com.literandltx.betime.ui.shared.ReorderHandle
import com.literandltx.betime.ui.theme.TimerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelCard(
    label: Label,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    dragModifier: Modifier = Modifier,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onToggleClick: () -> Unit = {}
) {
    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = { totalDistance -> totalDistance * 0.4f }
    )

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDeleteClick()
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier.fillMaxWidth(),
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            LabelSwipeBackground(
                dismissState = dismissState,
                shape = shape
            )
        }
    ) {
        Card(
            onClick = onToggleClick,
            modifier = Modifier.fillMaxWidth(),
            shape = shape,
            elevation = CardDefaults.cardElevation(
                defaultElevation = dimensionResource(R.dimen.card_elevation)
            ),
            border = BorderStroke(
                width = if (isActive) 0.5.dp else 0.dp,
                color = if (isActive) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                }
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimensionResource(R.dimen.padding_content_horizontal),
                        vertical = dimensionResource(R.dimen.padding_large)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ReorderHandle(modifier = dragModifier)
                LabelIcon(
                    color = label.composeColor,
                    modifier = Modifier.padding(
                        horizontal = dimensionResource(R.dimen.padding_standard)
                    )
                )
                Text(
                    text = label.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                OptionsMenu(
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LabelSwipeBackground(
    dismissState: SwipeToDismissBoxState,
    shape: Shape,
    modifier: Modifier = Modifier
) {
    val isSwiping = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart

    val backgroundColor by animateColorAsState(
        targetValue = if (isSwiping) {
            MaterialTheme.colorScheme.errorContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerLow
        },
        label = "LabelSwipeBackgroundColor"
    )

    val iconScale by animateFloatAsState(
        targetValue = if (isSwiping) 1.15f else 0.75f,
        label = "LabelSwipeIconScale"
    )

    val alphaAnim by animateFloatAsState(
        targetValue = if (isSwiping) 1f else 0.9f,
        label = "LabelSwipeIconAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(shape)
            .background(backgroundColor)
            .padding(horizontal = dimensionResource(R.dimen.padding_swipe_background)),
        contentAlignment = Alignment.CenterEnd
    ) {
        DeleteIcon(
            iconScale = iconScale,
            alphaAnim = alphaAnim
        )
    }
}

@Preview(group = "Edge Cases", showBackground = true)
@Composable
private fun LabelCardPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.padding(
                    dimensionResource(R.dimen.padding_content_horizontal)
                )
            ) {
                LabelCard(
                    label = Label(
                        id = 1,
                        name = "Work & Coding",
                        colorArgb = 0xFF6200EE.toInt(),
                        orderIndex = 0
                    ),
                    isActive = false,
                    dragModifier = Modifier
                )
            }
        }
    }
}
