package com.literandltx.betime.ui.screens.home

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.literandltx.betime.data.model.ActivePreset
import com.literandltx.betime.data.model.ActivePresetWithDetails
import com.literandltx.betime.data.model.Label
import com.literandltx.betime.data.model.TimerOption
import com.literandltx.betime.data.model.TimerRecord
import com.literandltx.betime.data.repository.ActivePresetRepository
import com.literandltx.betime.data.repository.LabelsRepository
import com.literandltx.betime.data.repository.TimerOptionsRepository
import com.literandltx.betime.data.repository.TimerRecordRepository
import com.literandltx.betime.util.DEFAULT_INITIAL_TIME_SECONDS
import com.literandltx.betime.util.SECONDS_PER_MINUTE
import com.literandltx.betime.util.TimerConstants
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.time.Duration.Companion.milliseconds

private const val TAG = "HomeViewModel"

data class HomeUiState(
    val labelList: List<Label> = emptyList(),
    val timerOptions: List<TimerOption> = emptyList(),
    val activePreset: ActivePresetWithDetails? = null
)

data class TimerUiState(
    val totalSeconds: Int = DEFAULT_INITIAL_TIME_SECONDS,
    val timeLeft: Int = DEFAULT_INITIAL_TIME_SECONDS,
    val isRunning: Boolean = false,
    val isCustomSession: Boolean = false
)

