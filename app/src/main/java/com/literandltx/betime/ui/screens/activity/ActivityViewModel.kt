package com.literandltx.betime.ui.screens.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.literandltx.betime.data.model.Label
import com.literandltx.betime.data.model.TimerRecordDetails
import com.literandltx.betime.data.preferences.SettingsPreferences
import com.literandltx.betime.data.repository.LabelsRepository
import com.literandltx.betime.data.repository.TimerRecordRepository
import com.literandltx.betime.ui.screens.activity.widgets.WidgetType
import com.literandltx.betime.util.STOP_TIMEOUT_MILLIS
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class IndexedRecord(
    val id: Int,
    val startTimestampMillis: Long,
    val startHourFloat: Float,
    val durationSeconds: Int,
    val labelId: Int?,
    val label: Label?
)

data class DayActivityBucket(
    val epochDay: Long,
    val totalSeconds: Int,
    val recordCount: Int,
    val records: List<IndexedRecord>
)

data class ActivityDataIndex(
    val byDay: Map<Long, DayActivityBucket> = emptyMap(),
    val labelsById: Map<Int, Label> = emptyMap(),
    val allRecords: List<IndexedRecord> = emptyList()
)

data class ActivityUiState(
    val labels: List<Label> = emptyList(),
    val records: List<TimerRecordDetails> = emptyList(),
    val index: ActivityDataIndex = ActivityDataIndex(),
    val widgetOrder: List<WidgetType> = SettingsPreferences.DEFAULT_ACTIVITY_WIDGET_ORDER,
    val isBottomNavEnabled: Boolean = SettingsPreferences.DEFAULT_BOTTOM_NAV_ENABLED
)

class ActivityViewModel(
    labelsRepository: LabelsRepository,
    timerRecordRepository: TimerRecordRepository,
    private val settingsPreferences: SettingsPreferences
) : ViewModel() {

    val activityUiState: StateFlow<ActivityUiState> = combine(
        labelsRepository.getAllLabelsStream(),
        timerRecordRepository.getAllTimerRecords(),
        settingsPreferences.activityWidgetOrderFlow,
        settingsPreferences.bottomNavEnabledFlow
    ) { labels, records, widgetOrder, isBottomNavEnabled ->
        val index = buildActivityIndex(
            records = records,
            labels = labels,
            zoneId = ZoneId.systemDefault()
        )
        ActivityUiState(
            labels = labels,
            records = records,
            index = index,
            widgetOrder = widgetOrder,
            isBottomNavEnabled = isBottomNavEnabled
        )
    }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = ActivityUiState()
        )

    fun updateWidgetOrder(newOrder: List<WidgetType>) {
        viewModelScope.launch {
            settingsPreferences.setActivityWidgetOrder(newOrder)
        }
    }

    private fun buildActivityIndex(
        records: List<TimerRecordDetails>,
        labels: List<Label>,
        zoneId: ZoneId
    ): ActivityDataIndex {
        if (records.isEmpty()) {
            return ActivityDataIndex(
                byDay = emptyMap(),
                labelsById = labels.associateBy { it.id },
                allRecords = emptyList()
            )
        }

        val labelsById = labels.associateBy { it.id }
        val groupedByDay = HashMap<Long, MutableList<IndexedRecord>>()
        val allIndexed = ArrayList<IndexedRecord>(records.size)

        for (details in records) {
            val rec = details.record
            val zdt = Instant.ofEpochMilli(rec.startTimestamp).atZone(zoneId)
            val date: LocalDate = zdt.toLocalDate()
            val epochDay = date.toEpochDay()
            val startHourFloat = zdt.hour + (zdt.minute / 60f) + (zdt.second / 3600f)

            val resolvedLabel = details.label ?: rec.labelId?.let { labelsById[it] }
            val indexed = IndexedRecord(
                id = rec.id,
                startTimestampMillis = rec.startTimestamp,
                startHourFloat = startHourFloat,
                durationSeconds = rec.value,
                labelId = rec.labelId,
                label = resolvedLabel
            )

            allIndexed.add(indexed)
            groupedByDay.getOrPut(epochDay) { ArrayList() }.add(indexed)
        }

        val buckets = HashMap<Long, DayActivityBucket>(groupedByDay.size)
        for ((day, dayRecords) in groupedByDay) {
            var totalSec = 0
            for (r in dayRecords) {
                totalSec += r.durationSeconds
            }
            buckets[day] = DayActivityBucket(
                epochDay = day,
                totalSeconds = totalSec,
                recordCount = dayRecords.size,
                records = dayRecords
            )
        }

        return ActivityDataIndex(
            byDay = buckets,
            labelsById = labelsById,
            allRecords = allIndexed
        )
    }
}
