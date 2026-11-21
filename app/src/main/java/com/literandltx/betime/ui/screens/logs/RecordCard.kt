package com.literandltx.betime.ui.screens.logs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.literandltx.betime.R
import com.literandltx.betime.data.model.Label
import com.literandltx.betime.data.model.TimerRecord
import com.literandltx.betime.data.model.TimerRecordDetails
import com.literandltx.betime.data.preferences.AppThemeMode
import com.literandltx.betime.data.preferences.AppThemeModeProvider
import com.literandltx.betime.ui.shared.LabelIcon
import com.literandltx.betime.ui.shared.OptionsMenu
import com.literandltx.betime.ui.theme.TimerTheme
import com.literandltx.betime.util.formatTime
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun RecordCard(
    timerRecordDetails: TimerRecordDetails,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(R.dimen.card_elevation)
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(R.dimen.padding_content_horizontal),
                    vertical = dimensionResource(R.dimen.padding_large)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RecordCardInfo(
                timerRecordDetails = timerRecordDetails,
                modifier = Modifier.weight(1f)
            )
            OptionsMenu(
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick
            )
        }
    }
}

@Composable
private fun RecordCardInfo(
    timerRecordDetails: TimerRecordDetails,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_standard))
        ) {
            Text(
                text = stringResource(
                    R.string.recordDurationFormat,
                    formatTime(timerRecordDetails.record.value)
                ),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )

            RecordLabelBadge(label = timerRecordDetails.label)
        }

        FormattedTimerText(
            startTimestamp = timerRecordDetails.record.startTimestamp
        )
    }
}

@Composable
private fun RecordLabelBadge(
    label: Label?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
    ) {
        LabelIcon(color = label?.composeColor)
        Text(
            text = label?.name ?: stringResource(R.string.noLabelMessage),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun FormattedTimerText(
    startTimestamp: Long,
    modifier: Modifier = Modifier
) {
    val locale = LocalLocale.current

    val sdf = remember(locale) { SimpleDateFormat("MMM d, yyyy, h:mm:ss a", locale.platformLocale) }
    val formattedTime = remember(startTimestamp, locale) { sdf.format(Date(startTimestamp)) }

    Text(
        text = formattedTime,
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
    )
}

@Preview(group = "Themes", showBackground = true)
@Composable
private fun RecordCardPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    val sampleDetails = TimerRecordDetails(
        record = TimerRecord(
            id = 1,
            value = 145,
            startTimestamp = System.currentTimeMillis(),
            labelId = 1
        ),
        label = Label(
            id = 1,
            name = "Work & Coding",
            colorArgb = 0xFF6200EE.toInt(),
            orderIndex = 0
        )
    )

    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.padding(dimensionResource(R.dimen.padding_content_horizontal))) {
                RecordCard(
                    timerRecordDetails = sampleDetails,
                    onEditClick = {},
                    onDeleteClick = {}
                )
            }
        }
    }
}

@Preview(group = "Edge Cases", showBackground = true)
@Composable
private fun RecordCardNoLabelPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    val sampleDetailsWithoutLabel = TimerRecordDetails(
        record = TimerRecord(
            id = 2,
            value = 1115,
            startTimestamp = System.currentTimeMillis() - 3600000L,
            labelId = null
        ),
        label = null
    )

    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.padding(dimensionResource(R.dimen.padding_content_horizontal))) {
                RecordCard(timerRecordDetails = sampleDetailsWithoutLabel)
            }
        }
    }
}
