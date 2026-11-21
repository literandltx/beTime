package com.literandltx.betime.data.preferences

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
    DISCORD,
    LIME
}

class AppThemeModeProvider : PreviewParameterProvider<AppThemeMode> {
    override val values: Sequence<AppThemeMode> = sequenceOf(
        AppThemeMode.LIGHT,
        AppThemeMode.DARK,
        AppThemeMode.DISCORD,
        AppThemeMode.LIME
    )
}
