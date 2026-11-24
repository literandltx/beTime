package com.literandltx.betime.ui.screens.preset.timers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.literandltx.betime.R
import com.literandltx.betime.data.preferences.AppThemeMode
import com.literandltx.betime.data.preferences.AppThemeModeProvider
import com.literandltx.betime.ui.shared.AddIconButton
import com.literandltx.betime.ui.shared.EditIconButton
import com.literandltx.betime.ui.shared.SectionHeader
import com.literandltx.betime.ui.theme.TimerTheme

@Composable
fun TimersHeader(
    modifier: Modifier = Modifier,
    onStartAddClick: () -> Unit = {},
    onEditClick: () -> Unit = {}
) {
    SectionHeader(
        title = stringResource(R.string.timersHeaderTitle),
        modifier = modifier
    ) {
        EditIconButton(onClick = onEditClick)
        AddIconButton(onClick = onStartAddClick)
    }
}

@Preview(showBackground = true)
@Composable
fun TimersHeaderPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                TimersHeader(
                    onStartAddClick = {},
                    onEditClick = {}
                )
            }
        }
    }
}
