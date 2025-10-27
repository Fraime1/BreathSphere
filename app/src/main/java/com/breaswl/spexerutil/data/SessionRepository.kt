package com.breaswl.spexerutil.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "breathing_sessions")

class SessionRepository(private val context: Context) {
    private val sessionsKey = stringPreferencesKey("sessions")
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    
    suspend fun saveSession(session: BreathingSession) {
        context.dataStore.edit { preferences ->
            val currentSessions = preferences[sessionsKey] ?: ""
            val sessionString = "${session.timestamp},${session.modeId},${session.durationMinutes},${session.date}"
            val updatedSessions = if (currentSessions.isEmpty()) {
                sessionString
            } else {
                "$currentSessions;$sessionString"
            }
            preferences[sessionsKey] = updatedSessions
        }
    }
    
    fun getSessions(): Flow<List<BreathingSession>> = context.dataStore.data.map { preferences ->
        val sessionsString = preferences[sessionsKey] ?: ""
        if (sessionsString.isEmpty()) {
            emptyList()
        } else {
            sessionsString.split(";").mapNotNull { sessionStr ->
                try {
                    val parts = sessionStr.split(",")
                    BreathingSession(
                        timestamp = parts[0].toLong(),
                        modeId = parts[1],
                        durationMinutes = parts[2].toInt(),
                        date = parts[3]
                    )
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
    
    fun getTodayDate(): String = dateFormat.format(Date())
}

