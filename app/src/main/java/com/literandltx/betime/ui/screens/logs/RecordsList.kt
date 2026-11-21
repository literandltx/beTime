package com.literandltx.betime.ui.screens.logs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.literandltx.betime.R
import com.literandltx.betime.data.model.Label
import com.literandltx.betime.data.model.TimerRecord
import com.literandltx.betime.data.model.TimerRecordDetails
import com.literandltx.betime.data.preferences.AppThemeMode
import com.literandltx.betime.data.preferences.AppThemeModeProvider
import com.literandltx.betime.ui.theme.TimerTheme

@Composable
fun RecordsList(
    records: List<TimerRecordDetails>,
    labels: List<Label>,
    isAdding: Boolean,
    editingRecordId: Int?,
    onConfirmAdd: (durationSeconds: Int, startTimestamp: Long, labelId: Int?) -> Unit,
    onCancelAdd: () -> Unit,
    onStartEdit: (TimerRecord) -> Unit,
    onConfirmEdit: (TimerRecord) -> Unit,
    onCancelEdit: () -> Unit,
    onDeleteClick: (TimerRecord) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isAdding) {
            item(key = "add_record_form") {
                EditorRecordCard(
                    initialValueSeconds = 0,
                    initialStartTimestamp = System.currentTimeMillis(),
                    initialLabelId = null,
                    labels = labels,
                    onConfirm = onConfirmAdd,
                    onCancel = onCancelAdd
                )
            }
        }

        if (records.isEmpty() && !isAdding) {
            item(key = "empty_state") {
                Text(
                    text = stringResource(R.string.noRecordsMessage),
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(32.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }

        items(
            items = records,
            key = { it.record.id }
        ) { item ->
            if (editingRecordId == item.record.id) {
                EditorRecordCard(
                    initialValueSeconds = item.record.value,
                    initialStartTimestamp = item.record.startTimestamp,
                    initialLabelId = item.record.labelId,
                    labels = labels,
                    onConfirm = { durationSeconds, startTimestamp, labelId ->
                        onConfirmEdit(
                            item.record.copy(
                                value = durationSeconds,
                                startTimestamp = startTimestamp,
                                labelId = labelId
                            )
                        )
                    },
                    onCancel = onCancelEdit
                )
            } else {
                RecordCard(
                    timerRecordDetails = item,
                    onEditClick = { onStartEdit(item.record) },
                    onDeleteClick = { onDeleteClick(item.record) },
                )
            }
        }
    }
}

private val sampleLabels = listOf(
    Label(id = 1, name = "Work & Coding", colorArgb = 0xFF6200EE.toInt(), orderIndex = 0),
    Label(id = 2, name = "English", colorArgb = 0xFF03DAC6.toInt(), orderIndex = 1)
)

private val sampleRecords = listOf(
    TimerRecordDetails(
        record = TimerRecord(id = 1, value = 2700, startTimestamp = System.currentTimeMillis(), labelId = 1),
        label = sampleLabels[0]
    ),
    TimerRecordDetails(
        record = TimerRecord(id = 2, value = 1800, startTimestamp = System.currentTimeMillis() - 86400000L, labelId = 2),
        label = sampleLabels[1]
    ),
    TimerRecordDetails(
        record = TimerRecord(id = 3, value = 900, startTimestamp = System.currentTimeMillis() - 172800000L, labelId = null),
        label = null
    )
)

@Preview(group = "List States", showBackground = true)
@Composable
private fun RecordsListPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                RecordsList(
                    records = sampleRecords,
                    labels = sampleLabels,
                    isAdding = false,
                    editingRecordId = null,
                    onConfirmAdd = { _, _, _ -> },
                    onCancelAdd = {},
                    onStartEdit = {},
                    onConfirmEdit = {},
                    onCancelEdit = {},
                    onDeleteClick = {}
                )
            }
        }
    }
}

@Preview(group = "List States", showBackground = true)
@Composable
private fun RecordsListEditingPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                RecordsList(
                    records = sampleRecords,
                    labels = sampleLabels,
                    isAdding = true,
                    editingRecordId = 2,
                    onConfirmAdd = { _, _, _ -> },
                    onCancelAdd = {},
                    onStartEdit = {},
                    onConfirmEdit = {},
                    onCancelEdit = {},
                    onDeleteClick = {}
                )
            }
        }
    }
}

@Preview(group = "List States", showBackground = true)
@Composable
private fun RecordsListEmptyPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                RecordsList(
                    records = emptyList(),
                    labels = sampleLabels,
                    isAdding = false,
                    editingRecordId = null,
                    onConfirmAdd = { _, _, _ -> },
                    onCancelAdd = {},
                    onStartEdit = {},
                    onConfirmEdit = {},
                    onCancelEdit = {},
                    onDeleteClick = {}
                )
            }
        }
    }
}
