package com.breaswl.spexerutil.data

data class BreathingSession(
    val timestamp: Long,
    val modeId: String,
    val durationMinutes: Int,
    val date: String // Format: yyyy-MM-dd
)

