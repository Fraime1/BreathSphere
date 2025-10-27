package com.breaswl.spexerutil.data

data class BreathingMode(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val inhale: Int,
    val hold: Int,
    val exhale: Int,
    val holdAfterExhale: Int = 0
) {
    companion object {
        val SLEEP = BreathingMode(
            id = "sleep",
            name = "Sleep",
            description = "Deep relaxation for better sleep",
            icon = "🌙",
            inhale = 4,
            hold = 7,
            exhale = 8,
            holdAfterExhale = 0
        )
        
        val FOCUS = BreathingMode(
            id = "focus",
            name = "Focus",
            description = "Sharp mind and concentration",
            icon = "⚡",
            inhale = 3,
            hold = 3,
            exhale = 3,
            holdAfterExhale = 3
        )
        
        val RELAX = BreathingMode(
            id = "relax",
            name = "Relax",
            description = "Balanced breathing for calm",
            icon = "😌",
            inhale = 5,
            hold = 0,
            exhale = 5,
            holdAfterExhale = 0
        )
        
        fun getAllModes() = listOf(SLEEP, FOCUS, RELAX)
    }
}

