package com.literandltx.betime.ui.screens.preset.timers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.literandltx.betime.data.model.TimerOption
import com.literandltx.betime.data.preferences.AppThemeMode
import com.literandltx.betime.data.preferences.AppThemeModeProvider
import com.literandltx.betime.ui.theme.TimerTheme

@Composable
fun TimersContent(
    timerOptions: List<TimerOption>,
    activeTimerId: Int?,
    isAdding: Boolean,
    isEditing: Boolean,
    onConfirmAdd: (value: Int) -> Unit,
    onCancelAdd: () -> Unit,
    onSelectClick: (TimerOption) -> Unit,
    onDeleteClick: (TimerOption) -> Unit,
    modifier: Modifier = Modifier,
    maxRows: Int = 2
) {
    TimersList(
        state = TimersListState(
            timerOptions = timerOptions,
            activeTimerId = activeTimerId,
            isAdding = isAdding,
            isEditing = isEditing,
            maxRows = maxRows
        ),
        actions = TimersListActions(
            onConfirmAdd = onConfirmAdd,
            onCancelAdd = onCancelAdd,
            onSelectClick = onSelectClick,
            onDeleteClick = onDeleteClick
        ),
        modifier = modifier
    )
}

@Preview(name = "Light Mode - Timers Content", showBackground = true)
@Composable
fun TimersContentLightPreview() {
    val mockTimerOptions = listOf(
        TimerOption(id = 1, value = 20),
        TimerOption(id = 3, value = 45),
        TimerOption(id = 4, value = 60)
    )

    TimerTheme(themeMode = AppThemeMode.LIGHT) {
        Surface {
            TimersContent(
                timerOptions = mockTimerOptions,
                isAdding = false,
                isEditing = true,
                onConfirmAdd = { },
                onCancelAdd = { },
                activeTimerId = 3,
                onSelectClick = {},
                onDeleteClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimersContentPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    val mockTimerOptions = listOf(
        TimerOption(id = 1, value = 20),
        TimerOption(id = 3, value = 45),
        TimerOption(id = 4, value = 60)
    )

    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                TimersContent(
                    timerOptions = mockTimerOptions,
                    isAdding = false,
                    isEditing = true,
                    onConfirmAdd = { },
                    onCancelAdd = { },
                    activeTimerId = 3,
                    onSelectClick = {},
                    onDeleteClick = {}
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimerCardPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    TimerTheme(themeMode = themeMode) {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                TimerCard(
                    text = "25m",
                    isEditing = true,
                    isActive = true,
                    onSelectClick = {},
                    onDeleteClick = {}
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditorTimerCardPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                EditorTimerCard(
                    initialValue = 25,
                    placeholder = "New timer...",
                    onConfirm = { },
                    onCancel = { }
                )
            }
        }
    }
}
