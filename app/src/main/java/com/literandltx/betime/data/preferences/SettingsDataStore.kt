package com.literandltx.betime.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

const val PREFERENCE_DATA_STORE_KEY = "settings"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCE_DATA_STORE_KEY)
