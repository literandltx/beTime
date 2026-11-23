package com.literandltx.betime.ui.screens.logs

import android.text.format.DateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.literandltx.betime.R
import com.literandltx.betime.data.model.Label
import com.literandltx.betime.data.preferences.AppThemeMode
import com.literandltx.betime.data.preferences.AppThemeModeProvider
import com.literandltx.betime.ui.shared.CloseIconButton
import com.literandltx.betime.ui.shared.ConfirmIconButton
import com.literandltx.betime.ui.shared.EditableTextField
import com.literandltx.betime.ui.theme.SmallElementShape
import com.literandltx.betime.ui.theme.TimerTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import kotlin.time.Duration.Companion.milliseconds

private const val UNSPECIFIED_TIMESTAMP = 0L

@Composable
fun EditorRecordCard(
    initialValueSeconds: Int,
    initialStartTimestamp: Long,
    initialLabelId: Int?,
    labels: List<Label>,
    onConfirm: (durationSeconds: Int, startTimestamp: Long, labelId: Int?) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    force24Hour: Boolean? = null
) {
    var textFieldValue by remember(initialValueSeconds) {
        val initialMinutes = if (initialValueSeconds > 0) (initialValueSeconds / 60).toString() else ""
        mutableStateOf(
            TextFieldValue(
                text = initialMinutes,
                selection = TextRange(initialMinutes.length)
            )
        )
    }

    var selectedTimestamp by remember(initialStartTimestamp) {
        mutableLongStateOf(initialStartTimestamp)
    }
    var selectedLabelId by remember(initialLabelId) {
        mutableStateOf(initialLabelId)
    }

    val selectedLabel = remember(labels, selectedLabelId) {
        labels.find { it.id == selectedLabelId }
    }
    val isUncertainSelected = selectedLabel?.isUncertainOnly == true

    var hasDateTime by remember(initialStartTimestamp, isUncertainSelected) {
        mutableStateOf(!isUncertainSelected || initialStartTimestamp > UNSPECIFIED_TIMESTAMP)
    }

    LaunchedEffect(isUncertainSelected) {
        if (!isUncertainSelected) {
            if (selectedTimestamp <= UNSPECIFIED_TIMESTAMP) {
                selectedTimestamp = System.currentTimeMillis()
            }
            hasDateTime = true
        }
    }

    val minsValue = textFieldValue.text.toIntOrNull() ?: 0
    val isConfirmEnabled = if (isUncertainSelected) true else minsValue > 0

    val focusRequester = remember { FocusRequester() }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val imeBottom = WindowInsets.ime.getBottom(LocalDensity.current)

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        delay(100.milliseconds)
        bringIntoViewRequester.bringIntoView()
    }

    LaunchedEffect(imeBottom) {
        if (imeBottom > 0) {
            delay(50.milliseconds)
            bringIntoViewRequester.bringIntoView()
        }
    }

    val submitForm = {
        val durationSeconds = minsValue.coerceAtLeast(0) * 60
        if (isUncertainSelected || minsValue > 0) {
            val finalTimestamp = when {
                !isUncertainSelected -> {
                    if (selectedTimestamp > UNSPECIFIED_TIMESTAMP) selectedTimestamp else System.currentTimeMillis()
                }
                !hasDateTime -> UNSPECIFIED_TIMESTAMP
                selectedTimestamp > UNSPECIFIED_TIMESTAMP -> selectedTimestamp
                else -> System.currentTimeMillis()
            }
            onConfirm(durationSeconds, finalTimestamp, selectedLabelId)
        }
    }

    val placeholder = if (isUncertainSelected) {
        stringResource(R.string.durationOptionalPlaceholder)
    } else {
        stringResource(R.string.durationPlaceholder)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester),
        shape = shape,
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(R.dimen.card_elevation)
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_content_horizontal)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_standard))
        ) {
            EditorDurationInput(
                textFieldValue = textFieldValue,
                onValueChange = { textFieldValue = it },
                placeholder = placeholder,
                isConfirmEnabled = isConfirmEnabled,
                focusRequester = focusRequester,
                onConfirm = submitForm,
                onCancel = onCancel
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                EditorDateTimePicker(
                    selectedTimestamp = selectedTimestamp,
                    isUncertain = isUncertainSelected,
                    hasDateTime = hasDateTime,
                    onDateTimeSelected = { newTs ->
                        selectedTimestamp = newTs
                        hasDateTime = true
                    },
                    onClearDateTime = {
                        hasDateTime = false
                        selectedTimestamp = UNSPECIFIED_TIMESTAMP
                    },
                    force24Hour = force24Hour,
                    modifier = Modifier.weight(1f)
                )

                EditorLabelPicker(
                    labels = labels,
                    selectedLabelId = selectedLabelId,
                    onLabelSelected = { selectedLabelId = it }
                )
            }
        }
    }
}

