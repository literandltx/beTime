package com.literandltx.betime.ui.screens.activity.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.literandltx.betime.data.model.Label
import com.literandltx.betime.data.preferences.AppThemeMode
import com.literandltx.betime.data.preferences.AppThemeModeProvider
import com.literandltx.betime.ui.shared.GenericModal
import com.literandltx.betime.ui.shared.SelectableChip
import com.literandltx.betime.ui.theme.TimerTheme

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

                SelectableChip(
                    text = label.name,
                    isSelected = isSelected,
                    onClick = {
                        val newSelected = if (isSelected) {
                            selectedLabels - label
                        } else {
                            selectedLabels + label
                        }
                        onLabelsChanged(newSelected)
                    },
                    borderColor = Color(label.colorArgb)
                )
            }
        }
    }
}

@Preview(name = "Activity Modals - Label Selector", showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun LabelSelectorModalFullscreenPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    val mockLabels = listOf(
        Label(id = 1, name = "Work", colorArgb = 0xFF4CAF50.toInt()),
        Label(id = 2, name = "Study", colorArgb = 0xFF2196F3.toInt()),
        Label(id = 3, name = "Exercise", colorArgb = 0xFFFF9800.toInt()),
        Label(id = 4, name = "Reading", colorArgb = 0xFF9C27B0.toInt()),
        Label(id = 5, name = "Chill", colorArgb = 0xFF00BCD4.toInt()),
        Label(id = 6, name = "Gaming", colorArgb = 0xFFE91E63.toInt()),
        Label(id = 7, name = "Side Project", colorArgb = 0xFF3F51B5.toInt()),
        Label(id = 8, name = "Meditation", colorArgb = 0xFF009688.toInt()),
        Label(id = 9, name = "Errands", colorArgb = 0xFF795548.toInt()),
        Label(id = 10, name = "Deep Focus", colorArgb = 0xFFF44336.toInt())
    )

    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LabelSelectorModal(
                isOpen = true,
                onDismissRequest = {},
                labels = mockLabels,
                selectedLabels = setOf(mockLabels[0], mockLabels[2]),
                onLabelsChanged = {}
            )
        }
    }
}
