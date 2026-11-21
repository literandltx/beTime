package com.literandltx.betime.util

import com.literandltx.betime.data.model.Label
import com.literandltx.betime.data.model.TimerRecord

data class ImportedData(
    val labels: List<Label>,
    val records: List<TimerRecord>
)

object CsvImporter {
    private val csvSplitRegex = Regex(",(?=(?:(?:[^\"]*\"){2})*[^\"]*$)")

    fun parseCsvContent(text: String): ImportedData {
        val lines = text.split("\n")
        var currentSection = ""

        val labelsToPut = mutableListOf<Label>()
        val recordsToPut = mutableListOf<TimerRecord>()

        for (rawLine in lines) {
            val line = rawLine.trim()
            if (line.isEmpty()) continue

            if (line.startsWith("--- LABELS ---")) {
                currentSection = "LABELS"
                continue
            } else if (line.startsWith("--- RECORDS ---")) {
                currentSection = "RECORDS"
                continue
            }

            if (line.startsWith("id,")) continue

            val parts = line.split(csvSplitRegex)

            if (currentSection == "LABELS" && parts.size >= 4) {
                labelsToPut.add(
                    Label(
                        id = parts[0].toIntOrNull() ?: 0,
                        name = parts[1].replace(Regex("^\"|\"$"), ""),
                        colorArgb = parts[2].toIntOrNull() ?: 0,
                        orderIndex = parts[3].toIntOrNull() ?: 0
                    )
                )
            } else if (currentSection == "RECORDS" && parts.size >= 4) {
                recordsToPut.add(
                    TimerRecord(
                        id = parts[0].toIntOrNull() ?: 0,
                        value = parts[1].toIntOrNull() ?: 0,
                        startTimestamp = parts[2].toLongOrNull() ?: 0L,
                        labelId = parts[3].toIntOrNull()
                    )
                )
            }
        }

        return ImportedData(labels = labelsToPut, records = recordsToPut)
    }
}
