package com.example.mymediapp.preferences

import android.content.Context
import androidx.compose.runtime.compositionLocalOf
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

val LocalUserPreferences = compositionLocalOf<UserPreferences> {
    error("No UserPreferences provided")
}
class UserPreferences(context: Context) {
    private val dataStore = context.dataStore
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")



    val darkModeFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[DARK_MODE_KEY] ?: false // Default er lys modus (false)
        }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }
}
