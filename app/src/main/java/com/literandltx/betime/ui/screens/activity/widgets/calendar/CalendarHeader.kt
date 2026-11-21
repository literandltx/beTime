package com.literandltx.betime.ui.screens.activity.widgets.calendar

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.literandltx.betime.ui.screens.activity.shared.HeaderPreviewProvider
import com.literandltx.betime.ui.screens.activity.shared.HeaderPreviewState
import com.literandltx.betime.ui.screens.activity.shared.RangeSelectorButton
import com.literandltx.betime.ui.screens.activity.shared.TimeSpan
import com.literandltx.betime.ui.shared.NextIconButton
import com.literandltx.betime.ui.shared.PreviousIconButton
import com.literandltx.betime.ui.theme.TimerTheme

@Composable
fun CalendarHeader(
    timeOffset: Int,
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
            modifier = Modifier.align(Alignment.CenterStart),
        )

        RangeSelectorButton(
            selectedTimeSpan = TimeSpan.WEEK,
            timeOffset = timeOffset,
            onClick = onRangeClick,
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
    @PreviewParameter(HeaderPreviewProvider::class) state: HeaderPreviewState
) {
    TimerTheme(themeMode = state.themeMode) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .border(width = 1.dp, color = Color.Black)
            ) {
                CalendarHeader(
                    timeOffset = 0,
                    onPrevClick = { },
                    onNextClick = { },
                    onRangeClick = { },
                )
            }
        }
    }
}
