package com.literandltx.betime.ui.screens.activity.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.literandltx.betime.R
import com.literandltx.betime.ui.shared.GenericModal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSelectorModal(
    isOpen: Boolean,
    onDismissRequest: () -> Unit,
    selectedTimeSpan: TimeSpan,
    onTimeSpanSelected: (TimeSpan) -> Unit,
    onCustomRangeClick: () -> Unit = {}
) {
    val quickOptions = listOf(TimeSpan.DAY, TimeSpan.WEEK, TimeSpan.MONTH, TimeSpan.ALL_TIME)

    GenericModal(
        isVisible = isOpen,
        onDismissRequest = onDismissRequest,
        title = stringResource(R.string.selectTimeRangeTitle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                quickOptions.forEachIndexed { index, timeSpan ->
                    SegmentedButton(
                        selected = timeSpan == selectedTimeSpan && selectedTimeSpan != TimeSpan.CUSTOM,
                        onClick = {
                            onTimeSpanSelected(timeSpan)
                        },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = quickOptions.size
                        ),
                        icon = {}
                    ) {
                        Text(
                            text = timeSpan.getName(),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            val isCustomSelected = selectedTimeSpan == TimeSpan.CUSTOM
            val primaryColor = MaterialTheme.colorScheme.primary
            val customBorderColor = if (isCustomSelected) primaryColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f)
            val customContainerColor = if (isCustomSelected) primaryColor.copy(alpha = 0.1f) else Color.Transparent

            OutlinedButton(
                onClick = {
                    onCustomRangeClick()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = customContainerColor,
                    contentColor = if (isCustomSelected) primaryColor else MaterialTheme.colorScheme.onSurface
                ),
                border = ButtonDefaults.outlinedButtonBorder().copy(
                    width = if (isCustomSelected) 2.dp else 1.dp,
                    brush = SolidColor(customBorderColor)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = stringResource(R.string.customRangeAction),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = stringResource(R.string.customRangeAction),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isCustomSelected) FontWeight.Bold else FontWeight.Normal
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreeOptionTimeSelectorModal(
    isOpen: Boolean,
    onDismissRequest: () -> Unit,
    selectedTimeSpan: TimeSpan,
    onTimeSpanSelected: (TimeSpan) -> Unit,
    options: List<TimeSpan>
) {
    GenericModal(
        isVisible = isOpen,
        onDismissRequest = onDismissRequest,
        title = stringResource(R.string.selectTimeRangeTitle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEachIndexed { index, timeSpan ->
                    SegmentedButton(
                        selected = timeSpan == selectedTimeSpan,
                        onClick = {
                            onTimeSpanSelected(timeSpan)
                            onDismissRequest()
                        },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size
                        ),
                        icon = {}
                    ) {
                        Text(
                            text = timeSpan.getName(),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}
