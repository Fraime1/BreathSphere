package com.breaswl.spexerutil.audio

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.*
import kotlinx.coroutines.delay

class MusicManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var isPaused = false
    
    fun startMusic() {
        if (!isPlaying && !isPaused) {
            try {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer.create(context, com.breaswl.spexerutil.R.raw.back_music).apply {
                    isLooping = true
                    setVolume(0.3f, 0.3f) // Low volume for background
                    start()
                }
                isPlaying = true
                isPaused = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (isPaused) {
            resumeMusic()
        }
    }
    
    fun stopMusic() {
        if (isPlaying || isPaused) {
            mediaPlayer?.apply {
                stop()
                release()
            }
            mediaPlayer = null
            isPlaying = false
            isPaused = false
        }
    }
    
    fun pauseMusic() {
        if (isPlaying) {
            mediaPlayer?.pause()
            isPlaying = false
            isPaused = true
        }
    }
    
    fun resumeMusic() {
        if (isPaused) {
            mediaPlayer?.start()
            isPlaying = true
            isPaused = false
        }
    }
    
    fun isMusicPlaying(): Boolean = isPlaying
    fun isMusicPaused(): Boolean = isPaused
    
    fun cleanup() {
        stopMusic()
    }
}

@Composable
fun rememberMusicManager(context: Context): MusicManager {
    return remember {
        MusicManager(context)
    }
}
