package com.literandltx.betime.ui.screens.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.literandltx.betime.R
import com.literandltx.betime.data.model.Label
import com.literandltx.betime.data.model.TimerOption
import com.literandltx.betime.data.preferences.AppThemeMode
import com.literandltx.betime.data.preferences.AppThemeModeProvider
import com.literandltx.betime.ui.AppViewModelProvider
import com.literandltx.betime.ui.screens.preset.timers.ScrollableFlowRow
import com.literandltx.betime.ui.screens.settings.SettingsViewModel
import com.literandltx.betime.ui.shared.GenericModal
import com.literandltx.betime.ui.shared.LabelIconTextButton
import com.literandltx.betime.ui.shared.TimerIconButton
import com.literandltx.betime.ui.theme.ChipShape
import com.literandltx.betime.ui.theme.TimerTheme
import com.literandltx.betime.util.DEFAULT_INITIAL_TIME_SECONDS
import com.literandltx.betime.util.SECONDS_PER_MINUTE

private enum class ModalType {
    NONE, TIMERS, LABELS
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onMenuVisibilityChanged: (Boolean) -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val uiState by homeViewModel.homeUiState.collectAsState()
    val timerState by homeViewModel.timerState.collectAsState()

    val isTimerButtonEnabled by settingsViewModel.isBottomTimerButtonEnabled.collectAsState()
    val isLabelButtonEnabled by settingsViewModel.isBottomLabelButtonEnabled.collectAsState()
    val isTimerDotEnabled by settingsViewModel.isTimerDotEnabled.collectAsState()
    val isTimerHintsEnabled by settingsViewModel.isTimerHintsEnabled.collectAsState()
    val isTimerStartIconEnabled by settingsViewModel.isTimerStartIconEnabled.collectAsState()
    val isTimerDoubleClickEnabled by settingsViewModel.isTimerDoubleClickEnabled.collectAsState()
    val isTimerSwipeBottomEnabled by settingsViewModel.isTimerSwipeBottomEnabled.collectAsState()

    val initialTimeSeconds =
        uiState.activePreset?.timerOption?.value?.times(SECONDS_PER_MINUTE)
            ?: DEFAULT_INITIAL_TIME_SECONDS

    LaunchedEffect(initialTimeSeconds) {
        homeViewModel.updateTimerDuration(initialTimeSeconds)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    homeViewModel.onAppBackgrounded()
                }
                Lifecycle.Event.ON_START -> {
                    homeViewModel.onAppForegrounded()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    HomeScreenContent(
        uiState = uiState,
        timerState = timerState,
        isTimerButtonEnabled = isTimerButtonEnabled,
        isLabelButtonEnabled = isLabelButtonEnabled,
        isTimerDotEnabled = isTimerDotEnabled,
        isTimerHintsEnabled = isTimerHintsEnabled,
        isTimerStartIconEnabled = isTimerStartIconEnabled,
        isTimerDoubleClickEnabled = isTimerDoubleClickEnabled,
        isTimerSwipeBottomEnabled = isTimerSwipeBottomEnabled,
        onTimerToggle = { homeViewModel.toggleTimer() },
        onTimerReset = { homeViewModel.resetTimer() },
        onAddMinute = { homeViewModel.addMinute() },
        onFinishSession = { homeViewModel.finishCurrentSession() },
        onLabelSelected = { homeViewModel.setAsActive(it) },
        onTimerSelected = { homeViewModel.setAsActive(it) },
        onMenuVisibilityChanged = onMenuVisibilityChanged,
        modifier = modifier
    )
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
    timerState: TimerUiState = TimerUiState(),
    isTimerButtonEnabled: Boolean = false,
    isLabelButtonEnabled: Boolean = false,
    isTimerDotEnabled: Boolean = true,
    isTimerHintsEnabled: Boolean = true,
    isTimerStartIconEnabled: Boolean = true,
    isTimerDoubleClickEnabled: Boolean = true,
    isTimerSwipeBottomEnabled: Boolean = true,
    onTimerToggle: () -> Unit = {},
    onTimerReset: () -> Unit = {},
    onAddMinute: () -> Unit = {},
    onFinishSession: () -> Unit = {},
    onLabelSelected: (Label) -> Unit = {},
    onTimerSelected: (TimerOption) -> Unit = {},
    onMenuVisibilityChanged: (Boolean) -> Unit = {}
) {
    var currentModal by remember { mutableStateOf(ModalType.NONE) }
    var isRadialMenuOpen by remember { mutableStateOf(false) }

    val activeLabel = uiState.activePreset?.label
    val activeTimer = uiState.activePreset?.timerOption
    val isModalOpen = currentModal != ModalType.NONE

    val shouldBlur = isModalOpen

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = dimensionResource(R.dimen.padding_content_horizontal))
                .blur(if (shouldBlur) 20.dp else 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CountdownTimer(
                    timeLeft = timerState.timeLeft,
                    totalTimeSeconds = timerState.totalSeconds,
                    isRunning = timerState.isRunning,
                    isDotEnabled = isTimerDotEnabled,
                    isHintsEnabled = isTimerHintsEnabled,
                    isStartIconEnabled = isTimerStartIconEnabled,
                    isDoubleClickEnabled = isTimerDoubleClickEnabled,
                    isSwipeBottomEnabled = isTimerSwipeBottomEnabled,
                    onPauseToggle = onTimerToggle,
                    onReset = onTimerReset,
                    onSwipeTop = onAddMinute,
                    onSwipeLeft = onTimerReset,
                    onSwipeRight = onFinishSession,
                    onSwipeBottom = onFinishSession,
                    onMenuVisibilityChanged = { isOpen ->
                        isRadialMenuOpen = isOpen
                        onMenuVisibilityChanged(isOpen)
                    }
                )
            }

