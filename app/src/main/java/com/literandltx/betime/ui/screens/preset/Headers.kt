package com.literandltx.betime.ui.screens.preset

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
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
fun LabelsHeader(
    modifier: Modifier = Modifier,
    onStartAddClick: () -> Unit = {}
) {
    Column(modifier = modifier) {
        SectionHeader(title = stringResource(R.string.labelsHeaderTitle)) {
            AddIconButton(onClick = onStartAddClick)
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_standard)),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
fun TimersHeader(
    modifier: Modifier = Modifier,
    onStartAddClick: () -> Unit = {},
    onEditClick: () -> Unit = {}
) {
    Column(modifier = modifier) {
        SectionHeader(title = stringResource(R.string.timersHeaderTitle)) {
            EditIconButton(onClick = onEditClick)
            AddIconButton(onClick = onStartAddClick)
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_standard)),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
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

@Preview(showBackground = true)
@Composable
fun LabelsHeaderPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                LabelsHeader(
                    onStartAddClick = {},
                )
            }
        }
    }
}
