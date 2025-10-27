package com.breaswl.spexerutil.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.breaswl.spexerutil.audio.MusicManager
import com.breaswl.spexerutil.data.BreathingMode
import com.breaswl.spexerutil.data.BreathingSession
import com.breaswl.spexerutil.data.SessionRepository
import com.breaswl.spexerutil.data.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BreathingViewModel(context: Context) : ViewModel() {
    private val sessionRepository = SessionRepository(context)
    private val preferencesRepository = UserPreferencesRepository(context)
    private val musicManager = MusicManager(context)
    
    private val _selectedMode = MutableStateFlow(BreathingMode.RELAX)
    val selectedMode: StateFlow<BreathingMode> = _selectedMode.asStateFlow()
    
    private val _selectedTheme = MutableStateFlow("Pink")
    val selectedTheme: StateFlow<String> = _selectedTheme.asStateFlow()
    
    private val _backgroundMusicEnabled = MutableStateFlow(false)
    val backgroundMusicEnabled: StateFlow<Boolean> = _backgroundMusicEnabled.asStateFlow()
    
    init {
        viewModelScope.launch {
            preferencesRepository.getSelectedMode().collect { modeId ->
                _selectedMode.value = BreathingMode.getAllModes().find { it.id == modeId } ?: BreathingMode.RELAX
            }
        }
        
        viewModelScope.launch {
            preferencesRepository.getTheme().collect { theme ->
                _selectedTheme.value = theme
            }
        }
        
        viewModelScope.launch {
            preferencesRepository.getBackgroundMusic().collect { enabled ->
                _backgroundMusicEnabled.value = enabled
            }
        }
    }
    
    fun selectMode(mode: BreathingMode) {
        _selectedMode.value = mode
        viewModelScope.launch {
            preferencesRepository.setSelectedMode(mode.id)
        }
    }
    
    fun setTheme(theme: String) {
        _selectedTheme.value = theme
        viewModelScope.launch {
            preferencesRepository.setTheme(theme)
        }
    }
    
    fun setBackgroundMusic(enabled: Boolean) {
        _backgroundMusicEnabled.value = enabled
        viewModelScope.launch {
            preferencesRepository.setBackgroundMusic(enabled)
        }
    }
    
    fun startBackgroundMusic() {
        if (_backgroundMusicEnabled.value) {
            musicManager.startMusic()
        }
    }
    
    fun pauseBackgroundMusic() {
        if (_backgroundMusicEnabled.value) {
            musicManager.pauseMusic()
        }
    }
    
    fun getMusicManager() = musicManager
    
    fun saveSession(durationMinutes: Int) {
        viewModelScope.launch {
            val session = BreathingSession(
                timestamp = System.currentTimeMillis(),
                modeId = _selectedMode.value.id,
                durationMinutes = durationMinutes,
                date = sessionRepository.getTodayDate()
            )
            sessionRepository.saveSession(session)
        }
    }
    
    fun getSessionRepository() = sessionRepository
    
    override fun onCleared() {
        super.onCleared()
        musicManager.cleanup()
    }
}

