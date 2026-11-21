package com.literandltx.betime.util

import com.literandltx.betime.data.model.Label
import com.literandltx.betime.data.model.TimerRecord

object CsvExporter {
    fun exportCsvContent(labels: List<Label>, records: List<TimerRecord>): String {
        val csvRows = mutableListOf<String>()

        csvRows.add("--- LABELS ---")
        csvRows.add("id,name,colorArgb,orderIndex")
        labels.forEach { l ->
            csvRows.add("${l.id},\"${l.name}\",${l.colorArgb},${l.orderIndex}")
        }

        csvRows.add("")
        csvRows.add("--- RECORDS ---")
        csvRows.add("id,value,startTimestamp,labelId")
        records.forEach { r ->
            csvRows.add("${r.id},${r.value},${r.startTimestamp},${r.labelId ?: ""}")
        }

        return csvRows.joinToString("\n")
    }
}
