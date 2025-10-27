package com.breaswl.spexerutil.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.preferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {
    private val themeKey = stringPreferencesKey("theme")
    private val backgroundMusicKey = booleanPreferencesKey("background_music")
    private val selectedModeKey = stringPreferencesKey("selected_mode")
    
    suspend fun setTheme(theme: String) {
        context.preferencesDataStore.edit { preferences ->
            preferences[themeKey] = theme
        }
    }
    
    fun getTheme(): Flow<String> = context.preferencesDataStore.data.map { preferences ->
        preferences[themeKey] ?: "Pink"
    }
    
    suspend fun setBackgroundMusic(enabled: Boolean) {
        context.preferencesDataStore.edit { preferences ->
            preferences[backgroundMusicKey] = enabled
        }
    }
    
    fun getBackgroundMusic(): Flow<Boolean> = context.preferencesDataStore.data.map { preferences ->
        preferences[backgroundMusicKey] ?: false
    }
    
    suspend fun setSelectedMode(modeId: String) {
        context.preferencesDataStore.edit { preferences ->
            preferences[selectedModeKey] = modeId
        }
    }
    
    fun getSelectedMode(): Flow<String> = context.preferencesDataStore.data.map { preferences ->
        preferences[selectedModeKey] ?: BreathingMode.RELAX.id
    }
}