            HomeBottomControls(
                activeLabel = activeLabel,
                isTimerButtonEnabled = isTimerButtonEnabled,
                isLabelButtonEnabled = isLabelButtonEnabled,
                onTimersClick = { currentModal = ModalType.TIMERS },
                onLabelsClick = { currentModal = ModalType.LABELS }
            )
        }

        HomeModalsProcess(
            currentModal = currentModal,
            labels = uiState.labelList,
            timers = uiState.timerOptions,
            activeLabel = activeLabel,
            activeTimer = activeTimer,
            onLabelSelected = onLabelSelected,
            onTimerSelected = onTimerSelected,
            onDismiss = { currentModal = ModalType.NONE }
        )
    }
}

@Composable
fun HomeBottomControls(
    activeLabel: Label?,
    isTimerButtonEnabled: Boolean,
    isLabelButtonEnabled: Boolean,
    onTimersClick: () -> Unit,
    onLabelsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isTimerButtonEnabled) {
            TimerIconButton(
                onClick = {
                    Log.d("TimerDebug", "Timer button clicked")
                    onTimersClick()
                }
            )
        }

        if (!isTimerButtonEnabled && isLabelButtonEnabled) {
            Spacer(modifier = Modifier.weight(1f))
        }

        if (isLabelButtonEnabled) {
            LabelIconTextButton(
                label = activeLabel,
                onClick = {
                    Log.d(
                        "TimerDebug",
                        "Label button clicked (active label: ${activeLabel?.name ?: "None"})"
                    )
                    onLabelsClick()
                }
            )
        }
    }
}

@Composable
private fun HomeModalsProcess(
    currentModal: ModalType,
    labels: List<Label>,
    timers: List<TimerOption>,
    activeLabel: Label?,
    activeTimer: TimerOption?,
    onLabelSelected: (Label) -> Unit,
    onTimerSelected: (TimerOption) -> Unit,
    onDismiss: () -> Unit
) {
    when (currentModal) {
        ModalType.LABELS -> {
            GenericModal(title = stringResource(R.string.labelsHeaderTitle), onDismissRequest = onDismiss) {
                LabelsModalContent(
                    labels = labels,
                    activeLabel = activeLabel,
                    onLabelSelected = onLabelSelected
                )
            }
        }
        ModalType.TIMERS -> {
            GenericModal(title = stringResource(R.string.timersHeaderTitle), onDismissRequest = onDismiss) {
                TimersModalContent(
                    timers = timers,
                    activeTimer = activeTimer,
                    onTimerSelected = onTimerSelected
                )
            }
        }
        ModalType.NONE -> {}
    }
}

