package com.literandltx.betime.ui.screens.logs

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.literandltx.betime.R
import com.literandltx.betime.data.model.DateTimePickMode
import com.literandltx.betime.data.model.Label
import com.literandltx.betime.data.model.TimerRecord
import com.literandltx.betime.data.model.TimerRecordDetails
import com.literandltx.betime.data.preferences.AppThemeMode
import com.literandltx.betime.data.preferences.AppThemeModeProvider
import com.literandltx.betime.ui.AppViewModelProvider
import com.literandltx.betime.ui.shared.AppSnackbar
import com.literandltx.betime.ui.theme.TimerTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun LogsScreen(
    modifier: Modifier = Modifier,
    viewModel: LogsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.logsUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val formatter = SimpleDateFormat("yy:MM:dd:HH:mm", LocalLocale.current.platformLocale)
    val formattedDate = formatter.format(Date())

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            try {
                val csvContent = viewModel.exportCsvContent()
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(csvContent.toByteArray())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val text = inputStream.bufferedReader().use { reader -> reader.readText() }
                    coroutineScope.launch {
                        viewModel.importCsvContent(text)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LogsScreenContent(
        uiState = uiState,
        modifier = modifier,
        onAddClick = viewModel::insert,
        onEditClick = viewModel::update,
        onDeleteClick = viewModel::delete,
        onExportCsv = {
            exportLauncher.launch("betime_export_$formattedDate.csv")
        },
        onImportCsv = {
            importLauncher.launch(arrayOf("text/csv", "text/comma-separated-values", "*/*"))
        }
    )
}

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun LogsScreenContent(
    uiState: LogsUiState,
    modifier: Modifier = Modifier,
    onAddClick: (TimerRecord) -> Unit = {},
    onEditClick: (TimerRecord) -> Unit = {},
    onDeleteClick: (TimerRecord) -> Unit = {},
    onExportCsv: () -> Unit = {},
    onImportCsv: () -> Unit = {}
) {
    var isAddingRecord by remember { mutableStateOf(false) }
    var editingRecordId by remember { mutableStateOf<Int?>(null) }

    var deletedRecordIds by remember { mutableStateOf(setOf<Int>()) }

    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val deletionJobs = remember { mutableMapOf<Int, Job>() }

    val currentDeletedIds by rememberUpdatedState(deletedRecordIds)
    val currentRecords by rememberUpdatedState(uiState.records)

    DisposableEffect(Unit) {
        onDispose {
            currentDeletedIds.forEach { id ->
                currentRecords.find { it.record.id == id }?.let { recordDetails ->
                    onDeleteClick(recordDetails.record)
                }
            }
        }
    }

    val startAddAction = {
        editingRecordId = null
        isAddingRecord = true
    }

    val displayedRecords = remember(uiState.records, deletedRecordIds) {
        if (deletedRecordIds.isNotEmpty()) {
            uiState.records.filter { it.record.id !in deletedRecordIds }
        } else {
            uiState.records
        }
    }

    val recordDeletedMessage = stringResource(R.string.recordDeletedMessage)
    val undoActionLabel = stringResource(R.string.undoAction)

    val imeBottomPx = WindowInsets.ime.getBottom(LocalDensity.current)
    val adjustedImeBottomPx = (imeBottomPx * 0.78f).toInt()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    isAddingRecord = false
                    editingRecordId = null
                })
            },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                AppSnackbar(snackbarData = data)
            }
        },
        floatingActionButton = {
            if (uiState.isFabEnabled) {
                FloatingActionButton(
                    onClick = startAddAction,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }
        }
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(
                    WindowInsets(
                        bottom = adjustedImeBottomPx
                    )
                )
                .padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 12.dp,
                    bottom = 24.dp
                )
        ) {
            LogsHeader(
                showAddButton = !uiState.isFabEnabled,
                onStartAddClick = startAddAction,
                onExportCsv = onExportCsv,
                onImportCsv = onImportCsv
            )

            RecordsList(
                records = displayedRecords,
                labels = uiState.labels,
                isAdding = isAddingRecord,
                editingRecordId = editingRecordId,
                modifier = Modifier.weight(1f),
                onConfirmAdd = { durationSeconds, startTimestamp, labelId, dateTimeMode ->
                    onAddClick(
                        TimerRecord(
                            value = durationSeconds,
                            startTimestamp = startTimestamp,
                            labelId = labelId,
                            dateTimeMode = dateTimeMode
                        )
                    )
                    isAddingRecord = false
                },
                onCancelAdd = { isAddingRecord = false },
                onStartEdit = { record ->
                    isAddingRecord = false
                    editingRecordId = record.id
                },
                onConfirmEdit = { updatedRecord ->
                    onEditClick(updatedRecord)
                    editingRecordId = null
                },
                onCancelEdit = { editingRecordId = null },
                onDeleteClick = { recordToDelete ->
                    val recordId = recordToDelete.id

                    deletedRecordIds = deletedRecordIds + recordId

                    val job = coroutineScope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()

                        val result = snackbarHostState.showSnackbar(
                            message = recordDeletedMessage,
                            actionLabel = undoActionLabel,
                            duration = SnackbarDuration.Short
                        )

                        if (result == SnackbarResult.ActionPerformed) {
                            deletedRecordIds = deletedRecordIds - recordId
                        } else {
                            onDeleteClick(recordToDelete)
                            deletedRecordIds = deletedRecordIds - recordId
                        }
                        deletionJobs.remove(recordId)
                    }

                    deletionJobs[recordId]?.cancel()
                    deletionJobs[recordId] = job
                }
            )
        }
    }
}

private val sampleLabels = listOf(
    Label(id = 1, name = "Focus", colorArgb = 0xFFFF5722.toInt(), orderIndex = 0),
    Label(id = 2, name = "Break", colorArgb = 0xFF4CAF50.toInt(), orderIndex = 1),
    Label(id = 3, name = "Work & Coding", colorArgb = 0xFF6200EE.toInt(), orderIndex = 2),
    Label(id = 4, name = "English", colorArgb = 0xFF03DAC6.toInt(), orderIndex = 3)
)

private val sampleRecords = listOf(
    TimerRecordDetails(
        record = TimerRecord(
            id = 1,
            value = 1500,
            startTimestamp = System.currentTimeMillis() - 1500000,
            labelId = 1
        ),
        label = sampleLabels[0]
    ),
    TimerRecordDetails(
        record = TimerRecord(
            id = 2,
            value = 300,
            startTimestamp = System.currentTimeMillis() - 300000,
            labelId = 2
        ),
        label = sampleLabels[1]
    ),
    TimerRecordDetails(
        record = TimerRecord(
            id = 3,
            value = 2700,
            startTimestamp = System.currentTimeMillis() - 2700000,
            labelId = null
        ),
        label = null
    )
)

@Preview(showBackground = true)
@Composable
fun LogsScreenPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.padding(4.dp)) {
                LogsScreenContent(
                    uiState = LogsUiState(
                        records = sampleRecords,
                        labels = sampleLabels,
                        isFabEnabled = false
                    ),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
