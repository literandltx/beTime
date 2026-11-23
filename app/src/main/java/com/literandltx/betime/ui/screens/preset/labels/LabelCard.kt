package com.literandltx.betime.ui.screens.preset.labels

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SubdirectoryArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.literandltx.betime.R
import com.literandltx.betime.data.model.Label
import com.literandltx.betime.data.preferences.AppThemeMode
import com.literandltx.betime.data.preferences.AppThemeModeProvider
import com.literandltx.betime.ui.shared.LabelIcon
import com.literandltx.betime.ui.shared.OptionsMenu
import com.literandltx.betime.ui.shared.ReorderHandle
import com.literandltx.betime.ui.theme.TimerTheme

@Composable
fun LabelCard(
    label: Label,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    dragModifier: Modifier = Modifier,
    onAddSubLabelClick: (() -> Unit)? = null,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onToggleClick: () -> Unit = {}
) {
    val isSubLabel = label.parentId != null

    Card(
        onClick = onToggleClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = if (isSubLabel) 24.dp else 0.dp),
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
            containerColor = if (isSubLabel) {
                MaterialTheme.colorScheme.surfaceContainerLow
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            },
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.padding_content_horizontal),
                    vertical = if (isSubLabel) {
                        dimensionResource(R.dimen.padding_standard)
                    } else {
                        dimensionResource(R.dimen.padding_large)
                    }
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSubLabel) {
                Icon(
                    imageVector = Icons.Outlined.SubdirectoryArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            ReorderHandle(modifier = dragModifier)
            LabelIcon(
                color = label.composeColor,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(R.dimen.padding_standard)
                )
            )
            Text(
                text = label.name,
                style = if (isSubLabel) {
                    MaterialTheme.typography.bodyMedium
                } else {
                    MaterialTheme.typography.bodyLarge
                }
            )
            Spacer(modifier = Modifier.weight(1f))
            OptionsMenu(
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
                onAddSubLabelClick = if (!isSubLabel) onAddSubLabelClick else null
            )
        }
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
