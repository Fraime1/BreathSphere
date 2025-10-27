package com.breaswl.spexerutil

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.breaswl.spexerutil.navigation.AppNavigation
import com.breaswl.spexerutil.ui.theme.BreathSphereTheme
import com.breaswl.spexerutil.viewmodel.BreathingViewModel

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: BreathingViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private var mediaPlayer: MediaPlayer? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Make status bar with white text
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false // White text on dark background
            isAppearanceLightNavigationBars = false // White text on dark background
        }
        
        // Set status bar color to transparent (using modern API)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
        }
        
        // Initialize SharedPreferences for music
        sharedPreferences = getSharedPreferences("breath_sphere_prefs", Context.MODE_PRIVATE)
        
        viewModel = BreathingViewModel(applicationContext)
        
        // Start music if enabled
        if (isMusicEnabled()) {
            startBackgroundMusic()
        }
        
        setContent {
            BreathSphereTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StatusBarProtection()
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        viewModel = viewModel,
                        onMusicToggle = { enabled ->
                            setMusicEnabled(enabled)
                        }
                    )
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer?.start()
    }
    
    private fun isMusicEnabled(): Boolean {
        return sharedPreferences.getBoolean("background_music_enabled", false)
    }
    
    private fun startBackgroundMusic() {
        if (mediaPlayer == null) {
            try {
                mediaPlayer = MediaPlayer.create(this, com.breaswl.spexerutil.R.raw.back_music).apply {
                    isLooping = true
                    setVolume(0.3f, 0.3f)
                    start()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private fun stopBackgroundMusic() {
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null
    }
    
    fun setMusicEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("background_music_enabled", enabled).apply()
        if (enabled) {
            startBackgroundMusic()
        } else {
            stopBackgroundMusic()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopBackgroundMusic()
    }
}

@Composable
private fun StatusBarProtection() {
    val density = LocalDensity.current
    androidx.compose.foundation.Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val statusBarHeight = 50.dp.toPx() // Fixed height for status bar
        val gradient = androidx.compose.ui.graphics.Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0D0D1A).copy(alpha = 1f),
                Color(0xFF0D0D1A).copy(alpha = 0.8f),
                Color.Transparent
            ),
            startY = 0f,
            endY = statusBarHeight * 2
        )
        drawRect(
            brush = gradient,
            size = androidx.compose.ui.geometry.Size(size.width, statusBarHeight * 2)
        )
    }
}
