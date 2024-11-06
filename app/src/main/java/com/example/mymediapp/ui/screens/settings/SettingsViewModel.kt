package com.example.mymediapp.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mymediapp.preferences.UserPreferences
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow

class SettingsViewModel(private val userPreferences: UserPreferences) : ViewModel() {

    val darkModeFlow: Flow<Boolean> = userPreferences.darkModeFlow

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setDarkMode(enabled)
        }
    }

    // Factory for SettingsViewModel
    class Factory(private val userPreferences: UserPreferences) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SettingsViewModel(userPreferences) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
