package com.literandltx.betime.ui.screens.activity.widgets.heatmap

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.literandltx.betime.R
import com.literandltx.betime.data.preferences.AppThemeMode
import com.literandltx.betime.data.preferences.AppThemeModeProvider
import com.literandltx.betime.ui.screens.activity.shared.RangeSelectorWithTextButton
import com.literandltx.betime.ui.screens.activity.shared.TimeSpan
import com.literandltx.betime.ui.shared.NextIconButton
import com.literandltx.betime.ui.shared.PreviousIconButton
import com.literandltx.betime.ui.theme.TimerTheme

@Composable
fun HeatmapHeader(
    timeOffset: Int,
    totalCount: Int,
    unitLabel: String,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    onRangeClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrevEnabled: Boolean = true,
    isNextEnabled: Boolean = true
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        PreviousIconButton(
            onClick = onPrevClick,
            isEnabled = isPrevEnabled,
            modifier = Modifier.align(Alignment.CenterStart)
        )

        RangeSelectorWithTextButton(
            selectedTimeSpan = TimeSpan.MONTH,
            timeOffset = timeOffset,
            onClick = onRangeClick,
            text = stringResource(R.string.heatmapMonthStatsFormat, totalCount, unitLabel),
            modifier = Modifier.align(Alignment.Center)
        )

        NextIconButton(
            onClick = onNextClick,
            isEnabled = isNextEnabled,
            modifier = Modifier.align(Alignment.CenterEnd),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HeaderPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .border(width = 1.dp, color = Color.Black)
            ) {
                HeatmapHeader(
                    timeOffset = 0,
                    onPrevClick = { },
                    onNextClick = { },
                    totalCount = 3,
                    unitLabel = "records",
                    onRangeClick = { },
                )
            }
        }
    }
}
