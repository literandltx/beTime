package com.literandltx.betime.ui.screens.activity.shared

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.literandltx.betime.R
import com.literandltx.betime.ui.shared.ReorderHandleIcon

@Composable
fun WidgetContainer(
    height: Dp,
    modifier: Modifier = Modifier,
    isEditing: Boolean = false,
    canMoveUp: Boolean = true,
    canMoveDown: Boolean = true,
    onMoveUp: () -> Unit = {},
    onMoveDown: () -> Unit = {},
    dragModifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val view = LocalView.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .border(
                width = 1.dp,
                color = if (isEditing) MaterialTheme.colorScheme.primary else Color.Gray,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isEditing) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ReorderHandleIcon(
                        modifier = dragModifier.padding(end = 8.dp)
                    )

                    Text(
                        text = stringResource(R.string.reorderMessage),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            onMoveUp()
                        },
                        enabled = canMoveUp
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = null,
                            tint = if (canMoveUp) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }

                    IconButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            onMoveDown()
                        },
                        enabled = canMoveDown
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = if (canMoveDown) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }

            content()
        }
    }
}
