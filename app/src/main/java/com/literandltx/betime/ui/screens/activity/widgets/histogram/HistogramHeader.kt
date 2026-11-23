package com.literandltx.betime.ui.screens.activity.widgets.histogram

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.literandltx.betime.R
import com.literandltx.betime.data.model.Label
import com.literandltx.betime.data.preferences.AppThemeMode
import com.literandltx.betime.data.preferences.AppThemeModeProvider
import com.literandltx.betime.ui.screens.activity.shared.RangeSelectorWithTextButton
import com.literandltx.betime.ui.screens.activity.shared.TimeSpan
import com.literandltx.betime.ui.shared.LabelIconButton
import com.literandltx.betime.ui.shared.NextIconButton
import com.literandltx.betime.ui.shared.PreviousIconButton
import com.literandltx.betime.ui.theme.TimerTheme
import com.literandltx.betime.util.formatDetailedTime

@Composable
fun HistogramHeader(
    avgMinutes: Int,
    timeOffset: Int,
    selectedTimeSpan: TimeSpan,
    selectedLabels: Set<Label>,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onTitleClick: () -> Unit,
    onLabelClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrevEnabled: Boolean = true,
    isNextEnabled: Boolean = true
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        PreviousIconButton(
            onClick = onPreviousClick,
            isEnabled = isPrevEnabled,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        RangeSelectorWithTextButton(
            selectedTimeSpan = selectedTimeSpan,
            timeOffset = timeOffset,
            onClick = onTitleClick,
            text = stringResource(R.string.averageStatsFormat, formatDetailedTime(avgMinutes, context = LocalContext.current)),
            modifier = Modifier.align(Alignment.Center)
        )

        LabelIconButton(
            label = if (selectedLabels.size == 1) selectedLabels.first() else null,
            onClick = onLabelClick,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 40.dp)
        )

        NextIconButton(
            onClick = onNextClick,
            isEnabled = isNextEnabled,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChartHeaderPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    val sampleLabels = remember {
        setOf(
            Label(id = 1, name = "Work", colorArgb = 0xFFFF5252.toInt(), orderIndex = 0),
            Label(id = 2, name = "Study", colorArgb = 0xFF4CAF50.toInt(), orderIndex = 1)
        )
    }

    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .border(width = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
            ) {
                HistogramHeader(
                    timeOffset = 0,
                    selectedTimeSpan = TimeSpan.MONTH,
                    avgMinutes = 42,
                    selectedLabels = sampleLabels,
                    onPreviousClick = {},
                    onNextClick = {},
                    onTitleClick = {},
                    onLabelClick = {}
                )
            }
        }
    }
}
