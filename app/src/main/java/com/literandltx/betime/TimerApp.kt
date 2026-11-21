package com.literandltx.betime

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.literandltx.betime.ui.AppViewModelProvider
import com.literandltx.betime.ui.screens.activity.ActivityScreen
import com.literandltx.betime.ui.screens.home.HomeScreen
import com.literandltx.betime.ui.screens.logs.LogsScreen
import com.literandltx.betime.ui.screens.preset.PresetScreen
import com.literandltx.betime.ui.screens.settings.SettingsScreen
import com.literandltx.betime.ui.screens.settings.SettingsViewModel
import com.literandltx.betime.ui.screens.TimerScreen
import com.literandltx.betime.ui.theme.TimerTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimerApp(
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val isOverscrollEnabled = false
    val screens = TimerScreen.entries

    val pagerState = rememberPagerState(
        initialPage = TimerScreen.Home.ordinal,
        pageCount = { screens.size }
    )

    val coroutineScope = rememberCoroutineScope()
    val isBottomNavEnabled by viewModel.isBottomNavEnabled.collectAsState()

    var isRadialMenuOpen by remember { mutableStateOf(false) }

    val overscrollFactory = if (isOverscrollEnabled) {
        LocalOverscrollFactory.current
    } else {
        null
    }

    Scaffold(
        bottomBar = {
            if (isBottomNavEnabled) {
                TimerBottomBar(
                    screens = screens,
                    selectedIndex = pagerState.currentPage,
                    onTabSelected = { index ->
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        CompositionLocalProvider(
            LocalOverscrollFactory provides overscrollFactory
        ) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = !isRadialMenuOpen,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) { page ->
                when (screens[page]) {
                    TimerScreen.Settings -> SettingsScreen()
                    TimerScreen.Preset -> PresetScreen()
                    TimerScreen.Home -> HomeScreen(
                        onMenuVisibilityChanged = { isOpen ->
                            isRadialMenuOpen = isOpen
                        }
                    )
                    TimerScreen.Activity -> ActivityScreen()
                    TimerScreen.Logs -> LogsScreen()
                }
            }
        }
    }
}

@Composable
fun TimerBottomBar(
    screens: List<TimerScreen>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        modifier = Modifier.height(dimensionResource(R.dimen.navigation_bar_height))
    ) {
        screens.forEachIndexed { index, screen ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        painter = painterResource(id = screen.iconId),
                        contentDescription = screen.name
                    )
                },
            )
        }
    }
}

@Preview
@Composable
fun TimerAppPreview() {
    TimerTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            TimerApp()
        }
    }
}