@Composable
fun LabelsModalContent(
    labels: List<Label>,
    activeLabel: Label?,
    onLabelSelected: (Label) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollableFlowRow(
        modifier = modifier.fillMaxWidth(),
        maxRows = 2
    ) {
        labels.forEach { label ->
            SelectableChip(
                text = label.name,
                isSelected = label.id == activeLabel?.id,
                onClick = { onLabelSelected(label) },
                customBorderColor = label.composeColor
            )
        }
    }
}

@Composable
fun TimersModalContent(
    timers: List<TimerOption>,
    activeTimer: TimerOption?,
    onTimerSelected: (TimerOption) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollableFlowRow(
        modifier = modifier.fillMaxWidth(),
        maxRows = 2
    ) {
        timers.forEach { timer ->
            SelectableChip(
                text = stringResource(R.string.timerOptionFormat, timer.value),
                isSelected = timer.id == activeTimer?.id,
                onClick = { onTimerSelected(timer) },
                customBorderColor = null
            )
        }
    }
}

@Composable
fun SelectableChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    customBorderColor: Color? = null
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    val outlineColor = if (isSelected) {
        customBorderColor ?: primaryColor
    } else {
        customBorderColor ?: MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f)
    }

    val containerColor = if (isSelected) {
        (customBorderColor ?: MaterialTheme.colorScheme.primaryContainer).copy(alpha = 0.3f)
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }

    val borderColor = if (isSelected) (customBorderColor ?: primaryColor) else outlineColor
    val textColor = if (isSelected) (customBorderColor ?: primaryColor) else MaterialTheme.colorScheme.onSurface
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Card(
        onClick = onClick,
        shape = ChipShape,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = textColor
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            width = borderWidth,
            brush = SolidColor(borderColor)
        ),
        modifier = modifier.padding(dimensionResource(R.dimen.padding_small))
    ) {
        Box(
            modifier = Modifier.padding(
                horizontal = dimensionResource(R.dimen.timer_card_content_horizontal),
                vertical = dimensionResource(R.dimen.timer_card_content_vertical)
            ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            )
        }
    }
}

@Preview(name = "Home Screen", showSystemUi = true)
@Composable
fun HomeScreenPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    val mockState = HomeUiState(
        labelList = listOf(
            Label(id = 1, name = "Work", colorArgb = 0xFF4285F4.toInt()),
            Label(id = 2, name = "Study", colorArgb = 0xFFEA4335.toInt()),
            Label(id = 3, name = "Exercise", colorArgb = 0xFF34A853.toInt())
        ),
        timerOptions = listOf(
            TimerOption(id = 1, value = 5),
            TimerOption(id = 2, value = 15),
            TimerOption(id = 3, value = 25)
        )
    )

    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HomeScreenContent(
                uiState = mockState,
                isTimerButtonEnabled = true,
                isLabelButtonEnabled = true,
                onLabelSelected = {},
                onTimerSelected = {}
            )
        }
    }
}

@Preview(name = "Home Modals - Labels", showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun HomeModalsProcessLabelsFullscreenPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    val mockLabels = listOf(
        Label(id = 1, name = "Work", colorArgb = 0xFF4285F4.toInt()),
        Label(id = 2, name = "Study", colorArgb = 0xFFEA4335.toInt()),
        Label(id = 3, name = "Exercise", colorArgb = 0xFF34A853.toInt())
    )

    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HomeModalsProcess(
                currentModal = ModalType.LABELS,
                labels = mockLabels,
                timers = emptyList(),
                activeLabel = mockLabels.first(),
                activeTimer = null,
                onLabelSelected = {},
                onTimerSelected = {},
                onDismiss = {}
            )
        }
    }
}

@Preview(name = "Home Modals - Timers", showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun HomeModalsProcessTimersFullscreenPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    val mockTimers = listOf(
        TimerOption(id = 1, value = 5),
        TimerOption(id = 2, value = 15),
        TimerOption(id = 3, value = 25)
    )

    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HomeModalsProcess(
                currentModal = ModalType.TIMERS,
                labels = emptyList(),
                timers = mockTimers,
                activeLabel = null,
                activeTimer = mockTimers[2],
                onLabelSelected = {},
                onTimerSelected = {},
                onDismiss = {}
            )
        }
    }
}
