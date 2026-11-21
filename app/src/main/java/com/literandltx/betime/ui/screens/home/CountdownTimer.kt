package com.literandltx.betime.ui.screens.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.literandltx.betime.R
import com.literandltx.betime.data.preferences.AppThemeMode
import com.literandltx.betime.data.preferences.AppThemeModeProvider
import com.literandltx.betime.ui.shared.HourglassIcon
import com.literandltx.betime.ui.theme.TimerTheme
import com.literandltx.betime.util.TimerConstants
import com.literandltx.betime.util.formatTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun CountdownTimer(
    timeLeft: Int,
    totalTimeSeconds: Int,
    modifier: Modifier = Modifier,
    isRunning: Boolean = false,
    isDotEnabled: Boolean = true,
    isHintsEnabled: Boolean = true,
    isStartIconEnabled: Boolean = true,
    isDoubleClickEnabled: Boolean = true,
    isSwipeBottomEnabled: Boolean = true,
    onPauseToggle: () -> Unit = {},
    onReset: () -> Unit = {},
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {},
    onSwipeTop: () -> Unit = {},
    onSwipeBottom: () -> Unit = {},
    onMenuVisibilityChanged: (Boolean) -> Unit = {}
) {
    val scaleAnim = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()

    val currentIsStartIconEnabled by rememberUpdatedState(isStartIconEnabled)
    var showStartIcon by remember { mutableStateOf(false) }
    val timerStartIndicatorIconAlpha = remember { Animatable(0f) }

    var isMenuVisible by remember { mutableStateOf(false) }
    var boxCenter by remember { mutableStateOf(Offset.Zero) }
    var activeSectorIndex by remember { mutableStateOf<Int?>(null) }
    var currentSectorIndex by remember { mutableStateOf<Int?>(null) }

    val menuItems = listOf(
        RadialMenuItem(RadialAction.TOP) { isSelected ->
            TimerRadialSectorTop(isSelected = isSelected)
        },
        RadialMenuItem(RadialAction.RIGHT) { isSelected ->
            TimerRadialSectorRight(isSelected = isSelected)
        },
        RadialMenuItem(RadialAction.BOTTOM) { isSelected ->
            TimerRadialSectorBottom(isSelected = isSelected)
        },
        RadialMenuItem(RadialAction.LEFT) { isSelected ->
            TimerRadialSectorLeft(isSelected = isSelected)
        }
    )
    val totalSectors = menuItems.size

    val progressAnim = remember { Animatable(if (totalTimeSeconds > 0) timeLeft.toFloat() / totalTimeSeconds.toFloat() else 1f) }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            val currentFraction = if (totalTimeSeconds > 0) {
                timeLeft.toFloat() / totalTimeSeconds.toFloat()
            } else {
                0f
            }
            progressAnim.snapTo(currentFraction)

            val duration = timeLeft * 1000L
            if (duration > 0) {
                progressAnim.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = duration.toInt(),
                        easing = LinearEasing
                    )
                )
            }
        } else {
            progressAnim.stop()
        }
    }

    LaunchedEffect(timeLeft, totalTimeSeconds) {
        if (!isRunning) {
            val targetProgress = if (totalTimeSeconds > 0) timeLeft.toFloat() / totalTimeSeconds.toFloat() else 1f
            if (timeLeft <= 0 && totalTimeSeconds > 0) {
                progressAnim.snapTo(0f)
                progressAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = TimerConstants.TIMER_FINISH_SWEEP_DURATION_MS,
                        easing = LinearEasing
                    )
                )
            } else {
                progressAnim.snapTo(targetProgress)
            }
        }
    }

    LaunchedEffect(isMenuVisible) {
        onMenuVisibilityChanged(isMenuVisible)
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                boxCenter = Offset(size.width / 2f, size.height / 2f)
            },
        contentAlignment = Alignment.Center
    ) {
        val dynamicSize = minOf(maxWidth, maxHeight) * 0.8f
        Box(
            modifier = Modifier
                .size(dynamicSize)
                .pointerInput(totalSectors, isDoubleClickEnabled, isSwipeBottomEnabled) {
                    var lastTapTime = 0L
                    val touchSlop = 12.dp.toPx()
                    val swipeDownThreshold = 48.dp.toPx()
                    val localCenter = Offset(size.width / 2f, size.height / 2f)

                    awaitPointerEventScope {
                        while (true) {
                            val downEvent = awaitFirstDown()
                            val downPosition = downEvent.position
                            val longPressTimeout = TimerConstants.LONG_PRESS_TIMEOUT_MS

                            var releasedBeforeLongPress = false
                            var currentPosition = downPosition
                            var hasMovedSignificantly = false

                            val longPressOccurred = withTimeoutOrNull(longPressTimeout) {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val change = event.changes.firstOrNull() ?: break
                                    currentPosition = change.position

                                    if (hypot(currentPosition.x - downPosition.x, currentPosition.y - downPosition.y) > touchSlop) {
                                        hasMovedSignificantly = true
                                    }

                                    if (!change.pressed) {
                                        releasedBeforeLongPress = true
                                        break
                                    }
                                }
                                releasedBeforeLongPress
                            }

                            if (longPressOccurred == null) {
                                isMenuVisible = true
                                currentSectorIndex = null
                                activeSectorIndex = null

                                val released = try {
                                    while (true) {
                                        val event = awaitPointerEvent()
                                        val change = event.changes.firstOrNull()

                                        if (change != null) {
                                            currentPosition = change.position
                                            val dx = currentPosition.x - localCenter.x
                                            val dy = currentPosition.y - localCenter.y
                                            val distance = hypot(dx, dy)

                                            if (distance > TimerConstants.RADIAL_DEADZONE_RADIUS) {
                                                var angle = atan2(dy, dx) * (180f / Math.PI)

                                                angle += 90.0
                                                if (angle < 0) {
                                                    angle += 360.0
                                                }

                                                angle = (angle + 45.0) % 360.0

                                                val sweepAngle = 360.0 / totalSectors
                                                val calculatedIndex = (angle / sweepAngle).toInt() % totalSectors

                                                currentSectorIndex = calculatedIndex
                                                activeSectorIndex = currentSectorIndex
                                            } else {
                                                currentSectorIndex = null
                                                activeSectorIndex = null
                                            }
                                        }

                                        if (!event.changes.any { it.pressed }) break
                                    }
                                    true
                                } catch (_: Exception) {
                                    false
                                }

                                if (released) {
                                    when (RadialAction.fromIndex(currentSectorIndex)) {
                                        RadialAction.TOP -> onSwipeTop()
                                        RadialAction.RIGHT -> onSwipeRight()
                                        RadialAction.BOTTOM -> {}
                                        RadialAction.LEFT -> onSwipeLeft()
                                        else -> {}
                                    }
                                }

                                isMenuVisible = false
                                activeSectorIndex = null

                            } else if (releasedBeforeLongPress) {
                                if (hasMovedSignificantly) {
                                    val dx = currentPosition.x - downPosition.x
                                    val dy = currentPosition.y - downPosition.y

                                    if (isSwipeBottomEnabled && dy > swipeDownThreshold && dy > abs(dx)) {
                                        coroutineScope.launch {
                                            scaleAnim.animateTo(
                                                targetValue = TimerConstants.DOUBLE_CLICK_SCALE_TARGET,
                                                animationSpec = tween(
                                                    durationMillis = TimerConstants.DOUBLE_CLICK_SCALE_IN_DURATION_MS,
                                                    easing = FastOutLinearInEasing
                                                )
                                            )
                                            scaleAnim.animateTo(
                                                targetValue = 1f,
                                                animationSpec = tween(
                                                    durationMillis = TimerConstants.DOUBLE_CLICK_SCALE_OUT_DURATION_MS,
                                                    easing = LinearEasing
                                                )
                                            )
                                        }
                                        onSwipeBottom()
                                        lastTapTime = 0L
                                    }
                                    continue
                                }

                                if (!isDoubleClickEnabled) {
                                    coroutineScope.launch {
                                        scaleAnim.animateTo(
                                            targetValue = TimerConstants.CLICK_SCALE_IN_TARGET,
                                            animationSpec = tween(
                                                durationMillis = TimerConstants.CLICK_SCALE_ANIMATION_IN_DURATION_MS,
                                                easing = FastOutLinearInEasing
                                            )
                                        )
                                        scaleAnim.animateTo(
                                            targetValue = 1f,
                                            animationSpec = tween(
                                                durationMillis = TimerConstants.CLICK_SCALE_ANIMATION_OUT_DURATION_MS,
                                                easing = LinearEasing
                                            )
                                        )
                                    }

                                    if (currentIsStartIconEnabled) {
                                        showStartIcon = true
                                        coroutineScope.launch {
                                            timerStartIndicatorIconAlpha.snapTo(1f)
                                            delay(TimerConstants.TIMER_ICON_VISIBLE_DURATION_MS.milliseconds)
                                            timerStartIndicatorIconAlpha.animateTo(
                                                targetValue = 0f,
                                                animationSpec = tween(durationMillis = TimerConstants.TIMER_ICON_FADE_OUT_DURATION_MS, easing = LinearEasing)
                                            )
                                            showStartIcon = false
                                        }
                                    }

                                    onPauseToggle()
                                } else {
                                    val now = System.currentTimeMillis()
                                    if (now - lastTapTime < TimerConstants.DOUBLE_CLICK_TIMEOUT_MS) {
                                        coroutineScope.launch {
                                            scaleAnim.animateTo(
                                                targetValue = TimerConstants.DOUBLE_CLICK_SCALE_TARGET,
                                                animationSpec = tween(durationMillis = TimerConstants.DOUBLE_CLICK_SCALE_IN_DURATION_MS, easing = FastOutLinearInEasing)
                                            )
                                            scaleAnim.animateTo(
                                                targetValue = 1f,
                                                animationSpec = tween(durationMillis = TimerConstants.DOUBLE_CLICK_SCALE_OUT_DURATION_MS, easing = LinearEasing)
                                            )
                                        }
                                        onReset()
                                        lastTapTime = 0L
                                    } else {
                                        lastTapTime = now
                                        val secondDown = withTimeoutOrNull(TimerConstants.DOUBLE_CLICK_TIMEOUT_MS) {
                                            awaitFirstDown(false)
                                        }
                                        if (secondDown != null) {
                                            coroutineScope.launch {
                                                scaleAnim.animateTo(
                                                    targetValue = TimerConstants.DOUBLE_CLICK_SCALE_TARGET,
                                                    animationSpec = tween(durationMillis = TimerConstants.DOUBLE_CLICK_SCALE_IN_DURATION_MS, easing = FastOutLinearInEasing)
                                                )
                                                scaleAnim.animateTo(
                                                    targetValue = 1f,
                                                    animationSpec = tween(durationMillis = TimerConstants.DOUBLE_CLICK_SCALE_OUT_DURATION_MS, easing = LinearEasing)
                                                )
                                            }
                                            onSwipeRight()
                                            lastTapTime = 0L
                                            try {
                                                withTimeoutOrNull(TimerConstants.DOUBLE_CLICK_TIMEOUT_MS) {
                                                    while (true) {
                                                        val event = awaitPointerEvent()
                                                        if (!event.changes.any { it.pressed }) break
                                                    }
                                                }
                                            } catch (_: Exception) {
                                            }
                                        } else {
                                            coroutineScope.launch {
                                                scaleAnim.animateTo(
                                                    targetValue = TimerConstants.CLICK_SCALE_IN_TARGET,
                                                    animationSpec = tween(
                                                        durationMillis = TimerConstants.CLICK_SCALE_ANIMATION_IN_DURATION_MS,
                                                        easing = FastOutLinearInEasing
                                                    )
                                                )
                                                scaleAnim.animateTo(
                                                    targetValue = 1f,
                                                    animationSpec = tween(
                                                        durationMillis = TimerConstants.CLICK_SCALE_ANIMATION_OUT_DURATION_MS,
                                                        easing = LinearEasing
                                                    )
                                                )
                                            }

                                            if (currentIsStartIconEnabled) {
                                                showStartIcon = true
                                                coroutineScope.launch {
                                                    timerStartIndicatorIconAlpha.snapTo(1f)
                                                    delay(TimerConstants.TIMER_ICON_VISIBLE_DURATION_MS.milliseconds)
                                                    timerStartIndicatorIconAlpha.animateTo(
                                                        targetValue = 0f,
                                                        animationSpec = tween(durationMillis = TimerConstants.TIMER_ICON_FADE_OUT_DURATION_MS, easing = LinearEasing)
                                                    )
                                                    showStartIcon = false
                                                }
                                            }

                                            onPauseToggle()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                .blur(if (isMenuVisible) TimerConstants.TIMER_BLUR_RADIUS_DP.dp else 0.dp),
            contentAlignment = Alignment.Center
        ) {
            TimerProgressRing(
                animatedProgress = progressAnim.value,
                isRunning = isRunning,
                isDotEnabled = isDotEnabled,
                timeLeft = timeLeft,
                totalTimeSeconds = totalTimeSeconds
            )

            TimerDisplay(
                timeLeft = timeLeft,
                scale = scaleAnim.value,
                isRunning = isRunning,
                isNotActive = timeLeft == totalTimeSeconds || timeLeft <= 0,
                isHintsEnabled = isHintsEnabled,
                isMenuVisible = isMenuVisible
            )
        }

        TimerStartIndicator(
            isVisible = isStartIconEnabled && showStartIcon,
            alpha = timerStartIndicatorIconAlpha.value
        )

        if (isMenuVisible && boxCenter != Offset.Zero) {
            TimerRadialMenuOverlay(
                boxCenter = boxCenter,
                activeSectorIndex = activeSectorIndex,
                outerRadius = TimerConstants.RADIAL_OUTER_RADIUS,
                innerRadius = TimerConstants.RADIAL_INNER_RADIUS,
                sectorDividerWidth = TimerConstants.RADIAL_DIVIDER_WIDTH,
                menuItems = menuItems
            )
        }
    }
}

@Composable
internal fun TimerDisplay(
    timeLeft: Int,
    scale: Float,
    isRunning: Boolean,
    isNotActive: Boolean,
    isHintsEnabled: Boolean,
    isMenuVisible: Boolean,
    modifier: Modifier = Modifier
) {
    val isPaused = !isRunning && !isNotActive
    val isReallyNotActive = !isRunning && (isNotActive || timeLeft == 0)

    val infiniteTransition = rememberInfiniteTransition(label = "PauseAlphaTransition")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = TimerConstants.PAUSE_PULSE_DURATION_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PauseAlphaAnim"
    )

    val currentAlpha = if (isMenuVisible) 0f else if (isPaused) alphaAnim else 1f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.graphicsLayer(
            scaleX = scale,
            scaleY = scale
        )
    ) {
        Text(
            text = formatTime(totalSeconds = timeLeft),
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Light
            ),
            modifier = Modifier.alpha(currentAlpha)
        )

        if (isHintsEnabled) {
            val hintText = when {
                isReallyNotActive -> stringResource(R.string.clickToStartTimerHint)
                isRunning -> stringResource(R.string.clickToPauseTimerHint)
                else -> stringResource(R.string.clickToResumeTimerHint)
            }

            Text(
                text = hintText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(top = dimensionResource(R.dimen.padding_small))
                    .alpha(currentAlpha)
            )
        }
    }
}

@Composable
internal fun TimerProgressRing(
    animatedProgress: Float,
    isRunning: Boolean,
    isDotEnabled: Boolean,
    timeLeft: Int,
    totalTimeSeconds: Int,
    modifier: Modifier = Modifier
) {
    val trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    val progressColor = MaterialTheme.colorScheme.primary
    val activeDotColor = MaterialTheme.colorScheme.primary
    val pausedDotColor = Color(0xFFE91E63)

    val isNotActiveOrPaused = !isRunning && (timeLeft == totalTimeSeconds || timeLeft <= 0)

    Canvas(
        modifier = modifier
            .size(260.dp)
            .padding(12.dp)
    ) {
        val strokeWidth = 6.dp.toPx()
        val diameter = size.minDimension
        val radius = (diameter - strokeWidth) / 2f

        drawCircle(
            color = trackColor,
            radius = radius,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )

        val elapsedFraction = 1f - animatedProgress
        val startAngle = -90f + (360f * elapsedFraction)
        val sweepAngle = 360f * animatedProgress

        drawArc(
            color = progressColor,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )

        if (isDotEnabled && !isNotActiveOrPaused) {
            val currentAngleRad = Math.toRadians(startAngle.toDouble())
            val centerX = center.x
            val centerY = center.y

            val dotRadius = radius + (strokeWidth * 0.5f)

            val dotX = centerX + (dotRadius * cos(currentAngleRad)).toFloat()
            val dotY = centerY + (dotRadius * sin(currentAngleRad)).toFloat()

            val dotColor = if (isRunning) activeDotColor else pausedDotColor

            drawCircle(
                color = dotColor,
                radius = strokeWidth,
                center = Offset(dotX, dotY)
            )
        }
    }
}

@Composable
internal fun TimerStartIndicator(
    isVisible: Boolean,
    alpha: Float,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.TopCenter
        ) {
            HourglassIcon(
                color = MaterialTheme.colorScheme.primary,
                contentDescription = null,
                modifier = Modifier
                    .offset(y = (-60).dp)
                    .alpha(alpha)
            )
        }
    }
}

@Preview(group = "Timer States", name = "Default State", showBackground = true)
@Composable
private fun CountdownTimerDefaultPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_content_horizontal)),
                contentAlignment = Alignment.Center
            ) {
                CountdownTimer(
                    timeLeft = 1500,
                    totalTimeSeconds = 1800
                )
            }
        }
    }
}