class HomeViewModel(
    private val labelsRepository: LabelsRepository,
    private val timerOptionsRepository: TimerOptionsRepository,
    private val activePresetRepository: ActivePresetRepository,
    private val timerRecordRepository: TimerRecordRepository
) : ViewModel() {

    private var wasBackgrounded = false

    val homeUiState: StateFlow<HomeUiState> = combine(
        labelsRepository.getAllLabelsStream(),
        timerOptionsRepository.getAllTimerOption(),
        activePresetRepository.observeActivePresetWithDetails()
    ) { labels, options, activePreset ->
        Log.d(TAG, "State updated: ${labels.size} labels, ${options.size} options")

        HomeUiState(
            labelList = labels,
            timerOptions = options,
            activePreset = activePreset
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TimerConstants.SUBSCRIPTION_STOP_TIMEOUT_MS),
        initialValue = HomeUiState()
    )

    private val _timerState = MutableStateFlow(TimerUiState())
    val timerState: StateFlow<TimerUiState> = _timerState.asStateFlow()

    private var timerJob: Job? = null
    private var timerDeadline: Long? = null

    fun updateTimerDuration(duration: Int) {
        if (duration <= 0) return

        val currentState = _timerState.value

        if (currentState.isRunning || currentState.isCustomSession) {
            return
        }

        if (currentState.totalSeconds == duration) {
            return
        }

        timerJob?.cancel()
        timerJob = null
        timerDeadline = null

        _timerState.value = TimerUiState(
            totalSeconds = duration,
            timeLeft = duration,
            isRunning = false,
            isCustomSession = false
        )
        Log.d(TAG, "Timer duration updated to preset: ${duration}s")
    }

    fun toggleTimer() {
        if (_timerState.value.isRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        val currentState = _timerState.value
        if (currentState.timeLeft <= 0) return

        val initialDisplaySeconds = (currentState.timeLeft - 1).coerceAtLeast(0)

        timerDeadline = SystemClock.elapsedRealtime() + (initialDisplaySeconds * 1000L)

        _timerState.value = currentState.copy(
            timeLeft = initialDisplaySeconds,
            isRunning = true
        )

        if (initialDisplaySeconds <= 0) {
            finishTimer()
            return
        }

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val currentDeadline = timerDeadline ?: break

                val remainingMillis = currentDeadline - SystemClock.elapsedRealtime()
                val remainingSeconds = ceil(
                    remainingMillis.coerceAtLeast(0L) / 1000.0
                ).toInt()

                if (remainingSeconds <= 0) {
                    finishTimer()
                    break
                }

                _timerState.value = _timerState.value.copy(
                    timeLeft = remainingSeconds
                )

                delay(TimerConstants.TIMER_TICK_INTERVAL_MS.milliseconds)
            }
        }

        Log.d(TAG, "Timer started with ${initialDisplaySeconds}s left")
    }

    private fun pauseTimer() {
        val deadline = timerDeadline ?: return

        timerJob?.cancel()
        timerJob = null
        timerDeadline = null

        val remainingMillis = deadline - SystemClock.elapsedRealtime()
        val remainingSeconds = ceil(
            remainingMillis.coerceAtLeast(0L) / 1000.0
        ).toInt()

        if (remainingSeconds <= 0) {
            finishTimer()
            return
        }

        _timerState.value = _timerState.value.copy(
            timeLeft = remainingSeconds,
            isRunning = false
        )

        Log.d(TAG, "Timer paused with ${remainingSeconds}s left")
    }

    private fun finishTimer() {
        val completedTime = _timerState.value.totalSeconds

        timerJob?.cancel()
        timerJob = null
        timerDeadline = null

        val presetSeconds = homeUiState.value.activePreset?.timerOption?.value?.times(60)
            ?: DEFAULT_INITIAL_TIME_SECONDS

        _timerState.value = TimerUiState(
            totalSeconds = presetSeconds,
            timeLeft = presetSeconds,
            isRunning = false,
            isCustomSession = false
        )

        Log.d(TAG, "Timer finished: ${completedTime}s completed")
        if (completedTime > 0) {
            insert(completedTime)
        }
    }

    fun resetTimer() {
        timerJob?.cancel()
        timerJob = null
        timerDeadline = null

        val presetSeconds = homeUiState.value.activePreset?.timerOption?.value?.times(SECONDS_PER_MINUTE)
            ?: DEFAULT_INITIAL_TIME_SECONDS

        _timerState.value = TimerUiState(
            totalSeconds = presetSeconds,
            timeLeft = presetSeconds,
            isRunning = false,
            isCustomSession = false
        )

        Log.d(TAG, "Timer reset cleanly to preset duration: ${presetSeconds}s")
    }

    fun addMinute() {
        val currentState = _timerState.value
        val newTotal = currentState.totalSeconds + SECONDS_PER_MINUTE
        val newTimeLeft = currentState.timeLeft + SECONDS_PER_MINUTE

        if (currentState.isRunning && timerDeadline != null) {
            timerDeadline = timerDeadline!! + TimerConstants.MILLISECONDS_PER_MINUTE
        }

        _timerState.value = currentState.copy(
            totalSeconds = newTotal,
            timeLeft = newTimeLeft,
            isCustomSession = true
        )
        Log.d(TAG, "Added 1 minute: newTimeLeft=${newTimeLeft}s, newTotal=${newTotal}s")
    }

    fun finishCurrentSession() {
        val currentState = _timerState.value
        val elapsedActiveTime = currentState.totalSeconds - currentState.timeLeft

        timerJob?.cancel()
        timerJob = null
        timerDeadline = null

        val presetSeconds = homeUiState.value.activePreset?.timerOption?.value?.times(SECONDS_PER_MINUTE)
            ?: DEFAULT_INITIAL_TIME_SECONDS

        _timerState.value = TimerUiState(
            totalSeconds = presetSeconds,
            timeLeft = presetSeconds,
            isRunning = false,
            isCustomSession = false
        )

        if (elapsedActiveTime > 0) {
            insert(elapsedActiveTime)
            Log.d(TAG, "Session finished manually. Saved exactly: ${elapsedActiveTime}s")
        }
    }

    fun setAsActive(label: Label) {
        viewModelScope.launch {
            val currentPreset =
                activePresetRepository.getActivePresetWithDetails()?.activePreset
                    ?: ActivePreset()

            activePresetRepository.setActivePreset(
                currentPreset.copy(labelId = label.id)
            )
            Log.d(TAG, "Active label set: ${label.name} (id=${label.id})")
        }
    }

    fun setAsActive(timerOption: TimerOption) {
        viewModelScope.launch {
            val currentPreset =
                activePresetRepository.getActivePresetWithDetails()?.activePreset
                    ?: ActivePreset()

            _timerState.value = _timerState.value.copy(isCustomSession = false)

            activePresetRepository.setActivePreset(
                currentPreset.copy(timerOptionId = timerOption.id)
            )
            Log.d(TAG, "Active option set: ${timerOption.value}m (id=${timerOption.id})")
        }
    }

    fun insert(
        value: Int,
        startTimestamp: Long = System.currentTimeMillis() - (value * 1000L)
    ) {
        viewModelScope.launch {
            val currentLabelId =
                activePresetRepository.getActivePresetWithDetails()
                    ?.activePreset?.labelId

            val record = TimerRecord(
                value = value,
                startTimestamp = startTimestamp,
                labelId = currentLabelId
            )

            timerRecordRepository.insert(record)
            Log.d(TAG, "Record saved: value=${value}s, labelId=$currentLabelId")
        }
    }

    fun onAppBackgrounded() {
        wasBackgrounded = true
        Log.d(TAG, "App moved to background. Timer continues running.")
    }

    fun onAppForegrounded() {
        wasBackgrounded = false
        val deadline = timerDeadline

        if (_timerState.value.isRunning && deadline != null) {
            val remainingMillis = deadline - SystemClock.elapsedRealtime()
            val remainingSeconds = ceil(
                remainingMillis.coerceAtLeast(0L) / 1000.0
            ).toInt()

            _timerState.value = _timerState.value.copy(timeLeft = remainingSeconds)
            if (remainingSeconds <= 0) {
                finishTimer()
            }
        }
        Log.d(TAG, "App returned to foreground.")
    }

    override fun onCleared() {
        val currentState = _timerState.value

        if (currentState.isRunning) {
            val deadline = timerDeadline
            val elapsedTime = if (deadline != null) {
                val remainingMillis = deadline - SystemClock.elapsedRealtime()
                val remainingSeconds = ceil(
                    remainingMillis.coerceAtLeast(0L) / 1000.0
                ).toInt()
                currentState.totalSeconds - remainingSeconds
            } else {
                0
            }

            if (elapsedTime > 0) {
                kotlinx.coroutines.runBlocking {
                    val currentLabelId = activePresetRepository.getActivePresetWithDetails()?.activePreset?.labelId
                    val record = TimerRecord(
                        value = elapsedTime,
                        startTimestamp = System.currentTimeMillis() - (elapsedTime * 1000L),
                        labelId = currentLabelId
                    )
                    timerRecordRepository.insert(record)
                }
                Log.d(TAG, "App killed/cleared. Saved elapsed time: ${elapsedTime}s")
            }
        }
    }
}
