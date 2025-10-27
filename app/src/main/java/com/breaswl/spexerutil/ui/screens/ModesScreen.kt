package com.breaswl.spexerutil.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.breaswl.spexerutil.data.BreathingMode
import com.breaswl.spexerutil.ui.components.BackgroundParticles
import com.breaswl.spexerutil.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModesScreen(
    selectedMode: BreathingMode,
    onModeSelected: (BreathingMode) -> Unit,
    onNavigateBack: () -> Unit
) {
    
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
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Choose Mode",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Mode cards
            BreathingMode.getAllModes().forEach { mode ->
                ModeCard(
                    mode = mode,
                    isSelected = selectedMode.id == mode.id,
                    onClick = { onModeSelected(mode) }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ModeCard(
    mode: BreathingMode,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(MaterialTheme.shapes.large)
            .background(
                if (isSelected) {
                    Brush.horizontalGradient(
                        colors = listOf(SpherePink1.copy(alpha = 0.3f), SpherePink2.copy(alpha = 0.3f))
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(BackgroundMid.copy(alpha = 0.5f), BackgroundDeep.copy(alpha = 0.5f))
                    )
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon circle
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) {
                            Brush.radialGradient(
                                colors = listOf(SpherePink1, SpherePink2)
                            )
                        } else {
                            Brush.radialGradient(
                                colors = listOf(BackgroundDeep, BackgroundMid)
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mode.icon,
                    fontSize = 48.sp
                )
            }
            
            Spacer(modifier = Modifier.width(24.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = mode.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = mode.description,
                    fontSize = 14.sp,
                    color = TextGray
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "${mode.inhale}-${mode.hold}-${mode.exhale}${if (mode.holdAfterExhale > 0) "-${mode.holdAfterExhale}" else ""}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) AccentCyan else TextGray
                )
            }
        }
    }
}

