// preferences/SettingsDataStore.kt (create this file)
package com.example.zuru.screens.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.zuru.ui.theme.AppThemeIdentifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.text.get

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    private object PreferencesKeys {
        val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
        val SELECTED_THEME_ID = stringPreferencesKey("selected_theme_id")
    }

    val isDarkModeEnabled: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.DARK_MODE_ENABLED] ?: false // Default to false
        }

    suspend fun setDarkModeEnabled(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE_ENABLED] = isEnabled
        }
    }

    val selectedThemeIdentifier: Flow<AppThemeIdentifier> = dataStore.data
        .map { preferences ->
            val themeIdString = preferences[PreferencesKeys.SELECTED_THEME_ID] ?: AppThemeIdentifier.DEFAULT.name
            try {
                AppThemeIdentifier.valueOf(themeIdString)
            } catch (e: IllegalArgumentException) {
                AppThemeIdentifier.DEFAULT // Fallback to default if stored value is invalid
            }
        }

    suspend fun setSelectedThemeIdentifier(themeIdentifier: AppThemeIdentifier) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_THEME_ID] = themeIdentifier.name
        }
    }
}