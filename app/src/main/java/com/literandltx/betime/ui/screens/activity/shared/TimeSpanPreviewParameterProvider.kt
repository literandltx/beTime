package com.literandltx.betime.ui.screens.activity.shared

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.literandltx.betime.data.preferences.AppThemeMode

data class HeaderPreviewState(
    val themeMode: AppThemeMode,
    val timeSpan: TimeSpan
)

class HeaderPreviewProvider : PreviewParameterProvider<HeaderPreviewState> {
    override val values: Sequence<HeaderPreviewState>
        get() = sequence {
            for (theme in AppThemeMode.entries) {
                for (span in TimeSpan.entries) {
                    yield(HeaderPreviewState(themeMode = theme, timeSpan = span))
                }
            }
        }
}
