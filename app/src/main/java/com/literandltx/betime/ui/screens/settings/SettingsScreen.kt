package com.literandltx.betime.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.outlined.RadioButtonChecked
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.literandltx.betime.R
import com.literandltx.betime.data.preferences.AppThemeMode
import com.literandltx.betime.data.preferences.AppThemeModeProvider
import com.literandltx.betime.ui.AppViewModelProvider
import com.literandltx.betime.ui.shared.SectionHeader
import com.literandltx.betime.ui.theme.TimerTheme

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val currentTheme by viewModel.themeMode.collectAsStateWithLifecycle()
    val isBottomNavEnabled by viewModel.isBottomNavEnabled.collectAsStateWithLifecycle()
    val isBottomLabelButtonEnabled by viewModel.isBottomLabelButtonEnabled.collectAsStateWithLifecycle()
    val isBottomTimerButtonEnabled by viewModel.isBottomTimerButtonEnabled.collectAsStateWithLifecycle()
    val isLogsFabEnabled by viewModel.isLogsFabEnabled.collectAsStateWithLifecycle()
    val isTimerDotEnabled by viewModel.isTimerDotEnabled.collectAsStateWithLifecycle()
    val isTimerHintsEnabled by viewModel.isTimerHintsEnabled.collectAsStateWithLifecycle()
    val isTimerStartIconEnabled by viewModel.isTimerStartIconEnabled.collectAsStateWithLifecycle()
    val isTimerDoubleClickEnabled by viewModel.isTimerDoubleClickEnabled.collectAsStateWithLifecycle()
    val isTimerSwipeBottomEnabled by viewModel.isTimerSwipeBottomEnabled.collectAsStateWithLifecycle()

    SettingsScreenContent(
        currentTheme = currentTheme,
        onThemeSelected = viewModel::setThemeMode,
        isBottomNavEnabled = isBottomNavEnabled,
        isBottomLabelButtonEnabled = isBottomLabelButtonEnabled,
        isBottomTimerButtonEnabled = isBottomTimerButtonEnabled,
        isLogsFabEnabled = isLogsFabEnabled,
        isTimerDotEnabled = isTimerDotEnabled,
        isTimerHintsEnabled = isTimerHintsEnabled,
        isTimerStartIconEnabled = isTimerStartIconEnabled,
        isTimerDoubleClickEnabled = isTimerDoubleClickEnabled,
        isTimerSwipeBottomEnabled = isTimerSwipeBottomEnabled,
        onBottomNavChanged = viewModel::setBottomNavEnabled,
        onBottomLabelButtonEnabled = viewModel::setBottomLabelButtonEnabled,
        onBottomTimerButtonEnabled = viewModel::setBottomTimerButtonEnabled,
        onLogsFabChanged = viewModel::setLogsFabEnabled,
        onTimerDotEnabledChanged = viewModel::setTimerDotEnabled,
        onTimerHintsEnabledChanged = viewModel::setTimerHintsEnabled,
        onTimerStartIconEnabledChanged = viewModel::setTimerStartIconEnabled,
        onTimerDoubleClickEnabledChanged = viewModel::setTimerDoubleClickEnabled,
        onTimerSwipeBottomEnabledChanged = viewModel::setTimerSwipeBottomEnabled,
        modifier = modifier
    )
}

