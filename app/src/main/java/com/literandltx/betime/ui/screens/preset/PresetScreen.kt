package com.literandltx.betime.ui.screens.preset

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.literandltx.betime.R
import com.literandltx.betime.data.model.Label
import com.literandltx.betime.data.model.TimerOption
import com.literandltx.betime.data.preferences.AppThemeMode
import com.literandltx.betime.data.preferences.AppThemeModeProvider
import com.literandltx.betime.ui.AppViewModelProvider
import com.literandltx.betime.ui.screens.preset.labels.LabelsContent
import com.literandltx.betime.ui.screens.preset.labels.LabelsHeader
import com.literandltx.betime.ui.screens.preset.timers.TimersContent
import com.literandltx.betime.ui.screens.preset.timers.TimersHeader
import com.literandltx.betime.ui.shared.AppSnackbar
import com.literandltx.betime.ui.theme.TimerTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG_TIMERS = "PresetTimers"
private const val TAG_LABELS = "PresetLabels"

@Composable
fun PresetScreen(
    modifier: Modifier = Modifier,
    viewModel: PresetViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.presetUiState.collectAsStateWithLifecycle()

    PresetContent(
        labels = uiState.labelList,
        timerOptions = uiState.timerOptions,
        activeLabelId = uiState.activePreset?.label?.id,
        activeTimerId = uiState.activePreset?.timerOption?.id,

        onAddTimerClick = viewModel::insert,
        onDeleteTimerClick = viewModel::delete,
        onSelectTimerClick = viewModel::setAsActive,

        onAddLabelClick = viewModel::insert,
        onEditLabelClick = viewModel::update,
        onDeleteLabelClick = viewModel::delete,
        onSelectLabelClick = viewModel::setAsActive,
        onReorderFinished = viewModel::reorderLabels,

        modifier = modifier
    )
}

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun PresetContent(
    labels: List<Label>,
    timerOptions: List<TimerOption>,
    activeLabelId: Int?,
    activeTimerId: Int?,
    modifier: Modifier = Modifier,

    onAddTimerClick: (TimerOption) -> Unit = {},
    onDeleteTimerClick: (TimerOption) -> Unit = {},
    onSelectTimerClick: (TimerOption) -> Unit = {},

    onAddLabelClick: (Label) -> Unit = {},
    onEditLabelClick: (Label) -> Unit = {},
    onDeleteLabelClick: (Label) -> Unit = {},
    onSelectLabelClick: (Label) -> Unit = {},
    onReorderFinished: (List<Label>) -> Unit = {}
) {
    var isAddingLabel by remember { mutableStateOf(false) }
    var addingParentId by remember { mutableStateOf<Int?>(null) }
    var editingLabelId by remember { mutableStateOf<Int?>(null) }
    var isAddingTimerOption by remember { mutableStateOf(false) }
    var isEditingTimerOption by remember { mutableStateOf(false) }

    var deletedLabelIds by remember { mutableStateOf(setOf<Int>()) }

    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val deletionJobs = remember { mutableMapOf<Int, Job>() }

    val currentDeletedIds by rememberUpdatedState(deletedLabelIds)
    val currentLabels by rememberUpdatedState(labels)

    DisposableEffect(Unit) {
        onDispose {
            currentDeletedIds.forEach { id ->
                currentLabels.find { it.id == id }?.let { label ->
                    onDeleteLabelClick(label)
                }
            }
        }
    }

    val displayedLabels = remember(labels, deletedLabelIds) {
        if (deletedLabelIds.isNotEmpty()) {
            labels.filter { it.id !in deletedLabelIds }
        } else {
            labels
        }
    }

    val labelDeletedMessage = stringResource(R.string.labelDeletedMessage)
    val undoActionLabel = stringResource(R.string.undoAction)

    val imeBottomPx = WindowInsets.ime.getBottom(LocalDensity.current)
    val adjustedImeBottomPx = (imeBottomPx * 0.78f).toInt()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    Log.v(
                        "PresetScreen",
                        "Background tapped: Clearing focus and resetting add/edit states"
                    )
                    focusManager.clearFocus()
                    isAddingLabel = false
                    addingParentId = null
                    editingLabelId = null
                    isAddingTimerOption = false
                })
            },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                AppSnackbar(snackbarData = data)
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
            TimersHeader(
                onStartAddClick = {
                    Log.d(
                        TAG_TIMERS,
                        "Add Clicked: Setting isAddingTimerOption=true (Was: $isAddingTimerOption)"
                    )
                    isAddingTimerOption = true
                },
                onEditClick = {
                    Log.d(
                        TAG_TIMERS,
                        "Edit Clicked: Toggling isEditingTimerOption (New state: ${!isEditingTimerOption})"
                    )
                    isEditingTimerOption = !isEditingTimerOption
                }
            )
            TimersContent(
                timerOptions = timerOptions,
                activeTimerId = activeTimerId,
                isAdding = isAddingTimerOption,
                isEditing = isEditingTimerOption,
                onConfirmAdd = { value ->
                    Log.i(
                        TAG_TIMERS,
                        "Timer Added: $value minutes. Resetting isAddingTimerOption=false"
                    )
                    onAddTimerClick(TimerOption(value = value))
                    isAddingTimerOption = false
                },
                onCancelAdd = {
                    Log.d(TAG_TIMERS, "Add Timer Cancelled: Resetting isAddingTimerOption=false")
                    isAddingTimerOption = false
                },
                onSelectClick = { timer ->
                    Log.d(TAG_TIMERS, "Timer Selected: ${timer.value}m (ID: ${timer.id})")
                    onSelectTimerClick(timer)
                },
                onDeleteClick = { timer ->
                    Log.w(TAG_TIMERS, "Timer Deleted: ${timer.value}m (ID: ${timer.id})")
                    onDeleteTimerClick(timer)
                }
            )

            LabelsHeader(onStartAddClick = {
                Log.d(TAG_LABELS, "Add Clicked: Setting isAddingLabel=true, clearing editingLabelId")
                editingLabelId = null
                addingParentId = null
                isAddingLabel = true
            })
            LabelsContent(
                labels = displayedLabels,
                activeLabelId = activeLabelId,
                isAdding = isAddingLabel,
                addingParentId = addingParentId,
                editingLabelId = editingLabelId,
                onConfirmAdd = { name, color, parentId ->
                    Log.i(
                        TAG_LABELS,
                        "Label Added: '$name' (parentId=$parentId). Resetting add state"
                    )
                    onAddLabelClick(
                        Label(
                            name = name,
                            colorArgb = color.toArgb(),
                            parentId = parentId
                        )
                    )
                    isAddingLabel = false
                    addingParentId = null
                },
                onCancelAdd = {
                    Log.d(TAG_LABELS, "Add Label Cancelled: Resetting add state")
                    isAddingLabel = false
                    addingParentId = null
                },
                onStartAddSubLabel = { parentId ->
                    Log.d(TAG_LABELS, "Start Add SubLabel for parentId=$parentId")
                    editingLabelId = null
                    addingParentId = parentId
                    isAddingLabel = true
                },
                onStartEdit = { label ->
                    Log.d(TAG_LABELS, "Edit Label Started: '${label.name}' (ID: ${label.id})")
                    isAddingLabel = false
                    addingParentId = null
                    editingLabelId = label.id
                },
                onConfirmEdit = { updatedLabel ->
                    Log.i(
                        TAG_LABELS,
                        "Label Edited: '${updatedLabel.name}' (ID: ${updatedLabel.id}). Clearing editingLabelId"
                    )
                    onEditLabelClick(updatedLabel)
                    editingLabelId = null
                },
                onCancelEdit = {
                    Log.d(TAG_LABELS, "Edit Label Cancelled. Clearing editingLabelId")
                    editingLabelId = null
                },
                onReorderFinished = { reorderedList ->
                    Log.d(TAG_LABELS, "Labels Reordered. New order size: ${reorderedList.size}")
                    onReorderFinished(reorderedList)
                },
                onDeleteClick = { labelToDelete ->
                    Log.w(TAG_LABELS, "Label Deleted (Pending): '${labelToDelete.name}' (ID: ${labelToDelete.id})")

                    val labelId = labelToDelete.id

                    deletedLabelIds = deletedLabelIds + labelId

                    val job = coroutineScope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        val result = snackbarHostState.showSnackbar(
                            message = labelDeletedMessage,
                            actionLabel = undoActionLabel,
                            duration = SnackbarDuration.Short
                        )

                        if (result == SnackbarResult.ActionPerformed) {
                            deletedLabelIds = deletedLabelIds - labelId
                        } else {
                            onDeleteLabelClick(labelToDelete)
                            deletedLabelIds = deletedLabelIds - labelId
                        }
                        deletionJobs.remove(labelId)
                    }

                    deletionJobs[labelId]?.cancel()
                    deletionJobs[labelId] = job
                },
                onToggleClick = { label ->
                    Log.d(TAG_LABELS, "Label Selected: '${label.name}' (ID: ${label.id})")
                    onSelectLabelClick(label)
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PresetScreenPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            PresetContent(
                labels = listOf(
                    Label(id = 1, name = "Chill", colorArgb = 0xFF2979FF.toInt(), orderIndex = 0),
                    Label(id = 2, name = "Study", colorArgb = 0xFF4CAF50.toInt(), orderIndex = 1),
                    Label(id = 3, name = "Work", colorArgb = 0xFFFF5252.toInt(), orderIndex = 2),
                ),
                timerOptions = listOf(
                    TimerOption(id = 0, value = 20),
                    TimerOption(id = 1, value = 45),
                    TimerOption(id = 2, value = 60),
                ),
                activeLabelId = 2,
                activeTimerId = 1
            )
        }
    }
}
