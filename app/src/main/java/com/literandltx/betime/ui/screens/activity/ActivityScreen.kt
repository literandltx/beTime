package com.literandltx.betime.ui.screens.activity

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.literandltx.betime.R
import com.literandltx.betime.data.model.Label
import com.literandltx.betime.data.model.TimerRecord
import com.literandltx.betime.data.model.TimerRecordDetails
import com.literandltx.betime.ui.AppViewModelProvider
import com.literandltx.betime.ui.screens.activity.widgets.WidgetType
import com.literandltx.betime.ui.screens.activity.widgets.calendar.CalendarWidget
import com.literandltx.betime.ui.screens.activity.widgets.donut.DonutContainer
import com.literandltx.betime.ui.screens.activity.widgets.heatmap.HeatmapContainer
import com.literandltx.betime.ui.screens.activity.widgets.histogram.HistogramWidget
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

private const val TAG = "ActivityScreen"

@Composable
fun ActivityScreen(
    modifier: Modifier = Modifier,
    viewModel: ActivityViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState: ActivityUiState by viewModel.activityUiState.collectAsStateWithLifecycle()

    ActivityScreenContent(
        uiState = uiState,
        onReorder = { newOrder -> viewModel.updateWidgetOrder(newOrder) },
        modifier = modifier
    )
}

@Composable
fun ActivityScreenContent(
    uiState: ActivityUiState,
    modifier: Modifier = Modifier,
    onReorder: (List<WidgetType>) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current
    var isEditing by remember { mutableStateOf(false) }

    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current

    val screenHeight = with(density) { windowInfo.containerSize.height.toDp() }
    val bottomHeight = if (uiState.isBottomNavEnabled) {
        dimensionResource(R.dimen.navigation_bar_height)
    } else {
        dimensionResource(R.dimen.navigation_bar_hidden_padding)
    }

    var headerHeightPx by remember { mutableFloatStateOf(0f) }
    val headerHeight = remember(headerHeightPx) {
        if (headerHeightPx > 0f) with(density) { headerHeightPx.toDp() } else 50.dp
    }

    val availableHeight = screenHeight - headerHeight - bottomHeight - 40.dp
    val halfWidgetHeight = ((availableHeight / 2) - 8.dp).coerceAtLeast(200.dp)

    val widgetList = remember(uiState.widgetOrder) { uiState.widgetOrder.toMutableStateList() }
    LaunchedEffect(uiState.widgetOrder) {
        if (widgetList != uiState.widgetOrder) {
            widgetList.clear()
            widgetList.addAll(uiState.widgetOrder)
        }
    }

    val lazyListState = rememberLazyListState()
    val reorderableLazyColumnState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val fromIndex = from.index - 1
        val toIndex = to.index - 1
        if (fromIndex in widgetList.indices && toIndex in widgetList.indices) {
            widgetList.add(toIndex, widgetList.removeAt(fromIndex))
        }
    }

    LazyColumn(
        state = lazyListState,
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        item(key = "activity_header") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        val measuredPx = coordinates.size.height.toFloat()
                        if (headerHeightPx != measuredPx) {
                            headerHeightPx = measuredPx
                        }
                    }
            ) {
                Column {
                    ActivityHeader(onEditClick = { isEditing = !isEditing })
                }
            }
        }

        itemsIndexed(
            items = widgetList,
            key = { _, item -> item }
        ) { index, widgetType ->
            ReorderableItem(reorderableLazyColumnState, key = widgetType) { _ ->
                val canMoveUp = index > 0
                val canMoveDown = index < widgetList.size - 1

                val moveUpAction: () -> Unit = {
                    if (canMoveUp) {
                        val list = widgetList.toMutableList()
                        val item = list.removeAt(index)
                        list.add(index - 1, item)
                        onReorder(list)
                    }
                }

                val moveDownAction: () -> Unit = {
                    if (canMoveDown) {
                        val list = widgetList.toMutableList()
                        val item = list.removeAt(index)
                        list.add(index + 1, item)
                        onReorder(list)
                    }
                }

                val dragModifier = Modifier.draggableHandle(
                    onDragStopped = { onReorder(widgetList.toList()) }
                )

                when (widgetType) {
                    WidgetType.DONUT -> DonutContainer(
                        records = uiState.records,
                        labels = uiState.labels,
                        index = uiState.index,
                        height = halfWidgetHeight,
                        isEditing = isEditing,
                        canMoveUp = canMoveUp,
                        canMoveDown = canMoveDown,
                        onMoveUp = moveUpAction,
                        onMoveDown = moveDownAction,
                        dragModifier = dragModifier
                    )

                    WidgetType.CALENDAR -> CalendarWidget(
                        records = uiState.records,
                        index = uiState.index,
                        height = halfWidgetHeight,
                        isEditing = isEditing,
                        canMoveUp = canMoveUp,
                        canMoveDown = canMoveDown,
                        onMoveUp = moveUpAction,
                        onMoveDown = moveDownAction,
                        dragModifier = dragModifier
                    )

                    WidgetType.HEATMAP -> HeatmapContainer(
                        records = uiState.records,
                        index = uiState.index,
                        height = halfWidgetHeight,
                        unitLabel = "records",
                        isEditing = isEditing,
                        canMoveUp = canMoveUp,
                        canMoveDown = canMoveDown,
                        onMoveUp = moveUpAction,
                        onMoveDown = moveDownAction,
                        dragModifier = dragModifier
                    )

                    WidgetType.HISTOGRAM -> HistogramWidget(
                        records = uiState.records,
                        labels = uiState.labels,
                        index = uiState.index,
                        height = halfWidgetHeight,
                        isEditing = isEditing,
                        canMoveUp = canMoveUp,
                        canMoveDown = canMoveDown,
                        onMoveUp = moveUpAction,
                        onMoveDown = moveDownAction,
                        dragModifier = dragModifier
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ActivityScreenContentPreview() {
    ActivityScreenContent(
        uiState = ActivityUiState(
            records = listOf(
                TimerRecordDetails(
                    record = TimerRecord(
                        id = 1,
                        value = 120,
                        startTimestamp = System.currentTimeMillis() - 7200000,
                        labelId = 1
                    ),
                    label = Label(id = 1, name = "Work", colorArgb = 0xFF4285F4.toInt())
                ),
                TimerRecordDetails(
                    record = TimerRecord(
                        id = 2,
                        value = 60,
                        startTimestamp = System.currentTimeMillis() - 3600000,
                        labelId = 2
                    ),
                    label = Label(id = 2, name = "Study", colorArgb = 0xFFEA4335.toInt())
                ),
                TimerRecordDetails(
                    record = TimerRecord(
                        id = 3,
                        value = 90,
                        startTimestamp = System.currentTimeMillis(),
                        labelId = 3
                    ),
                    label = Label(id = 3, name = "Exercise", colorArgb = 0xFF34A853.toInt())
                )
            ),
            labels = listOf(
                Label(id = 1, name = "Work", colorArgb = 0xFF4285F4.toInt()),
                Label(id = 2, name = "Study", colorArgb = 0xFFEA4335.toInt()),
                Label(id = 3, name = "Exercise", colorArgb = 0xFF34A853.toInt())
            )
        )
    )
}
