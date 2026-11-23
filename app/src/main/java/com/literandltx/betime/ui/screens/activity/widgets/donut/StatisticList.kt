package com.literandltx.betime.ui.screens.activity.widgets.donut

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SubdirectoryArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.literandltx.betime.R
import com.literandltx.betime.data.model.Label
import com.literandltx.betime.ui.shared.LabelIcon
import com.literandltx.betime.util.formatDetailedTime

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatsList(
    stats: Map<Label?, Int>,
    modifier: Modifier = Modifier,
    initialShowPercentage: Boolean = false
) {
    var showPercentage by remember { mutableStateOf(initialShowPercentage) }
    val sortedStats = remember(stats) {
        stats.entries.sortedByDescending { it.value }
    }

    val totalTimeAll = stats.values.sum()
    val scrollState = rememberScrollState()

    FlowRow(
        modifier = modifier
            .verticalScroll(scrollState)
            .clickable { showPercentage = !showPercentage }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        sortedStats.forEach { (label, time) ->
            StatsListItem(
                label = label,
                time = time,
                showPercentage = showPercentage,
                totalTimeAll = totalTimeAll
            )
        }
    }
}

@Composable
fun StatsListItem(
    label: Label?,
    time: Int,
    showPercentage: Boolean,
    totalTimeAll: Int,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(vertical = 2.dp)
    ) {
        if (label?.parentId != null) {
            Icon(
                imageVector = Icons.Outlined.SubdirectoryArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(14.dp)
                    .padding(end = 2.dp)
            )
        }

        LabelIcon(color = label?.composeColor)

        Spacer(modifier = Modifier.width(6.dp))

        val labelName = label?.name ?: stringResource(R.string.noLabelMessage)
        val displayValue = if (showPercentage && totalTimeAll > 0) {
            val percentage = (time.toFloat() / totalTimeAll) * 100
            if (percentage in 0.0..<0.1) {
                "$labelName: <0.1%"
            } else {
                "$labelName: %.1f%%".format(percentage)
            }
        } else {
            "$labelName: ${formatDetailedTime(time, context = LocalContext.current)}"
        }

        Text(
            text = displayValue,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
