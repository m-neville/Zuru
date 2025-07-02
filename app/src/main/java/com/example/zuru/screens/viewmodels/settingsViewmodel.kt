// viewmodels/SettingsViewModel.kt
package com.example.zuru.screens.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.zuru.screens.preferences.SettingsRepository
import com.example.zuru.screens.preferences.dataStore
import com.example.zuru.ui.theme.AppThemeIdentifier
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository = SettingsRepository(application.dataStore)

    val isDarkMode: StateFlow<Boolean> = settingsRepository.isDarkModeEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val selectedThemeIdentifier: StateFlow<AppThemeIdentifier> = settingsRepository.selectedThemeIdentifier
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppThemeIdentifier.DEFAULT)

    fun setDarkMode(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkModeEnabled(isEnabled)
        }
    }

    fun setSelectedTheme(themeIdentifier: AppThemeIdentifier) {
        viewModelScope.launch {
            settingsRepository.setSelectedThemeIdentifier(themeIdentifier)
        }
    }
}