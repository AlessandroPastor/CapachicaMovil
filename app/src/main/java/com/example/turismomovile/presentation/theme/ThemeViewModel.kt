package com.example.turismomovile.presentation.theme

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ThemeViewModel(private val dataStore: DataStore<Preferences>) : ViewModel() {
    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode_enabled")
    }

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    init {
        viewModelScope.launch {
            dataStore.data
                .map { preferences -> preferences[DARK_MODE_KEY] ?: false }
                .collect { isDark ->
                    _isDarkMode.value = isDark
                }
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                val current = preferences[DARK_MODE_KEY] ?: false
                preferences[DARK_MODE_KEY] = !current
            }
        }
    }
}