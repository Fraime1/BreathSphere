package com.breaswl.spexerutil.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.breaswl.spexerutil.data.BreathingMode
import com.breaswl.spexerutil.ui.components.BackgroundParticles
import com.breaswl.spexerutil.ui.components.BreathingPhase
import com.breaswl.spexerutil.ui.components.BreathingSphere
import com.breaswl.spexerutil.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    selectedMode: BreathingMode,
    selectedTheme: String,
    onNavigateToModes: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onSessionComplete: (Int) -> Unit
) {
    var isSessionActive by remember { mutableStateOf(false) }
    var selectedDuration by remember { mutableIntStateOf(5) }
    var remainingTime by remember { mutableIntStateOf(selectedDuration * 60) }
    var currentPhase by remember { mutableStateOf(BreathingPhase.INHALE) }
    val coroutineScope = rememberCoroutineScope()
    
    // Update remaining time when duration changes
    LaunchedEffect(selectedDuration) {
        if (!isSessionActive) {
            remainingTime = selectedDuration * 60
        }
    }
    
    val (sphereColor1, sphereColor2) = when (selectedTheme) {
        "Blue" -> SphereBlue1 to SphereBlue2
        "Green" -> SphereGreen1 to SphereGreen2
        else -> SpherePink1 to SpherePink2
    }
    
    
    LaunchedEffect(isSessionActive) {
        if (isSessionActive) {
            remainingTime = selectedDuration * 60
            while (remainingTime > 0 && isSessionActive) {
                delay(1000)
                remainingTime--
            }
            if (remainingTime == 0) {
                isSessionActive = false
                onSessionComplete(selectedDuration)
            }
        }
    }
    
    // Phase cycling - synchronized with sphere animation
    LaunchedEffect(isSessionActive, selectedMode) {
        if (isSessionActive) {
            while (isSessionActive) {
                currentPhase = BreathingPhase.INHALE
                delay(selectedMode.inhale * 1000L)
                
                if (selectedMode.hold > 0) {
                    currentPhase = BreathingPhase.HOLD
                    delay(selectedMode.hold * 1000L)
                }
                
                currentPhase = BreathingPhase.EXHALE
                delay(selectedMode.exhale * 1000L)
                
                if (selectedMode.holdAfterExhale > 0) {
                    currentPhase = BreathingPhase.HOLD_AFTER_EXHALE
                    delay(selectedMode.holdAfterExhale * 1000L)
                }
            }
        } else {
            currentPhase = BreathingPhase.INHALE
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BackgroundDark, BackgroundMid, BackgroundDeep)
                )
            )
    ) {
        BackgroundParticles()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 32.dp) // Add top padding for status bar
                .padding(bottom = 32.dp), // Add bottom padding for navigation
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateToModes) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Modes",
                        tint = TextWhite
                    )
                }
                
                Text(
                    text = "BreathSphere",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = TextWhite
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Current mode
            Text(
                text = selectedMode.name,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            
            Text(
                text = selectedMode.description,
                fontSize = 16.sp,
                color = TextGray
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Phase indicator
            Text(
                text = when (currentPhase) {
                    BreathingPhase.INHALE -> "Inhale"
                    BreathingPhase.HOLD -> "Hold"
                    BreathingPhase.EXHALE -> "Exhale"
                    BreathingPhase.HOLD_AFTER_EXHALE -> "Hold"
                },
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSessionActive) AccentCyan else TextGray
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Breathing sphere
            BreathingSphere(
                isActive = isSessionActive,
                mode = selectedMode,
                sphereColor1 = sphereColor1,
                sphereColor2 = sphereColor2,
                currentPhase = currentPhase,
                modifier = Modifier.weight(1f)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Timer
            Text(
                text = "${remainingTime / 60}:${String.format("%02d", remainingTime % 60)}",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSessionActive) TextWhite else TextGray
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Duration selection
            if (!isSessionActive) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(1, 3, 5, 10, 20).forEach { duration ->
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selectedDuration == duration) {
                                        Brush.radialGradient(
                                            colors = listOf(sphereColor1, sphereColor2)
                                        )
                                    } else {
                                        Brush.radialGradient(
                                            colors = listOf(
                                                BackgroundMid,
                                                BackgroundDeep
                                            )
                                        )
                                    }
                                )
                                .clickable { selectedDuration = duration },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$duration",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "minutes",
                    fontSize = 14.sp,
                    color = TextGray
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Start/Pause button
            Button(
                onClick = {
                    isSessionActive = !isSessionActive
                },
                modifier = Modifier
                    .size(80.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSessionActive) Color.Red.copy(alpha = 0.7f) else AccentCyan.copy(alpha = 0.7f)
                )
            ) {
                Text(
                    text = if (isSessionActive) "❚❚" else "▶",
                    fontSize = 24.sp,
                    color = TextWhite
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