@Composable
fun SettingsScreenContent(
    currentTheme: AppThemeMode,
    onThemeSelected: (AppThemeMode) -> Unit,
    isBottomNavEnabled: Boolean,
    isBottomLabelButtonEnabled: Boolean,
    isBottomTimerButtonEnabled: Boolean,
    isLogsFabEnabled: Boolean,
    isTimerDotEnabled: Boolean,
    isTimerHintsEnabled: Boolean,
    isTimerStartIconEnabled: Boolean,
    isTimerDoubleClickEnabled: Boolean,
    isTimerSwipeBottomEnabled: Boolean,
    onBottomNavChanged: (Boolean) -> Unit,
    onBottomLabelButtonEnabled: (Boolean) -> Unit,
    onBottomTimerButtonEnabled: (Boolean) -> Unit,
    onLogsFabChanged: (Boolean) -> Unit,
    onTimerDotEnabledChanged: (Boolean) -> Unit,
    onTimerHintsEnabledChanged: (Boolean) -> Unit,
    onTimerStartIconEnabledChanged: (Boolean) -> Unit,
    onTimerDoubleClickEnabledChanged: (Boolean) -> Unit,
    onTimerSwipeBottomEnabledChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = innerPadding.calculateBottomPadding())
                .padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 12.dp,
                    bottom = 24.dp
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsHeader()

            AppearanceSection(
                currentTheme = currentTheme,
                onThemeSelected = onThemeSelected
            )

            PreferencesSection(
                isBottomNavEnabled = isBottomNavEnabled,
                isBottomTimerButtonEnabled = isBottomTimerButtonEnabled,
                isBottomLabelButtonEnabled = isBottomLabelButtonEnabled,
                isLogsFabEnabled = isLogsFabEnabled,
                isTimerDotEnabled = isTimerDotEnabled,
                isTimerHintsEnabled = isTimerHintsEnabled,
                isTimerStartIconEnabled = isTimerStartIconEnabled,
                isTimerDoubleClickEnabled = isTimerDoubleClickEnabled,
                isTimerSwipeBottomEnabled = isTimerSwipeBottomEnabled,
                onBottomNavChanged = onBottomNavChanged,
                onBottomTimerButtonEnabled = onBottomTimerButtonEnabled,
                onBottomLabelButtonEnabled = onBottomLabelButtonEnabled,
                onLogsFabChanged = onLogsFabChanged,
                onTimerDotEnabledChanged = onTimerDotEnabledChanged,
                onTimerHintsEnabledChanged = onTimerHintsEnabledChanged,
                onTimerStartIconEnabledChanged = onTimerStartIconEnabledChanged,
                onTimerDoubleClickEnabledChanged = onTimerDoubleClickEnabledChanged,
                onTimerSwipeBottomEnabledChanged = onTimerSwipeBottomEnabledChanged
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppearanceSection(
    currentTheme: AppThemeMode,
    onThemeSelected: (AppThemeMode) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val themeLabel = when (currentTheme) {
        AppThemeMode.SYSTEM -> stringResource(R.string.themeSystemDefault)
        AppThemeMode.LIGHT -> stringResource(R.string.themeLight)
        AppThemeMode.DARK -> stringResource(R.string.themeDark)
        AppThemeMode.DISCORD -> stringResource(R.string.themeDiscord)
        AppThemeMode.LIME -> stringResource(R.string.themeLime)
    }

    SettingsSection(title = stringResource(R.string.settingsAppearanceTitle)) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            ListItem(
                modifier = Modifier
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        enabled = true
                    )
                    .clickable { expanded = true },
                headlineContent = { Text(stringResource(R.string.appThemeTitle)) },
                supportingContent = { Text(themeLabel) },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.DarkMode,
                        contentDescription = null
                    )
                },
                trailingContent = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                AppThemeMode.entries.forEach { mode ->
                    val text = when (mode) {
                        AppThemeMode.SYSTEM -> stringResource(R.string.themeSystemDefault)
                        AppThemeMode.LIGHT -> stringResource(R.string.themeLight)
                        AppThemeMode.DARK -> stringResource(R.string.themeDark)
                        AppThemeMode.DISCORD -> stringResource(R.string.themeDiscord)
                        AppThemeMode.LIME -> stringResource(R.string.themeLime)
                    }
                    ThemeMenuItem(
                        text = text,
                        isSelected = currentTheme == mode,
                        onClick = {
                            onThemeSelected(mode)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PreferencesSection(
    isBottomNavEnabled: Boolean,
    isBottomTimerButtonEnabled: Boolean,
    isBottomLabelButtonEnabled: Boolean,
    isLogsFabEnabled: Boolean,
    isTimerDotEnabled: Boolean,
    isTimerHintsEnabled: Boolean,
    isTimerStartIconEnabled: Boolean,
    isTimerDoubleClickEnabled: Boolean,
    isTimerSwipeBottomEnabled: Boolean,
    onBottomNavChanged: (Boolean) -> Unit,
    onBottomTimerButtonEnabled: (Boolean) -> Unit,
    onBottomLabelButtonEnabled: (Boolean) -> Unit,
    onLogsFabChanged: (Boolean) -> Unit,
    onTimerDotEnabledChanged: (Boolean) -> Unit,
    onTimerHintsEnabledChanged: (Boolean) -> Unit,
    onTimerStartIconEnabledChanged: (Boolean) -> Unit,
    onTimerDoubleClickEnabledChanged: (Boolean) -> Unit,
    onTimerSwipeBottomEnabledChanged: (Boolean) -> Unit
) {
    SettingsSection(title = stringResource(R.string.settingsPreferencesTitle)) {
        PreferenceSwitchItem(
            title = stringResource(R.string.bottomNavigationTitle),
            subtitle = stringResource(R.string.bottomNavigationSubtitle),
            icon = Icons.Outlined.Navigation,
            checked = isBottomNavEnabled,
            onCheckedChange = onBottomNavChanged
        )
        PreferenceDivider()
        PreferenceSwitchItem(
            title = stringResource(R.string.timerChipTitle),
            subtitle = stringResource(R.string.timerChipSubtitle),
            icon = Icons.Outlined.Home,
            checked = isBottomTimerButtonEnabled,
            onCheckedChange = onBottomTimerButtonEnabled
        )
        PreferenceDivider()
        PreferenceSwitchItem(
            title = stringResource(R.string.labelChipTitle),
            subtitle = stringResource(R.string.labelChipSubtitle),
            icon = Icons.Outlined.Home,
            checked = isBottomLabelButtonEnabled,
            onCheckedChange = onBottomLabelButtonEnabled
        )
        PreferenceDivider()
        PreferenceSwitchItem(
            title = stringResource(R.string.historyAddButtonFabTitle),
            subtitle = stringResource(R.string.historyAddButtonFabSubtitle),
            icon = Icons.Outlined.AddCircle,
            checked = isLogsFabEnabled,
            onCheckedChange = onLogsFabChanged
        )
        PreferenceDivider()
        PreferenceSwitchItem(
            title = stringResource(R.string.timerProgressDotTitle),
            subtitle = stringResource(R.string.timerProgressDotSubtitle),
            icon = Icons.Outlined.RadioButtonChecked,
            checked = isTimerDotEnabled,
            onCheckedChange = onTimerDotEnabledChanged
        )
        PreferenceDivider()
        PreferenceSwitchItem(
            title = stringResource(R.string.timerActionHintsTitle),
            subtitle = stringResource(R.string.timerActionHintsSubtitle),
            icon = Icons.Outlined.Info,
            checked = isTimerHintsEnabled,
            onCheckedChange = onTimerHintsEnabledChanged
        )
        PreferenceDivider()
        PreferenceSwitchItem(
            title = stringResource(R.string.timerStartIndicatorTitle),
            subtitle = stringResource(R.string.timerStartIndicatorSubtitle),
            icon = Icons.Outlined.HourglassEmpty,
            checked = isTimerStartIconEnabled,
            onCheckedChange = onTimerStartIconEnabledChanged
        )
        PreferenceDivider()
        PreferenceSwitchItem(
            title = stringResource(R.string.timerDoubleClickTitle),
            subtitle = stringResource(R.string.timerDoubleClickSubtitle),
            icon = Icons.Outlined.TouchApp,
            checked = isTimerDoubleClickEnabled,
            onCheckedChange = onTimerDoubleClickEnabledChanged
        )
        PreferenceDivider()
        PreferenceSwitchItem(
            title = stringResource(R.string.timerSwipeDownTitle),
            subtitle = stringResource(R.string.timerSwipeDownSubtitle),
            icon = Icons.Outlined.ArrowDownward,
            checked = isTimerSwipeBottomEnabled,
            onCheckedChange = onTimerSwipeBottomEnabledChanged
        )
    }
}

@Composable
private fun PreferenceSwitchItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    )
}

@Composable
private fun PreferenceDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 8.dp)
        )
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun ThemeMenuItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = { Text(text) },
        onClick = onClick,
        trailingIcon = {
            AnimatedVisibility(visible = isSelected) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview(
    @PreviewParameter(AppThemeModeProvider::class) themeMode: AppThemeMode
) {
    TimerTheme(themeMode = themeMode) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.padding(4.dp)) {
                SettingsScreenContent(
                    currentTheme = themeMode,
                    onThemeSelected = {},
                    isBottomNavEnabled = true,
                    isBottomLabelButtonEnabled = false,
                    isBottomTimerButtonEnabled = true,
                    isLogsFabEnabled = false,
                    isTimerDotEnabled = true,
                    isTimerHintsEnabled = true,
                    isTimerStartIconEnabled = true,
                    isTimerDoubleClickEnabled = true,
                    isTimerSwipeBottomEnabled = true,
                    onBottomNavChanged = {},
                    onBottomTimerButtonEnabled = {},
                    onBottomLabelButtonEnabled = {},
                    onLogsFabChanged = {},
                    onTimerDotEnabledChanged = {},
                    onTimerHintsEnabledChanged = {},
                    onTimerStartIconEnabledChanged = {},
                    onTimerDoubleClickEnabledChanged = {},
                    onTimerSwipeBottomEnabledChanged = {},
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
