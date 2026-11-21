package com.literandltx.betime.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlusOne
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.literandltx.betime.R
import com.literandltx.betime.data.preferences.AppThemeMode
import com.literandltx.betime.data.preferences.AppThemeModeProvider
import com.literandltx.betime.ui.theme.TimerTheme
import com.literandltx.betime.util.TimerConstants
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

enum class RadialAction(val sectorIndex: Int?) {
    TOP(0),
    RIGHT(1),
    BOTTOM(2),
    LEFT(3),
    CENTER(null),
    SINGLE_CLICK(null),
    DOUBLE_CLICK(null);

    companion object {
        fun fromIndex(index: Int?): RadialAction {
            return entries.find { it.sectorIndex == index } ?: CENTER
        }
    }
}

data class RadialMenuItem(
    val action: RadialAction,
    val content: @Composable (isSelected: Boolean) -> Unit
)

@Composable
internal fun TimerRadialMenuOverlay(
    boxCenter: Offset,
    activeSectorIndex: Int?,
    outerRadius: Float,
    innerRadius: Float,
    sectorDividerWidth: Float,
    menuItems: List<RadialMenuItem>
) {
    val totalSectors = menuItems.size
    if (totalSectors == 0) return

    val sweepAngle = 360f / totalSectors

    val backgroundColor = MaterialTheme.colorScheme.surface
    val primaryContainerColor = MaterialTheme.colorScheme.primaryContainer
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            menuItems.forEachIndexed { index, _ ->
                val startAngle = (index * sweepAngle) - 90f - (sweepAngle / 2f)
                val colorToDraw = if (activeSectorIndex == index) {
                    primaryContainerColor
                } else {
                    surfaceVariantColor
                }

                drawArc(
                    color = colorToDraw,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = Offset(boxCenter.x - outerRadius, boxCenter.y - outerRadius),
                    size = Size(outerRadius * 2, outerRadius * 2),
                    style = androidx.compose.ui.graphics.drawscope.Fill
                )
            }

            if (sectorDividerWidth > 0f) {
                for (index in 0 until totalSectors) {
                    val angleDeg = (index * sweepAngle) - 90f - (sweepAngle / 2f)
                    val rad = Math.toRadians(angleDeg.toDouble())

                    val startX = boxCenter.x + (innerRadius * cos(rad)).toFloat()
                    val startY = boxCenter.y + (innerRadius * sin(rad)).toFloat()
                    val endX = boxCenter.x + (outerRadius * cos(rad)).toFloat()
                    val endY = boxCenter.y + (outerRadius * sin(rad)).toFloat()

                    drawLine(
                        color = backgroundColor,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = sectorDividerWidth
                    )
                }
            }

            drawCircle(
                color = backgroundColor,
                radius = innerRadius,
                center = boxCenter
            )
        }

        val midRadius = (innerRadius + outerRadius) / 2f

        menuItems.forEachIndexed { index, item ->
            val angleDeg = (index * sweepAngle) - 90f
            val rad = Math.toRadians(angleDeg.toDouble())

            val x = boxCenter.x + (midRadius * cos(rad)).toFloat()
            val y = boxCenter.y + (midRadius * sin(rad)).toFloat()

            val isSelected = activeSectorIndex == index

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset((x - 24.dp.toPx()).roundToInt(), (y - 24.dp.toPx()).roundToInt())
                    }
                    .size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                item.content(isSelected)
            }
        }
    }
}

@Composable
fun TimerRadialSectorTop(isSelected: Boolean) {
    SectorContent(
        icon = Icons.Default.Add,
        label = stringResource(R.string.addMinuteTimerAction),
        isSelected = isSelected
    )
}

@Composable
fun TimerRadialSectorRight(isSelected: Boolean) {
    SectorContent(
        icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        label = stringResource(R.string.finishTimerAction),
        isSelected = isSelected
    )
}

@Composable
fun TimerRadialSectorBottom(isSelected: Boolean) {
    SectorContent(
        icon = Icons.Default.Close,
        label = stringResource(R.string.closeTimerAction),
        isSelected = isSelected
    )
}

@Composable
fun TimerRadialSectorLeft(isSelected: Boolean) {
    SectorContent(
        icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
        label = stringResource(R.string.resetTimerAction),
        isSelected = isSelected
    )
}

@Composable
fun SectorContent(
    icon: ImageVector,
    label: String,
    isSelected: Boolean
) {
    val tintColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tintColor,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            color = tintColor,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Preview(group = "Timer States", name = "Radial Menu Overlay Preview", showBackground = true, backgroundColor = 0xFF141416)
@Composable
private fun CountdownTimerRadialMenuPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier
                    .size(320.dp)
                    .padding(dimensionResource(R.dimen.padding_content_horizontal)),
                contentAlignment = Alignment.Center
            ) {
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

                TimerRadialMenuOverlay(
                    boxCenter = Offset(400f, 400f),
                    activeSectorIndex = 0,
                    outerRadius = TimerConstants.RADIAL_OUTER_RADIUS,
                    innerRadius = TimerConstants.RADIAL_INNER_RADIUS,
                    sectorDividerWidth = TimerConstants.RADIAL_DIVIDER_WIDTH,
                    menuItems = menuItems
                )
            }
        }
    }
}
