package com.literandltx.betime.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.literandltx.betime.ui.screens.activity.widgets.WidgetType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsPreferences(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        // Appearance
        val DEFAULT_THEME_MODE = AppThemeMode.SYSTEM

        // Navigation & Controls
        const val DEFAULT_BOTTOM_NAV_ENABLED = false
        const val DEFAULT_BOTTOM_TIMER_BUTTON_ENABLED = true
        const val DEFAULT_BOTTOM_LABEL_BUTTON_ENABLED = true
        const val DEFAULT_LOGS_FAB_ENABLED = false

        // Timer
        const val DEFAULT_TIMER_DOT_ENABLED = false
        const val DEFAULT_TIMER_HINTS_ENABLED = true
        const val DEFAULT_TIMER_START_ICON_ENABLED = true
        const val DEFAULT_TIMER_DOUBLE_CLICK_ENABLED = false
        const val DEFAULT_TIMER_SWIPE_BOTTOM_ENABLED = true

        // Activity Dashboard
        val DEFAULT_ACTIVITY_WIDGET_ORDER: List<WidgetType> = WidgetType.entries
    }

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val BOTTOM_NAV_ENABLED = booleanPreferencesKey("bottom_nav_enabled")
        val BOTTOM_TIMER_BUTTON_ENABLED = booleanPreferencesKey("bottom_timer_button_enabled")
        val BOTTOM_LABEL_BUTTON_ENABLED = booleanPreferencesKey("bottom_label_button_enabled")
        val LOGS_FAB_ENABLED = booleanPreferencesKey("logs_fab_enabled")
        val ACTIVITY_WIDGET_ORDER = stringPreferencesKey("activity_widget_order")
        val TIMER_DOT_ENABLED = booleanPreferencesKey("timer_dot_enabled")
        val TIMER_HINTS_ENABLED = booleanPreferencesKey("timer_hints_enabled")
        val TIMER_START_ICON_ENABLED = booleanPreferencesKey("timer_start_icon_enabled")
        val TIMER_DOUBLE_CLICK_ENABLED = booleanPreferencesKey("timer_double_click_enabled")
        val TIMER_SWIPE_BOTTOM_ENABLED = booleanPreferencesKey("timer_swipe_bottom_enabled")
    }

    val themeModeFlow: Flow<AppThemeMode> = dataStore.data
        .map { preferences ->
            val modeName = preferences[Keys.THEME_MODE] ?: DEFAULT_THEME_MODE.name
            try {
                AppThemeMode.valueOf(modeName)
            } catch (_: IllegalArgumentException) {
                DEFAULT_THEME_MODE
            }
        }

    val bottomNavEnabledFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[Keys.BOTTOM_NAV_ENABLED] ?: DEFAULT_BOTTOM_NAV_ENABLED
        }

    val bottomTimerButtonEnabledFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[Keys.BOTTOM_TIMER_BUTTON_ENABLED] ?: DEFAULT_BOTTOM_TIMER_BUTTON_ENABLED
        }

    val bottomLabelButtonEnabledFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[Keys.BOTTOM_LABEL_BUTTON_ENABLED] ?: DEFAULT_BOTTOM_LABEL_BUTTON_ENABLED
        }

    val logsFabEnabledFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[Keys.LOGS_FAB_ENABLED] ?: DEFAULT_LOGS_FAB_ENABLED
        }

    val timerDotEnabledFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[Keys.TIMER_DOT_ENABLED] ?: DEFAULT_TIMER_DOT_ENABLED
        }

    val timerHintsEnabledFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[Keys.TIMER_HINTS_ENABLED] ?: DEFAULT_TIMER_HINTS_ENABLED
        }

    val activityWidgetOrderFlow: Flow<List<WidgetType>> = dataStore.data
        .map { preferences ->
            val savedOrder = preferences[Keys.ACTIVITY_WIDGET_ORDER]
            if (savedOrder.isNullOrBlank()) {
                DEFAULT_ACTIVITY_WIDGET_ORDER
            } else {
                val parsed = savedOrder.split(",").mapNotNull { name ->
                    WidgetType.entries.find { it.name.equals(name, ignoreCase = true) }
                }
                val missing = DEFAULT_ACTIVITY_WIDGET_ORDER.filter { it !in parsed }
                (parsed + missing).ifEmpty { DEFAULT_ACTIVITY_WIDGET_ORDER }
            }
        }

    val timerStartIconEnabledFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[Keys.TIMER_START_ICON_ENABLED] ?: DEFAULT_TIMER_START_ICON_ENABLED
        }

    val timerDoubleClickEnabledFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[Keys.TIMER_DOUBLE_CLICK_ENABLED] ?: DEFAULT_TIMER_DOUBLE_CLICK_ENABLED
        }

    val timerSwipeBottomEnabledFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[Keys.TIMER_SWIPE_BOTTOM_ENABLED] ?: DEFAULT_TIMER_SWIPE_BOTTOM_ENABLED
        }

    suspend fun setTimerStartIconEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.TIMER_START_ICON_ENABLED] = enabled }
    }

    suspend fun setTimerDoubleClickEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.TIMER_DOUBLE_CLICK_ENABLED] = enabled }
    }

    suspend fun setTimerSwipeBottomEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.TIMER_SWIPE_BOTTOM_ENABLED] = enabled }
    }

    suspend fun setThemeMode(mode: AppThemeMode) {
        dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setBottomNavEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.BOTTOM_NAV_ENABLED] = enabled }
    }

    suspend fun setBottomTimerButtonEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.BOTTOM_TIMER_BUTTON_ENABLED] = enabled }
    }

    suspend fun setBottomLabelButtonEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.BOTTOM_LABEL_BUTTON_ENABLED] = enabled }
    }

    suspend fun setLogsFabEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.LOGS_FAB_ENABLED] = enabled }
    }

    suspend fun setTimerDotEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.TIMER_DOT_ENABLED] = enabled }
    }

    suspend fun setTimerHintsEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.TIMER_HINTS_ENABLED] = enabled }
    }

    suspend fun setActivityWidgetOrder(order: List<WidgetType>) {
        dataStore.edit { preferences ->
            preferences[Keys.ACTIVITY_WIDGET_ORDER] = order.joinToString(",") { it.name }
        }
    }

    suspend fun resetToDefaults() {
        dataStore.edit { it.clear() }
    }
}
