package com.literandltx.betime.ui.screens.activity.shared

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.literandltx.betime.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDateRangePickerDialog(
    isOpen: Boolean,
    onDismissRequest: () -> Unit,
    onDateRangeSelected: (Long?, Long?) -> Unit
) {
    if (!isOpen) return

    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    val startDate = dateRangePickerState.selectedStartDateMillis
                    val endDate = dateRangePickerState.selectedEndDateMillis ?: startDate

                    if (startDate != null) {
                        onDateRangeSelected(startDate, endDate)
                    }
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.cancelAction))
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            showModeToggle = false,
            title = null,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}