@Composable
private fun EditorDurationInput(
    textFieldValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String,
    isConfirmEnabled: Boolean,
    focusRequester: FocusRequester,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EditableTextField(
            value = textFieldValue,
            onValueChange = {
                if (it.text.isEmpty() || it.text.toLongOrNull() != null) {
                    onValueChange(it)
                }
            },
            placeholder = placeholder,
            cursorColor = MaterialTheme.colorScheme.primary,
            keyboardType = KeyboardType.Number,
            onConfirm = onConfirm,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
                .focusRequester(focusRequester)
        )

        Text(
            text = stringResource(R.string.timeUnitMin),
            modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_standard)),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        ConfirmIconButton(
            onClick = onConfirm,
            isEnabled = isConfirmEnabled,
            contentDescription = stringResource(R.string.confirmAction)
        )

        CloseIconButton(
            onClick = onCancel,
            contentDescription = stringResource(R.string.cancelAction)
        )
    }
}

@Composable
fun EditorDateTimePicker(
    selectedTimestamp: Long,
    isUncertain: Boolean,
    hasDateTime: Boolean,
    onDateTimeSelected: (Long) -> Unit,
    onClearDateTime: () -> Unit,
    modifier: Modifier = Modifier,
    force24Hour: Boolean? = null
) {
    val context = LocalContext.current
    val locale = LocalLocale.current
    val is24Hour = remember(context, force24Hour) {
        force24Hour ?: DateFormat.is24HourFormat(context)
    }
    val pattern = if (is24Hour) "MMM d, yyyy, HH:mm:ss" else "MMM d, yyyy, h:mm:ss a"
    val sdf = remember(locale, pattern) { SimpleDateFormat(pattern, locale.platformLocale) }

    val baseMillis = if (selectedTimestamp > UNSPECIFIED_TIMESTAMP) {
        selectedTimestamp
    } else {
        System.currentTimeMillis()
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var pendingMillis by remember { mutableLongStateOf(baseMillis) }

    val showDateTimeLabel = !isUncertain || hasDateTime
    val labelText = if (showDateTimeLabel) sdf.format(Date(baseMillis)) else stringResource(R.string.noDateTime)
    val textColor = if (showDateTimeLabel) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = labelText,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            modifier = Modifier
                .clip(SmallElementShape)
                .clickable {
                    pendingMillis = baseMillis
                    showDatePicker = true
                }
                .padding(vertical = 4.dp, horizontal = 8.dp)
        )

        if (isUncertain && hasDateTime) {
            IconButton(
                onClick = onClearDateTime,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Clear,
                    contentDescription = stringResource(R.string.clearDateTimeAction),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }

    if (showDatePicker) {
        EditorDateDialog(
            initialMillis = pendingMillis,
            onDismiss = { showDatePicker = false },
            onDateConfirmed = { updatedMillis ->
                pendingMillis = updatedMillis
                showDatePicker = false
                showTimePicker = true
            }
        )
    }

    if (showTimePicker) {
        EditorTimeDialog(
            initialMillis = pendingMillis,
            is24Hour = is24Hour,
            onDismiss = { showTimePicker = false },
            onTimeConfirmed = { finalMillis ->
                onDateTimeSelected(finalMillis)
                showTimePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorDateDialog(
    initialMillis: Long,
    onDismiss: () -> Unit,
    onDateConfirmed: (Long) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = localMillisToUtcStartOfDay(initialMillis)
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { utcMillis ->
                        onDateConfirmed(applyUtcDateToLocalMillis(initialMillis, utcMillis))
                    }
                }
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancelAction))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditorTimeDialog(
    initialMillis: Long,
    is24Hour: Boolean,
    onDismiss: () -> Unit,
    onTimeConfirmed: (Long) -> Unit
) {
    val initialCal = remember(initialMillis) {
        Calendar.getInstance().apply { timeInMillis = initialMillis }
    }
    val timePickerState = rememberTimePickerState(
        initialHour = initialCal.get(Calendar.HOUR_OF_DAY),
        initialMinute = initialCal.get(Calendar.MINUTE),
        is24Hour = is24Hour
    )
    var showKeyboardInput by remember { mutableStateOf(false) }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.selectTimeTitle),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                )

                if (showKeyboardInput) {
                    TimeInput(state = timePickerState)
                } else {
                    TimePicker(state = timePickerState)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { showKeyboardInput = !showKeyboardInput }) {
                        Icon(
                            imageVector = if (showKeyboardInput) {
                                Icons.Outlined.AccessTime
                            } else {
                                Icons.Outlined.Keyboard
                            },
                            contentDescription = stringResource(
                                if (showKeyboardInput) {
                                    R.string.switchToClockInputAction
                                } else {
                                    R.string.switchToKeyboardInputAction
                                }
                            )
                        )
                    }

                    Row {
                        TextButton(onClick = onDismiss) {
                            Text(stringResource(R.string.cancelAction))
                        }
                        TextButton(
                            onClick = {
                                onTimeConfirmed(
                                    applyTimeToLocalMillis(
                                        baseMillis = initialMillis,
                                        hour = timePickerState.hour,
                                        minute = timePickerState.minute
                                    )
                                )
                            }
                        ) {
                            Text(stringResource(R.string.ok))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditorLabelPicker(
    labels: List<Label>,
    selectedLabelId: Int?,
    onLabelSelected: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val activeLabel = labels.find { it.id == selectedLabelId }
    val noLabelText = stringResource(R.string.noLabelMessage)

    Box(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable { expanded = true }
                .padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.LocalOffer,
                contentDescription = null,
                tint = activeLabel?.composeColor ?: MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(dimensionResource(R.dimen.standard_icon_size))
            )
            Text(
                text = activeLabel?.name ?: noLabelText,
                style = MaterialTheme.typography.labelMedium,
                color = activeLabel?.composeColor ?: MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(noLabelText) },
                onClick = {
                    onLabelSelected(null)
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.LocalOffer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
            labels.forEach { label ->
                DropdownMenuItem(
                    text = { Text(label.name) },
                    onClick = {
                        onLabelSelected(label.id)
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.LocalOffer,
                            contentDescription = null,
                            tint = label.composeColor
                        )
                    }
                )
            }
        }
    }
}

private fun localMillisToUtcStartOfDay(localMillis: Long): Long {
    val localCal = Calendar.getInstance().apply { timeInMillis = localMillis }
    return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear()
        set(
            localCal.get(Calendar.YEAR),
            localCal.get(Calendar.MONTH),
            localCal.get(Calendar.DAY_OF_MONTH)
        )
    }.timeInMillis
}

private fun applyUtcDateToLocalMillis(baseLocalMillis: Long, selectedUtcMillis: Long): Long {
    val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = selectedUtcMillis
    }
    return Calendar.getInstance().apply {
        timeInMillis = baseLocalMillis
        set(Calendar.YEAR, utcCal.get(Calendar.YEAR))
        set(Calendar.MONTH, utcCal.get(Calendar.MONTH))
        set(Calendar.DAY_OF_MONTH, utcCal.get(Calendar.DAY_OF_MONTH))
    }.timeInMillis
}

private fun applyTimeToLocalMillis(baseMillis: Long, hour: Int, minute: Int): Long {
    return Calendar.getInstance().apply {
        timeInMillis = baseMillis
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private val sampleLabels = listOf(
    Label(id = 1, name = "Work & Coding", colorArgb = 0xFF6200EE.toInt(), orderIndex = 0),
    Label(id = 2, name = "English", colorArgb = 0xFF03DAC6.toInt(), orderIndex = 1),
    Label(id = 3, name = "Uncertain", colorArgb = 0xFF90A4AE.toInt(), orderIndex = 2, isUncertainOnly = true)
)

@Preview(group = "Themes", showBackground = true)
@Composable
private fun EditorRecordCardEditModePreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.padding(dimensionResource(R.dimen.padding_content_horizontal))) {
                EditorRecordCard(
                    initialValueSeconds = 2700,
                    initialStartTimestamp = System.currentTimeMillis(),
                    initialLabelId = 1,
                    labels = sampleLabels,
                    onConfirm = { _, _, _ -> },
                    onCancel = {}
                )
            }
        }
    }
}

@Preview(group = "Edge Cases", showBackground = true)
@Composable
private fun EditorRecordCardUncertainPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.padding(dimensionResource(R.dimen.padding_content_horizontal))) {
                EditorRecordCard(
                    initialValueSeconds = 0,
                    initialStartTimestamp = UNSPECIFIED_TIMESTAMP,
                    initialLabelId = 3,
                    labels = sampleLabels,
                    onConfirm = { _, _, _ -> },
                    onCancel = {}
                )
            }
        }
    }
}
